package infrastructure

import domain.{GameRepo, GameWorld}
import domain.components.{Direction, GlobalEvent, Position, Speed}
import firebase.{DataSnapshot, Firebase}
import monix.reactive.Observable
import prickle._

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.{Dictionary, Dynamic, JSON}
import scala.util.{Failure, Success, Try}

object FirebaseGameRepo extends GameRepo {

  val dbRoot = Firebase.database().ref()

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

  override def subscribeToAllEvents(): Observable[GlobalEvent] = ???

  override def broadcastEvent(ev: GlobalEvent): Unit = ???
}
