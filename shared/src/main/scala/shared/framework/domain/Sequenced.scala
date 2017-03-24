package shared.framework.domain

trait Sequenced extends Ordered[Sequenced] {
  def seqNo: Sequenced
}
