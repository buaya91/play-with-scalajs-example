package utils

object Utility {
  def positiveModulo(a: Int, n: Int): Int = {
    // to get positive mod
    // we compute remainder, and add back the n
    // to prevent result bigger than n which violate the rules
    // we take the remainder again
    ((a % n) + n) % n
  }
}
