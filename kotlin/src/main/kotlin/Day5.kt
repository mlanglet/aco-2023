import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

val digit = Pattern.compile("\\d+")

fun main() {
    val input = Files.readAllLines(Path.of("input/day5/day5.txt"))

    lateinit var partOneSeeds: List<Long>
    lateinit var partTwoSeeds: List<LongRange>

    var parsingSeedToSoil = false
    val seedToSoil = ArrayList<MappingRange>()

    var parsingSoilToFertilizer = false
    val soilToFertilizer = ArrayList<MappingRange>()

    var parsingFertilizerToWater = false
    val fertilizerToWater = ArrayList<MappingRange>()

    var parsingWaterToLight = false
    val waterToLight = ArrayList<MappingRange>()

    var parsingLightToTemperature = false
    val lightToTemperature = ArrayList<MappingRange>()

    var parsingTemperatureToHumidity = false
    val temperatureToHumidity = ArrayList<MappingRange>()

    var parsingHumidityToLocation = false
    val humidityToLocation = ArrayList<MappingRange>()

    input.forEach {
        if (it.startsWith("seeds:")) {
            partOneSeeds = it
                .split(":")[1]
                .split(Pattern.compile("\\s+"))
                .filter { number -> number.isNotBlank() }
                .map { n -> n.toLong() }
                .toList()

            partTwoSeeds = parseSeeds(it)
        }

        if (it.startsWith("seed-to-soil")) {
            parsingSeedToSoil = true
        } else if (parsingSeedToSoil && it.isBlank()) {
            parsingSeedToSoil = false
        } else if (parsingSeedToSoil) {
            seedToSoil.add(buildMappingRange(it))
        }

        if (it.startsWith("soil-to-fertilizer")) {
            parsingSoilToFertilizer = true
        } else if (parsingSoilToFertilizer && it.isBlank()) {
            parsingSoilToFertilizer = false
        } else if (parsingSoilToFertilizer) {
            soilToFertilizer.add(buildMappingRange(it))
        }

        if (it.startsWith("fertilizer-to-water")) {
            parsingFertilizerToWater = true
        } else if (parsingFertilizerToWater && it.isBlank()) {
            parsingFertilizerToWater = false
        } else if (parsingFertilizerToWater) {
            fertilizerToWater.add(buildMappingRange(it))
        }

        if (it.startsWith("water-to-light")) {
            parsingWaterToLight = true
        } else if (parsingWaterToLight && it.isBlank()) {
            parsingWaterToLight = false
        } else if (parsingWaterToLight) {
            waterToLight.add(buildMappingRange(it))
        }

        if (it.startsWith("light-to-temperature")) {
            parsingLightToTemperature = true
        } else if (parsingLightToTemperature && it.isBlank()) {
            parsingLightToTemperature = false
        } else if (parsingLightToTemperature) {
            lightToTemperature.add(buildMappingRange(it))
        }

        if (it.startsWith("temperature-to-humidity")) {
            parsingTemperatureToHumidity = true
        } else if (parsingTemperatureToHumidity && it.isBlank()) {
            parsingTemperatureToHumidity = false
        } else if (parsingTemperatureToHumidity) {
            temperatureToHumidity.add(buildMappingRange(it))
        }

        if (it.startsWith("humidity-to-location")) {
            parsingHumidityToLocation = true
        } else if (parsingHumidityToLocation && it.isBlank()) {
            parsingHumidityToLocation = false
        } else if (parsingHumidityToLocation) {
            humidityToLocation.add(buildMappingRange(it))
        }
    }

    val partOne = partOneSeeds.minOf { seed ->
        val soil = matchRange(seed, seedToSoil)
        val fertilizer = matchRange(soil, soilToFertilizer)
        val water = matchRange(fertilizer, fertilizerToWater)
        val light = matchRange(water, waterToLight)
        val temperature = matchRange(light, lightToTemperature)
        val humidity = matchRange(temperature, temperatureToHumidity)
        matchRange(humidity, humidityToLocation)
    }

    val partTwo = partTwoSeeds.minOf {
        it.minOf { seed ->
            val soil = matchRange(seed, seedToSoil)
            val fertilizer = matchRange(soil, soilToFertilizer)
            val water = matchRange(fertilizer, fertilizerToWater)
            val light = matchRange(water, waterToLight)
            val temperature = matchRange(light, lightToTemperature)
            val humidity = matchRange(temperature, temperatureToHumidity)
            matchRange(humidity, humidityToLocation)
        }
    }

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

fun parseSeeds(it: String): List<LongRange> {
    val seeds = ArrayList<LongRange>()
    val parts = it.split(":")[1]
    val matcher = digit.matcher(parts)
    while (matcher.find()){
        val start = matcher.group().toLong()
        matcher.find()
        val size = matcher.group().toLong()
        seeds.add(LongRange(start, start+size-1))
    }
    return seeds
}

fun matchRange(value: Long, ranges: List<MappingRange>): Long {
    var match: Long? = null
    ranges.forEach {
        if (it.start <= value && it.end >= value){
            match = (value - it.start) + it.destination
        }
    }
    return match ?: value
}

fun buildMappingRange(definition: String): MappingRange {
    val parts = definition.split(" ")
    val destination = parts[0].toLong()
    val start = parts[1].toLong()
    val size = parts[2].toLong()
    val end = start + size - 1
    return MappingRange(start, end, destination)
}

data class MappingRange(val start: Long, val end: Long, val destination: Long)
