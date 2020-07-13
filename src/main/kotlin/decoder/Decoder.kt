package decoder

import protocol.ServerEvent

interface Decoder {

  fun decode(string: String): List<ServerEvent>
}
