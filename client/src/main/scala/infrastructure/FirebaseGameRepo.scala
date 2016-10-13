package infrastructure

import domain.{GameRepo, GameWorld}
import domain.components._
import firebase.{DataSnapshot, Firebase}
import monix.reactive.{Observable, OverflowStrategy}
import prickle._

import scala.language.implicitConversions
import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.{Dictionary, Dynamic, JSON, isUndefined}
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
    println("child added", db.`val`())
  })

//  def printJs[D](js: Dictionary[D]) = {
//    println("Wat" + JSON.stringify(js))
//  }

  def proceedIfDefined[T](obj: Dynamic)(op: Dynamic => T): Option[T] = {
    if (isUndefined(obj))
      None
    else
      Some(op(obj))
  }

  def castToDict(obj: Dynamic): Option[Dictionary[Dynamic]] =
    proceedIfDefined(obj)(o => o.asInstanceOf[Dictionary[Dynamic]])

  override def init(): Future[GameWorld] = {
    val p: Promise[GameWorld] = Promise()

    dbRoot.once("value", (db: DataSnapshot) => {
      val dbVal: Dynamic = db.`val`()
      if (dbVal == null) {
        p.complete(Success(new GameWorld()))
      } else {

        val rawAreaComponents =
          for {
            areaJs  <- castToDict(dbVal.area)
          } yield {
            val area = areaJs.mapValues(jsObj => Unpickle[Seq[Position]].fromString(jsObj.toString))
            mergeMapOfTry(area)
          }

        val rawIsSnakeComponents =
          for {
            isSnakeJs <- castToDict(dbVal.isSnake)
          } yield {
            val isSnake = isSnakeJs.mapValues(jsObj => Unpickle[Boolean].fromString(jsObj.toString))
            mergeMapOfTry(isSnake)
          }

        val rawSpeedComponents =
          for {
            speedJs <- castToDict(dbVal.speed)
          } yield {
            val speed = speedJs.mapValues(jsObj => Unpickle[Speed].fromString(jsObj.toString))
            mergeMapOfTry(speed)
          }

        val rawDirComponents =
          for {
            dirJs <- castToDict(dbVal.dir)
          } yield {
            val dir = dirJs.mapValues(jsObj => Unpickle[Direction].fromString(jsObj.toString))
            mergeMapOfTry(dir)
          }

        val worldFromDB =
          for {
            area    <- Try(rawAreaComponents.getOrElse(Success(mutable.Map.empty[String, Seq[Position]]))).flatten
            isSnake <- Try(rawIsSnakeComponents.getOrElse(Success(mutable.Map.empty[String, Boolean]))).flatten
            speed   <- Try(rawSpeedComponents.getOrElse(Success(mutable.Map.empty[String, Speed]))).flatten
            dir     <- Try(rawDirComponents.getOrElse(Success(mutable.Map.empty[String, Direction]))).flatten
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
