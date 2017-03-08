import shared.core.IdentifiedGameInput
import shared._
import scala.collection.SortedMap

package object game {
  type IndexedInputs  = Map[UserID, IdentifiedGameInput]
  type BufferedInputs = SortedMap[FrameNo, IndexedInputs]
}
