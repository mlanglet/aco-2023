import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.max

fun main() {
    val input = Files.readAllLines(Path.of("input/day16/day16.txt"))
    val maxColumn = (0..<input.first().length).map { column ->
        listOf(
            simulateLightBeam(input, LightBeam(LightPosition(column, 0), LightDirection.South)),
            simulateLightBeam(input, LightBeam(LightPosition(column, input.size - 1), LightDirection.North)),
        )
    }.flatten().max()

    val maxRow = (0..<input.size).map { row ->
        listOf(
            simulateLightBeam(input, LightBeam(LightPosition(0, row), LightDirection.East)),
            simulateLightBeam(input, LightBeam(LightPosition(input.first().length - 1, row), LightDirection.West)),
        )
    }.flatten().max()

    println("Part one: ${simulateLightBeam(input, LightBeam(LightPosition(0, 0), LightDirection.East)) }")
    println("Part two: ${max(maxColumn, maxRow)}")
}

private fun simulateLightBeam(grid: List<String>, lightBeam: LightBeam) : Int {
    val energizedNodes: MutableSet<LightBeam> = mutableSetOf()

    followBeam(grid, lightBeam, energizedNodes)

    return energizedNodes.map { it.position}.toSet().size
}

private fun followBeam(grid: List<String>, currentPosition: LightBeam, energizedNodes: MutableSet<LightBeam>) {
    if (energizedNodes.contains(currentPosition) || isOutOfBounds(currentPosition, grid)){
        return
    }
    energizedNodes.add(currentPosition)

    val beams = currentPosition.eval(grid[currentPosition.position.y][currentPosition.position.x])
    beams.forEach {
        followBeam(grid, it, energizedNodes)
    }
}

private fun printGrid(grid: List<String>, currentPosition: LightBeam, energizedNodes: MutableSet<LightBeam>) {
    grid.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, tile ->
            val currentTile = LightPosition(colIndex, rowIndex)
            val energizedNode = energizedNodes.find { it.position == currentTile }
            if(currentPosition.position == currentTile){
                print("@")
            } else if (energizedNode != null){
                print("#")
            } else {
                print(tile)
            }
        }
        println()
    }
    println()
    println()
}

private fun isOutOfBounds(beam: LightBeam, grid: List<String>) : Boolean {
    return beam.position.x < 0 || beam.position.x > grid.first().length - 1
        || beam.position.y < 0 || beam.position.y > grid.size - 1
}

private data class LightPosition(val x: Int, val y: Int){
    fun plus(other: LightPosition) = LightPosition(x + other.x, y + other.y)
}

private enum class LightDirection(val modifier: LightPosition) {
    North(LightPosition(0, -1)),
    East(LightPosition(1, 0)),
    South(LightPosition(0, 1)),
    West(LightPosition(-1, 0));

    fun getDirectionModifier(): LightPosition = modifier
}

private data class LightBeam(val position: LightPosition, val direction: LightDirection) {
    fun split(): List<LightBeam> {
        return when (direction) {
            LightDirection.North, LightDirection.South ->
                listOf(move(LightDirection.East), move(LightDirection.West))

            LightDirection.East, LightDirection.West ->
                listOf(move(LightDirection.North), move(LightDirection.South))
        }
    }

    fun eval(tile: Char): List<LightBeam> {
        return when(direction){
            LightDirection.North -> {
                when(tile){
                    '/' -> listOf(move(LightDirection.East))
                    '\\' -> listOf(move(LightDirection.West))
                    '-' -> split()
                    else -> listOf(move(direction))
                }
            }
            LightDirection.East -> {
                when(tile){
                    '/' -> listOf(move(LightDirection.North))
                    '\\' -> listOf(move(LightDirection.South))
                    '|' -> split()
                    else -> listOf(move(direction))
                }
            }
            LightDirection.South -> {
                when(tile){
                    '/' -> listOf(move(LightDirection.West))
                    '\\' -> listOf(move(LightDirection.East))
                    '-' -> split()
                    else -> listOf(move(direction))
                }
            }
            LightDirection.West -> {
                when(tile){
                    '/' -> listOf(move(LightDirection.South))
                    '\\' -> listOf(move(LightDirection.North))
                    '|' -> split()
                    else -> listOf(move(direction))
                }
            }
        }
    }

    fun move(direction: LightDirection): LightBeam {
        return LightBeam(position.plus(direction.getDirectionModifier()), direction)
    }
}
