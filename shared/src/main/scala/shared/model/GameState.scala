package shared.model

/**
  * @author limqingwei
  */
case class GameState(snakes: Seq[Snake], apples: Set[Apple])

object GameState {
  def init = GameState(Seq.empty, Set.empty)
}
