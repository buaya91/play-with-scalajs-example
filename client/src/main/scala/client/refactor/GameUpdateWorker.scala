package client.refactor

import monix.execution.Ack.Continue
import org.scalajs.dom.MessageEvent
import shared.protocol._
import monix.execution.Scheduler.Implicits.global
import boopickle.Default._
import client.Utils
import client.infrastructure.DefaultWSSource
import shared.model
import shared.serializers.Serializers._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object WorkerGlobal extends js.GlobalScope {
  def addEventListener(`type`: String, f: js.Function): Unit = js.native
  def postMessage(data: js.Any): Unit                        = js.native
}

@JSExport("GameUpdateWorker")
object GameUpdateWorker {

  val keyToCmd: PartialFunction[Int, Int => SequencedGameRequest] = {
    case 32 => SpeedUp.apply
    case 37 => ChangeDirection(model.Left, _)
    case 38 => ChangeDirection(model.Up, _)
    case 39 => ChangeDirection(model.Right, _)
    case 40 => ChangeDirection(model.Down, _)
  }
  val gameData = new MutableGameData()
  val gameLoop = new GameLoop(gameData)

  def forwardGameResponseToMainThread(res: GameResponse) = {
    val serialized = Pickle.intoBytes[GameResponse](res)
    val arrayBuf   = Utils.bbToArrayBuffer(serialized)
    WorkerGlobal.postMessage(arrayBuf)
  }

  @JSExport
  def main() = {
    WorkerGlobal.addEventListener("message", onMessage _)

    DefaultWSSource.stream().subscribe { res =>
      res match {
        case st: GameState =>

          gameData.predictedState.lastOption.foreach(p => println(s"Diff ${p._1 - st.seqNo}"))

          gameData.serverStateQueue += st.seqNo -> st
        case assigned @ AssignedID(id) =>
          gameData.assignedID = Some(id)
          forwardGameResponseToMainThread(assigned)
      }
      Continue
    }

    gameLoop
      .start()
      .subscribe(st => {
//        if (gameData.assignedID.exists(id => !st.hasSnake(id))) {
//          println("Clinet no snake")
//        }

        forwardGameResponseToMainThread(st)
        Continue
      })

  }

  def onMessage(msg: MessageEvent) = {
    import gameData._

    msg.data match {
      case key: Int =>
        val inputFn = keyToCmd(key)

        predictedState.lastOption.foreach {
          case (frameNo, _) =>
            val nextK = frameNo + 1
            val i     = inputFn(nextK)
            DefaultWSSource.request(i)
            unackInput = unackInput + (nextK -> i)
        }
      case name: String =>
        DefaultWSSource.request(JoinGame(name))
    }
  }
}
