import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs


fun main() {
    val input = Files.readAllLines(Path.of("input/day11/day11.txt"))

    val image = buildBaseImage(input)

    val partOne = countDistanceBetweenPairs(buildGalaxies(image, 2))
    val partTwo = countDistanceBetweenPairs(buildGalaxies(image, 1_000_000))

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

private fun countDistanceBetweenPairs(galaxies: List<Coordinates>): Long {
    var sumOfDistances = 0L

    val countedPairs = HashSet<Pair<Coordinates, Coordinates>>()
    galaxies.forEach { g1 ->
        galaxies.forEach { g2 ->
            if (g1 != g2) {
                val sortedPairs = listOf(g1, g2).sorted()
                val pair = Pair(sortedPairs.first(), sortedPairs.last())
                if (pair !in countedPairs) {
                    sumOfDistances += distanceBetweenGalaxies(pair.first, pair.second)
                    countedPairs.add(pair)
                }
            }
        }
    }

    return sumOfDistances
}

private fun distanceBetweenGalaxies(first: Coordinates, second: Coordinates): Long {
    return abs(first.x - second.x) + abs(first.y - second.y)
}

private fun buildGalaxies(image: List<List<Char>>, gravityCoefficient: Long): List<Coordinates> {
    val emptyRowIndices = image.mapIndexed { index, chars ->
        if (chars.all { char -> char == '.' }){
            index
        } else {
            null
        }
    }.filterNotNull().toList().sorted()
    val emptyColumnIndices = (0..<image[0].size).filter { columnIndex ->
        image.map { row ->
            row[columnIndex] == '.'
        }.all {
            it
        }
    }.toList().sorted()

    return image.mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, char ->
            if (char == '#') {
                val yModifier = emptyRowIndices.count { emptyRowIndex -> emptyRowIndex < rowIndex }
                val yCoordinate = rowIndex + (yModifier * gravityCoefficient) - (yModifier * 1)
                val xModifier = emptyColumnIndices.count { emptyColumnIndex -> emptyColumnIndex < columnIndex }
                val xCoordinate = columnIndex + (xModifier * gravityCoefficient) - (xModifier * 1)

                Coordinates(xCoordinate, yCoordinate)
            } else {
                null
            }
        }.filterNotNull()
    }.flatten().toList()
}

private fun buildBaseImage(input: List<String>): List<List<Char>> {
    return input.map { row ->
        row.toCharArray().toList()
    }
}


private data class Coordinates(val x: Long, val y: Long) : Comparable<Coordinates> {
    override fun compareTo(other: Coordinates): Int {
        return if (this.y.compareTo(other.y) == 0){
            this.x.compareTo(other.x)
        } else {
            this.y.compareTo(other.y)
        }
    }
}


