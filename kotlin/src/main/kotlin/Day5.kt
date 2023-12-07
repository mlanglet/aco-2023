import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

val digit = Pattern.compile("\\d+")
val mappingHeading = Pattern.compile("(.*)\\smap:")

fun main() {
    val input = Files.readAllLines(Path.of("input/day5/day5.txt"))

    lateinit var partOneSeeds: List<Long>
    lateinit var partTwoSeeds: List<LongRange>

    val mappings = HashMap<String, ArrayList<MappingRange>>()
    lateinit var mappingName: String

    input.forEach {
        if (it.isBlank()){
            return@forEach
        }

        if (it.startsWith("seeds:")) {
            partOneSeeds = it
                .split(":")[1]
                .split(Pattern.compile("\\s+"))
                .filter { number -> number.isNotBlank() }
                .map { n -> n.toLong() }
                .toList()

            partTwoSeeds = parseSeeds(it)

            return@forEach
        }

        val mappingMatcher = mappingHeading.matcher(it)
        if(mappingMatcher.find()){
            mappingName = mappingMatcher.group(1)
            mappings[mappingName] = ArrayList()
        } else {
            mappings[mappingName]!!.add(buildMappingRange(it))
        }
    }

    val partOne = partOneSeeds.minOf { seed ->
        matchRange(
            matchRange(
                matchRange(
                    matchRange(
                        matchRange(
                            matchRange(
                                matchRange(seed,
                                    mappings["seed-to-soil"]!!),
                                mappings["soil-to-fertilizer"]!!),
                            mappings["fertilizer-to-water"]!!),
                        mappings["water-to-light"]!!),
                    mappings["light-to-temperature"]!!),
                mappings["temperature-to-humidity"]!!),
            mappings["humidity-to-location"]!!)
    }

    val partTwo = 0
//    val partTwo = partTwoSeeds.minOf {
//        it.minOf { seed ->
//            matchRange(
//                matchRange(
//                    matchRange(
//                        matchRange(
//                            matchRange(
//                                matchRange(
//                                    matchRange(seed,
//                                        mappings["seed-to-soil"]!!),
//                                    mappings["soil-to-fertilizer"]!!),
//                                mappings["fertilizer-to-water"]!!),
//                            mappings["water-to-light"]!!),
//                        mappings["light-to-temperature"]!!),
//                    mappings["temperature-to-humidity"]!!),
//                mappings["humidity-to-location"]!!)
//        }
//    }

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
