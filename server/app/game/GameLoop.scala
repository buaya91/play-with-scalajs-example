package game

import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import shared.core.{GameLogic, IdentifiedGameInput}
import shared.model.GameState
import shared._

object GameLoop {

  /**
    * Run main loop that consume input and emit output at fixed interval
    * It will buffer all input
    */
  // input that's not processed yet
  private var inputBuffers: Seq[IdentifiedGameInput] = Seq.empty

  def start(updateRate: Int = updateRate): Flow[IdentifiedGameInput, GameState, _] = {
    val initState = GameState(Seq.empty, Set.empty)

    val inputSink: Sink[IdentifiedGameInput, _] = Sink.foreach { i =>
      inputBuffers = inputBuffers :+ i
    }

    val gameStateSource: Source[GameState, _] =
      Source
        .actorRef(1000000000, OverflowStrategy.dropBuffer)
        .mapMaterializedValue(ref => {
          while (true) {
            val startTime = System.currentTimeMillis()

            val updated = GameLogic.step(initState, inputBuffers)

            ref ! updated

            inputBuffers = Seq.empty
            val endTime = System.currentTimeMillis()

            val used = endTime - startTime


            println(s"Sleep for: ${millisNeededPerUpdate(updateRate) - used}")
            Thread.sleep(millisNeededPerUpdate(updateRate) - used)
          }
        })

    Flow.fromSinkAndSource(inputSink, gameStateSource)
  }
}
