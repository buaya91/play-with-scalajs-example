package utils

object Utility {
  def positiveModulo(a: Double, n: Double): Double = {
    // to get positive mod
    // we compute remainder, and add back the n
    // to prevent result bigger than n which violate the rules
    // we take the remainder again
    ((a % n) + n) % n
  }
}
