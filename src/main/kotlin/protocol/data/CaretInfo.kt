package protocol.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CaretInfo(
  @SerialName("a")
  val locationInWindow: Point
)
