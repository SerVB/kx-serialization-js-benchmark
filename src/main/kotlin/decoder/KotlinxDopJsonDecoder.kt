package decoder

import kotlinx.serialization.DynamicObjectParser
import kotlinx.serialization.builtins.list
import protocol.ServerEvent

object KotlinxDopJsonDecoder : Decoder {

  private val dynamicObjectParser = DynamicObjectParser(configuration = KotlinxJson.configuration)

  override fun decode(string: String): List<ServerEvent> {
    // don't do KotlinxJsonServerEventSerializer.deserializeList(string) because it's slow on JS
    val nativelyParsedString = JSON.parse<Any>(string)

    return dynamicObjectParser.parse(nativelyParsedString, ServerEvent.serializer().list)
  }
}
