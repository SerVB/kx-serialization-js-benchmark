package decoder

import kotlinx.serialization.json.JsonConfiguration

object KotlinxJson {

  val configuration = JsonConfiguration.Stable.copy(useArrayPolymorphism = true)
}
