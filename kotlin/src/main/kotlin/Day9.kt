import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs


fun main() {
    val input = Files.readAllLines(Path.of("input/day9/day9.txt"))
    var partOne = 0L
    var partTwo = 0L
    input.forEach { historyReading ->
        val values = historyReading
            .split(" ")
            .map { it.toLong() }
            .toMutableList()

        partOne += extrapolateNextValue(values)
        partTwo += extrapolatePreviousValue(values)
    }
    println("Part one: $partOne")
    println("Part two: $partTwo")
}

private fun extrapolateNextValue(values: List<Long>): Long {

    val diffLists = ArrayList<List<Long>>(listOf(values))
    while (diffLists.last().any { it != 0L }) {
        val diffListInput = diffLists.last()
        diffLists.add((0..<diffListInput.size - 1).map { diffListInput[it + 1] - diffListInput[it] }.toMutableList())
    }

    diffLists.last().addLast(0)
    (diffLists.size-2 downTo 0).map {
        diffLists[it].addLast(diffLists[it+1].last() + diffLists[it].last())
    }

    return diffLists.first().last()
}

private fun extrapolatePreviousValue(values: List<Long>): Long {

    val diffLists = ArrayList<List<Long>>(listOf(values))
    while (diffLists.last().any { it != 0L }) {
        val diffListInput = diffLists.last()
        diffLists.add((0..<diffListInput.size - 1).map { diffListInput[it + 1] - diffListInput[it] }.toMutableList())
    }

    diffLists.last().addLast(0)
    (diffLists.size-2 downTo 0).map {
        diffLists[it].addFirst( diffLists[it].first() - diffLists[it+1].first())
    }

    return diffLists.first().first()
}

