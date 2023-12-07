import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern


fun main() {
    val input = Files.readAllLines(Path.of("input/day6/day6.txt"))
    var partOne = 0
    var partTwo = 0

    lateinit var times: List<Int>
    lateinit var distances: List<Int>
    var time: Long = 0
    var distance: Long = 0

    input.forEach {
        if (it.startsWith("Time:")) {
            time = parseNumber(it)
            times = parseNumbers(it)
        }
        if (it.startsWith("Distance:")) {
            distance = parseNumber(it)
            distances = parseNumbers(it)
        }
    }

    times.forEachIndexed { i, t ->
        var waysToWin = 0
        val threshold = distances[i]
        (1..t).forEach { pressTime ->
            val distanceTravelled = pressTime * (t - pressTime)
            if (distanceTravelled > threshold) {
                waysToWin++
            }
        }
        if (partOne == 0) {
            partOne = waysToWin
        } else {
            partOne *= waysToWin
        }
    }

    val threshold = distance
    (1..time).forEach { pressTime ->
        val distanceTravelled = pressTime * (time - pressTime)
        if (distanceTravelled > threshold) {
            partTwo++
        }
    }

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

fun parseNumber(it: String): Long {
    return it
        .split(":")[1]
        .trim()
        .replace(" ", "")
        .toLong()
}

private fun parseNumbers(it: String): List<Int> {
    return it
        .split(":")[1]
        .split(Pattern.compile("\\s+"))
        .filter { s -> s.isNotBlank() }
        .map { n -> n.toInt() }
        .toList()
}

