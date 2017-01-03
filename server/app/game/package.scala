import shared.core.IdentifiedGameInput

import scala.collection.SortedMap

package object game {
  type UserID         = String
  type FrameNo        = Int
  type IndexedInputs  = Map[UserID, IdentifiedGameInput]
  type BufferedInputs = SortedMap[FrameNo, IndexedInputs]
}
