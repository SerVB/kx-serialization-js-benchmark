# JSON decoding benchmarks for Kotlin/JS
Let's determine the best way of decoding JSON in K/JS!

## Data
Data classes from [Projector protocol](https://github.com/JetBrains/projector-client) are taken. A 200 KB JSON containing a snapshot of IntelliJ IDEA UI is used.

## Competitors
- `kotlinx.serialization.json.Json.parse` ([KotlinxJsonDecoder.kt](src/main/kotlin/decoder/KotlinxJsonDecoder.kt)).
- `kotlinx.serialization.DynamicObjectParser.parse` ([KotlinxDopJsonDecoder.kt](src/main/kotlin/decoder/KotlinxDopJsonDecoder.kt)).
- A manual parser which uses `when` to handle polymorphism ([ManualWhenJsonDecoder.kt](src/main/kotlin/decoder/ManualWhenJsonDecoder.kt)).
- A manual parser which uses `mapOf` to handle polymorphism ([ManualKotlinMapJsonDecoder.kt](src/main/kotlin/decoder/ManualKotlinMapJsonDecoder.kt)).
- A manual parser which uses `json` to handle polymorphism ([ManualJsMapJsonDecoder.kt](src/main/kotlin/decoder/ManualJsMapJsonDecoder.kt)).

## Results
I use Chromium 78 on Linux Mint 19.3. CPU is i7-7700HQ.

There is an error every launch, but the main thing is clear: kx is much slower than manual decoding code:
```
kotlinx 200KB:           avg = 40.4 ms, (min, max) = (37.4 ms, 48.3 ms)
kotlinx DOP 200KB:       avg = 36.9 ms, (min, max) = (35.8 ms, 39.0 ms)
manual when 200KB:       avg =  2.2 ms, (min, max) = ( 1.8 ms,  4.6 ms)
manual kotlin map 200KB: avg =  2.2 ms, (min, max) = ( 2.0 ms,  2.7 ms)
manual js map 200KB:     avg =  2.3 ms, (min, max) = ( 1.9 ms,  4.6 ms)
```
