package shared.framework.domain

class Game[N <: Sequenced, St <: State[N, Dlt], Dlt <: Delta[N]](conf: HyperConfig,
                                                                    initState: St,
                                                                    stepFn: (St, Dlt) => St) {
  private var lastConfirmedState: St      = initState
  private var latestPredictedState: St    = initState
  private var incompleteDelta: Seq[Dlt] = List.empty[Dlt]

  def start(onNext: St => Unit): Unit = {

  }
}
