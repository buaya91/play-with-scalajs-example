package infrastructure

import domain.{GameRepo, GameWorld}
import domain.components._
import firebase.{DataSnapshot, Firebase}
import monix.reactive.{Observable, OverflowStrategy}
import prickle._

import scala.language.implicitConversions
import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.{Dictionary, Dynamic, JSON}
import scala.util.{Failure, Success, Try}

object FirebaseGameRepo extends GameRepo {

  lazy val dbRoot = Firebase.database().ref()
  lazy val areaRoot = dbRoot.child("area")
  lazy val isSnakeRoot = dbRoot.child("isSnake")
  lazy val speedRoot = dbRoot.child("speed")
  lazy val directionRoot = dbRoot.child("direction")

  implicit val customConfig = JsConfig("xx", false)    // todo: test if we could remove it

  implicit def pimpImmutableMap[T](immutable: collection.Map[String, T]): mutable.Map[String, T] =
    mutable.Map(immutable.toSeq:_*)

  def mergeMapOfTry[T](mot: mutable.Map[String, Try[T]]): Try[mutable.Map[String, T]] = {
    mot.foldLeft(Try(mutable.Map[String, T]())) {
      case (Success(acc), (id, Success(direction))) =>
        val added = acc.updated(id, direction)
        Success(added)
      case (Failure(e), _) => Failure(e)
      case (_, (id, Failure(e))) => Failure(e)
    }
  }

  dbRoot.on("child_added", (db: DataSnapshot) => {
    println(db.`val`())
  })

  override def init(): Future[GameWorld] = {
    val p: Promise[GameWorld] = Promise()

    dbRoot.once("value", (db: DataSnapshot) => {
      val dbVal: Dynamic = db.`val`()
      if (dbVal == null) {
        p.complete(Success(new GameWorld()))
      } else {

        val rawAreaComponents = dbVal.area.asInstanceOf[Dictionary[Dynamic]]
        val rawIsSnakeComponents = dbVal.isSnake.asInstanceOf[Dictionary[Dynamic]]
        val rawSpeedComponents = dbVal.speed.asInstanceOf[Dictionary[Dynamic]]
        val rawDirComponents = dbVal.dir.asInstanceOf[Dictionary[Dynamic]]

        val areaComponents = rawAreaComponents
          .mapValues(jsObj => Unpickle[Seq[Position]].fromString(JSON.stringify(jsObj)))

        val isSnakeComponents = rawIsSnakeComponents
          .mapValues(jsObj => Unpickle[Boolean].fromString(JSON.stringify(jsObj)))

        val speedComponents = rawSpeedComponents
          .mapValues(jsObj => Unpickle[Speed].fromString(JSON.stringify(jsObj)))

        val dirComponents = rawDirComponents
          .mapValues(jsObj => Unpickle[Direction].fromString(JSON.stringify(jsObj)))


        val worldFromDB = for {
          area <- mergeMapOfTry(areaComponents)
          isSnake <- mergeMapOfTry(isSnakeComponents)
          speed <- mergeMapOfTry(speedComponents)
          dir <- mergeMapOfTry(dirComponents)
        } yield new GameWorld(area, isSnake, speed, dir)

        p.complete(worldFromDB)
      }
    })

    p.future
  }

  override def subscribeToAllEvents(): Observable[GlobalEvent] = {
//    Observable.create(OverflowStrategy.Unbounded) { sync =>
//
//    }
    ???
  }

  override def broadcastEvent(ev: GlobalEvent): Unit = ev match {
    case SnakeAdded(id, body, dir, spd) =>
      areaRoot.child(id).set(Pickle.intoString(body))
      directionRoot.child(id).set(Pickle.intoString(dir))
      speedRoot.child(id).set(Pickle.intoString(spd))
      isSnakeRoot.child(id).set(Pickle.intoString(true))
    case _ =>
  }
}
