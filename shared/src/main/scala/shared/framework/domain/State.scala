package shared.framework.domain

trait State[SeqNo, Dlt <: Delta[SeqNo]] extends Sequenced {
  def step(delta: Dlt): State[SeqNo, Dlt]
}
