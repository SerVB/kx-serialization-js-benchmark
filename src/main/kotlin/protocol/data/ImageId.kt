package protocol.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ImageId {

  @Serializable
  @SerialName("a")
  data class BufferedImageId(
    @SerialName("a")
    val identityHash: Int,
    @SerialName("b")
    val stateHash: Int
  ) : ImageId()

  @Serializable
  @SerialName("b")
  data class PVolatileImageId(
    @SerialName("a")
    val id: Long
  ) : ImageId()

  @Serializable
  @SerialName("c")
  data class Unknown(
    @SerialName("a")
    val className: String
  ) : ImageId()
}
