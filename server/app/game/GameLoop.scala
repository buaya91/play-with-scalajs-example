package game

import shared.model.GameState

object GameLoop {
  /**
    * Run main loop that consume input and emit output at fixed interval
    * It will buffer all input
    */

  // input that's not processed yet
  private var inputBuffers: Seq[]

  // Gamestate that is sent to client, but not acked
  private var gameStateUnack: Seq[GameState] = Seq.empty

}
