package protocol.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonIntSize(
  @SerialName("a")
  val width: Int,
  @SerialName("b")
  val height: Int
)
