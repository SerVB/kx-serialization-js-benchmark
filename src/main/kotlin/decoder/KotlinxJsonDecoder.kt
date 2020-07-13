package decoder

import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import protocol.ServerEvent

object KotlinxJsonDecoder : Decoder {

  private val json = Json(configuration = KotlinxJson.configuration)

  override fun decode(string: String): List<ServerEvent> {
    return json.parse(ServerEvent.serializer().list, string)
  }
}
