package protocol.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PaintType {
  @SerialName("a")
  DRAW,

  @SerialName("b")
  FILL,
}
