import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.pow
import kotlin.math.sqrt


fun main() {
    val input = Files.readAllLines(Path.of("input/day11/day11.txt"))

    val image1 = applyGravitationalEffects(buildBaseImage(input))

    val partOne = partOne(image1)
    val partTwo = 0

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

fun partOne(image: List<List<Char>>): Int {
    var sumOfDistances = 0
    val galaxies = image.mapIndexed { rowIndex, row ->
        row.mapIndexed { columnIndex, char ->
            if (char == '#'){
                Point(columnIndex, rowIndex)
            } else {
                null
            }
        }.filterNotNull()
    }.toList().flatten()

    val countedPairs = HashSet<Pair<Point, Point>>()
    galaxies.forEach { g1 ->
        galaxies.forEach { g2 ->
            if (g1 != g2){
                val sortedGalaxies = listOf(g1, g2).sorted()
                val pair = Pair(sortedGalaxies[0], sortedGalaxies[1])
                if (pair !in countedPairs) {
                    sumOfDistances += distanceBetweenGalaxies(pair.first, pair.second)
                    countedPairs.add(pair)
                }
            }
        }
    }

    return sumOfDistances
}

fun distanceBetweenGalaxies(first: Point, second: Point): Int {
    var distance = 0
    var currentPosition = first
    while (currentPosition != second) {
        val candidate1 = currentPosition.copy(x = currentPosition.x + 1)
        val candidate2 = currentPosition.copy(x = currentPosition.x - 1)
        val candidate3 = currentPosition.copy(y = currentPosition.y + 1)
        val candidate4 = currentPosition.copy(y = currentPosition.y - 1)

        currentPosition = listOf(
            CandidateMove(candidate1, distanceToGalaxy(candidate1.x, candidate1.y, second)),
            CandidateMove(candidate2, distanceToGalaxy(candidate2.x, candidate2.y, second)),
            CandidateMove(candidate3, distanceToGalaxy(candidate3.x, candidate3.y, second)),
            CandidateMove(candidate3, distanceToGalaxy(candidate4.x, candidate4.y, second)),
        ).minOf { it }.move

        distance++
    }
    return distance
}

fun distanceToGalaxy(x: Int, y: Int, galaxy: Point) : Int {
    return sqrt((x - galaxy.x).toDouble().pow(2.0) + (y - galaxy.y).toDouble().pow(2.0)).toInt()
}

fun applyGravitationalEffects(image: List<MutableList<Char>>) : List<List<Char>> {
    val additionalEmptyColumns = ArrayList<Int>()
    (0..<image[0].size).map { columnIndex ->
        val isEmptyColumn = image.map { row ->
            row[columnIndex] == '.'
        }.all {
            it
        }

        if (isEmptyColumn) {
            additionalEmptyColumns.add(columnIndex)
        }
    }

    var insertedColumns = 0
    additionalEmptyColumns.map { columnIndex ->
        image.forEach { row ->
            row.add(columnIndex + insertedColumns, '.')
        }
        insertedColumns++
    }
    return image
}

private fun buildBaseImage(input: List<String>): List<MutableList<Char>> {
    return input.map { row ->
        if (row.all { it == '.' }) {
            listOf(row.toCharArray().toMutableList(), row.toCharArray().toMutableList())
        } else {
            listOf(row.toCharArray().toMutableList())
        }
    }.flatten()
}


data class Point(val x: Int, val y: Int) : Comparable<Point> {
    override fun compareTo(other: Point): Int {
        return if(this.y > other.y){
            1
        } else if(this.y < other.y){
            -1
        } else {
            return if(this.x > other.x){
                1
            } else if(this.x < other.x){
                -1
            } else {
                0
            }
        }
    }

    fun plus(other: Point) : Point {
        return Point(this.x + other.x, this.y + other.y)
    }
}

data class CandidateMove(val move: Point, val distance: Int) : Comparable<CandidateMove> {
    override fun compareTo(other: CandidateMove): Int {
        return distance.compareTo(other.distance)
    }
}

