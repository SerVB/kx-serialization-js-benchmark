package protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import protocol.data.*

@Serializable
sealed class ServerEvent

@Serializable
@SerialName("a")
data class ServerImageDataReplyEvent(
  @SerialName("a")
  val imageId: ImageId,
  @SerialName("b")
  val imageData: ImageData
) : ServerEvent()

@Serializable
@SerialName("b")
data class ServerPingReplyEvent(
  /** From connection opening. */
  @SerialName("a")
  val clientTimeStamp: Int,
  /** From connection opening. */
  @SerialName("b")
  val serverReadEventTimeStamp: Int
) : ServerEvent()

@Serializable
@SerialName("c")
data class ServerClipboardEvent(
  @SerialName("a")
  val stringContent: String
) : ServerEvent()

@Serializable
@SerialName("d")
data class ServerWindowSetChangedEvent(
  @SerialName("a")
  val windowDataList: List<WindowData> = emptyList()  // todo: remove default after https://github.com/Kotlin/kotlinx.serialization/issues/806
) : ServerEvent()

@Serializable
@SerialName("e")
data class ServerDrawCommandsEvent(
  @SerialName("a")
  val target: Target,
  @SerialName("b")
  val drawEvents: List<ServerWindowEvent> = emptyList()  // todo: remove default after https://github.com/Kotlin/kotlinx.serialization/issues/806
) : ServerEvent() {

  @Serializable
  sealed class Target {

    @Serializable
    @SerialName("a")
    data class Onscreen(
      @SerialName("a")
      val windowId: Int
    ) : Target()

    @Serializable
    @SerialName("b")
    data class Offscreen(
      @SerialName("a")
      val pVolatileImageId: Long,
      @SerialName("b")
      val width: Int,
      @SerialName("c")
      val height: Int
    ) : Target()
  }
}

@Serializable
@SerialName("f")
data class ServerCaretInfoChangedEvent(
  @SerialName("a")
  val data: CaretInfoChange
) : ServerEvent() {

  @Serializable
  sealed class CaretInfoChange {

    @Serializable
    @SerialName("a")
    object NoCarets : CaretInfoChange()

    @Serializable
    @SerialName("b")
    data class Carets(
      @SerialName("a")
      val caretInfoList: List<CaretInfo> = emptyList(),  // todo: remove default after https://github.com/Kotlin/kotlinx.serialization/issues/806
      @SerialName("b")
      val fontId: Short? = null,
      @SerialName("c")
      val fontSize: Int,
      @SerialName("d")
      val nominalLineHeight: Int,
      @SerialName("e")
      val plainSpaceWidth: Float,
      @SerialName("f")
      val editorWindowId: Int,
      @SerialName("g")
      val editorMetrics: CommonRectangle
    ) : CaretInfoChange()
  }
}

@Serializable
sealed class ServerMarkdownEvent : ServerEvent() {

  @Serializable
  @SerialName("g")
  data class ServerMarkdownShowEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val show: Boolean
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("h")
  data class ServerMarkdownResizeEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val size: CommonIntSize
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("i")
  data class ServerMarkdownMoveEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val point: Point
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("j")
  data class ServerMarkdownDisposeEvent(
    @SerialName("a")
    val panelId: Int
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("k")
  data class ServerMarkdownPlaceToWindowEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val windowId: Int
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("l")
  data class ServerMarkdownSetHtmlEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val html: String
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("m")
  data class ServerMarkdownSetCssEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val css: String
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("n")
  data class ServerMarkdownScrollEvent(
    @SerialName("a")
    val panelId: Int,
    @SerialName("b")
    val scrollOffset: Int
  ) : ServerMarkdownEvent()

  @Serializable
  @SerialName("o")
  data class ServerMarkdownBrowseUriEvent(
    @SerialName("a")
    val link: String
  ) : ServerMarkdownEvent()
}

@Serializable
@SerialName("p")
data class ServerWindowColorsEvent(
  @SerialName("a")
  val colors: Map<String, PaintValue.Color> = emptyMap()
) : ServerEvent()
