import kotlinx.coroutines.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

val digit = Pattern.compile("\\d+")
val mappingHeading = Pattern.compile("(.*)\\smap:")

suspend fun main() {
    val input = Files.readAllLines(Path.of("input/day5/day5.txt"))

    lateinit var partOneSeeds: List<Long>
    lateinit var partTwoSeeds: List<LongRange>

    val mappings = HashMap<String, ArrayList<RangeMapping>>()
    lateinit var mappingName: String

    input.forEach {
        if (it.isBlank()) {
            return@forEach
        }

        if (it.startsWith("seeds:")) {
            partOneSeeds = parseSeeds(it)
            partTwoSeeds = parseSeedRanges(it)
            return@forEach
        }

        val mappingMatcher = mappingHeading.matcher(it)
        if (mappingMatcher.find()) {
            mappingName = mappingMatcher.group(1)
            mappings[mappingName] = ArrayList()
        } else {
            mappings[mappingName]!!.add(buildRangeMapping(it))
        }
    }

    val partOne = partOneSeeds.minOf { seed ->
        mapSeedToLocation(seed, mappings)
    }

    val partTwo = coroutineScope {
        partTwoSeeds.map {
            async { mapSeedRangeToLocation(it, mappings) }
        }.toList().awaitAll()
    }.min()

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

private fun mapSeedRangeToLocation(seedRange: LongRange, mappings: HashMap<String, ArrayList<RangeMapping>>): Long {
    return seedRange.minOf {
        mapSeedToLocation(it, mappings)
    }
}

private fun mapSeedToLocation(
    seed: Long,
    mappings: HashMap<String, ArrayList<RangeMapping>>
): Long {
    return matchRange(
        matchRange(
            matchRange(
                matchRange(
                    matchRange(
                        matchRange(
                            matchRange(
                                seed,
                                mappings["seed-to-soil"]!!
                            ),
                            mappings["soil-to-fertilizer"]!!
                        ),
                        mappings["fertilizer-to-water"]!!
                    ),
                    mappings["water-to-light"]!!
                ),
                mappings["light-to-temperature"]!!
            ),
            mappings["temperature-to-humidity"]!!
        ),
        mappings["humidity-to-location"]!!
    )
}

private fun parseSeeds(it: String): List<Long> {
    return it
        .split(":")[1]
        .split(Pattern.compile("\\s+"))
        .filter { number -> number.isNotBlank() }
        .map { n -> n.toLong() }
        .toList()
}

private fun parseSeedRanges(it: String): List<LongRange> {
    val seeds = ArrayList<LongRange>()
    val parts = it.split(":")[1]
    val matcher = digit.matcher(parts)
    while (matcher.find()) {
        val start = matcher.group().toLong()
        matcher.find()
        val size = matcher.group().toLong()
        seeds.add(LongRange(start, start + size - 1))
    }
    return seeds
}

private fun matchRange(value: Long, ranges: List<RangeMapping>): Long {
    var match: Long? = null
    ranges.forEach {
        if (it.source.first <= value && it.source.last >= value) {
            match = (value - it.source.first) + it.destination.first
        }
    }
    return match ?: value
}

private fun buildRangeMapping(definition: String): RangeMapping {
    val parts = definition.split(" ")
    val destinationStart = parts[0].toLong()
    val sourceStart = parts[1].toLong()
    val size = parts[2].toLong()
    val sourceEnd = sourceStart + size - 1
    val destinationEnd = destinationStart + size - 1
    return RangeMapping(LongRange(sourceStart, sourceEnd), LongRange(destinationStart, destinationEnd))
}

private data class RangeMapping(val source: LongRange, val destination: LongRange)
