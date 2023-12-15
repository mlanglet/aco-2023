import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.pow


fun main() {
    val input = Files.readAllLines(Path.of("input/day12/example.txt"))
    var partOne = 0
    var partTwo = 0
    input.forEach {
        val parts = it.split(" ")
        val springs = parts[0]
        val groups = parts[1].split(",").map { it.toInt() }

        println("Springs: $springs, groups: $groups")

        partOne += countPossibleCombinations(springs, groups)
    }
    println("Part one: $partOne")
    println("Part two: $partTwo")
}

val damagePattern = "[#|?]*".toRegex().toPattern()

fun countPossibleCombinations(pattern: String, groups: List<Int>): Int {
    val groupCombinations = mutableListOf<Int>()
    countPossibleCombinations(pattern, groups, groupCombinations)
    return groupCombinations.sum()
}

fun countPossibleCombinations(
    pattern: String,
    remainingGroups: List<Int>,
    groupCombinations: MutableList<Int>,
) {
    if (remainingGroups.isEmpty()){
        return
    }

    val groupSize = remainingGroups.first()
    val matcher = damagePattern.matcher(pattern)
    var nextSegment: String
    if (matcher.find()){
        nextSegment = matcher.group()
    } else {
        throw IllegalStateException("No segment to derive combinations from")
    }

    val remainingGroupCandidates = mutableListOf<String>()
    while (matcher.find()){
        remainingGroupCandidates.add(matcher.group())
    }

    remainingGroups.reversed().forEach {
        if (remainingGroupCandidates.isNotEmpty()){
            remainingGroupCandidates.removeLast()
        } else {
            nextSegment = nextSegment.substring(0, nextSegment.length - it)
        }
    }


    countPossibleCombinations(pattern.substring(remainingGroups.first()), remainingGroups.drop(1).toList(), groupCombinations)
}
