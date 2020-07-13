package protocol.data

import kotlinx.serialization.Serializable

@Serializable
data class FontDataHolder(
  val fontId: Short,
  val fontData: TtfFontData
)

@Serializable
data class TtfFontData(val ttfBase64: String)
