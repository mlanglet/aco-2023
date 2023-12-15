import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.max


fun main() {
    val input = Files.readAllLines(Path.of("input/day14/day14.txt"))
    var partOne = 0
    val size = input.size
    val bounds = RockPosition(input.first().length - 1, input.size - 1)
    val roundRocks: MutableMap<Int, MutableList<Rock>> = mutableMapOf()
    val squareRocks: MutableMap<Int, MutableList<Rock>> = mutableMapOf()
    val rocks: MutableList<Rock> = mutableListOf()

    println("Starting position: ")
    input.forEach {
        println(it)
    }
    println()
    println()

    input.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { columnIndex, c ->
            if (c == 'O') {
                if (roundRocks[columnIndex] == null) {
                    roundRocks[columnIndex] = mutableListOf(Rock(columnIndex, rowIndex, c, bounds))
                } else {
                    roundRocks[columnIndex]!!.add(Rock(columnIndex, rowIndex, c, bounds))
                }
            } else if (c == '#') {
                if (squareRocks[columnIndex] == null) {
                    squareRocks[columnIndex] = mutableListOf(Rock(columnIndex, rowIndex, c, bounds))
                } else {
                    squareRocks[columnIndex]!!.add(Rock(columnIndex, rowIndex, c, bounds))
                }
            }

            if (c != '.') {
                rocks.add(Rock(columnIndex, rowIndex, c, bounds))
            }
        }
    }

    roundRocks.map { e ->
        val rockColumn = e.value
        rockColumn.forEachIndexed { index, rock ->
            rock.y = max(rockColumn.filter { it.y < rock.y }.map { it.y + 1 }.maxOrNull() ?: 0,
                squareRocks[e.key]?.filter { it.y < rock.y }?.map { it.y + 1 }?.maxOrNull() ?: 0
            )
        }
    }

    roundRocks.map { e ->
        partOne += e.value.map { rock ->
            size - rock.y
        }.sum()
    }

    val staticRocks = rocks.filter { it.isSquare() }.associateBy { RockPosition(it.x, it.y) }
    val movingRocks = rocks.filter { it.isRound() }
    (1..1000).forEach {
        var direction = TiltDirection.first()
        (1..4).forEach {
            val rocksReadyForMove = when(direction){
                TiltDirection.North -> movingRocks.sortedBy { it.y }
                TiltDirection.West -> movingRocks.sortedBy { it.x }
                TiltDirection.South -> movingRocks.sortedByDescending { it.y }
                TiltDirection.East -> movingRocks.sortedByDescending { it.x }
            }
            rocksReadyForMove.forEach { rock ->
                val movingRockLookup = rocksReadyForMove.associateBy { RockPosition(it.x, it.y) }
                rock.roll(direction, staticRocks, movingRockLookup)
            }
            direction = direction.nextDirection()
        }

        val movingRocksLookup = movingRocks.associateBy { RockPosition(it.x, it.y) }
        println("Cycle: $it")
        (0..bounds.y).forEach { row ->
            (0..bounds.x).forEach { col ->
                if (staticRocks.containsKey(RockPosition(col, row))){
                    print("#")
                } else if (movingRocksLookup.containsKey(RockPosition(col, row))){
                    print("O")
                } else {
                    print(".")
                }
            }
            println()
        }
        println()
        println()
    }

    val partTwo = movingRocks.map { rock -> size - rock.y }.sum()

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

private enum class TiltDirection {
    North,
    West,
    South,
    East;

    companion object {
        fun first() : TiltDirection = North
    }


    fun nextDirection(): TiltDirection {
        return when (this) {
            North -> West
            West -> South
            South -> East
            East -> North
        }
    }

    fun modifier(): RockPosition {
        return when (this) {
            North -> RockPosition(0, -1)
            West -> RockPosition(-1, 0)
            South -> RockPosition(0, 1)
            East -> RockPosition(1, 0)
        }
    }
}

private enum class RockType {
    Round,
    Square;
}

private data class RockPosition(var x: Int, var y: Int) {
    fun add(rock: Rock): RockPosition {
        return RockPosition(rock.x + x, rock.y + y)
    }
}

private data class Rock(var x: Int, var y: Int, val type: Char, val bounds: RockPosition) {

    val rockType: RockType

    init {
        if (type == 'O') {
            rockType = RockType.Round
        } else if (type == '#') {
            rockType = RockType.Square
        } else {
            throw IllegalArgumentException("Wrong rock type $type")
        }
    }

    fun isRound(): Boolean = rockType == RockType.Round

    fun isSquare(): Boolean = rockType == RockType.Square

    fun roll(direction: TiltDirection, squareRocks: Map<RockPosition, Rock>, movingRocks: Map<RockPosition, Rock>) {
        if (isRound()) {
            var moveCandidate = direction.modifier().add(this)
            while (!squareRocks.containsKey(moveCandidate)
                && !isOutOfBounds(moveCandidate)
                && !movingRocks.containsKey(moveCandidate)
            ) {
                setPosition(moveCandidate)
                moveCandidate = direction.modifier().add(this)
            }
        }
    }

    private fun isOutOfBounds(moveCandidate: RockPosition): Boolean {
        return moveCandidate.x < 0 || moveCandidate.x > bounds.x ||
            moveCandidate.y < 0 || moveCandidate.y > bounds.y
    }

    private fun setPosition(newPosition: RockPosition) {
        x = newPosition.x
        y = newPosition.y
    }
}
