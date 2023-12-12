import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val input = Files.readAllLines(Path.of("input/day10/day10.txt"))

    val maze = Array(input.size) { i -> input[i].map { char -> prettyChar(char) }.toCharArray() }
    val start = findStartingPosition(maze)
    val pipe = buildPipe(start, maze)
    val enclosedNodes = findInsideNodes(pipe, maze)

    enclosedNodes.forEach { node ->
        maze[node.y][node.x] = 'I'
    }

    val red = "\u001b[31m"
    val yellow = "\u001b[33m"
    val green = "\u001b[32m"
    val reset = "\u001b[0m"

    maze.forEachIndexed { i, row ->
        row.forEachIndexed { i2, col ->
            if (col == 'S'){
                print("$red$col$reset")
            }else if (pipe.contains(Point(i2, i))){
                print("$yellow$col$reset")
            } else if (enclosedNodes.contains(Point(i2, i))) {
                print("$green$col$reset")
            }  else {
                print(col)
            }
        }
        println()
    }

    println("Part one: ${pipe.size / 2}")
    println("Part two: ${enclosedNodes.size}")
}

private fun buildPipe(
    startingPosition: Point,
    maze: Array<CharArray>,
): List<Point> {
    val pipe = mutableListOf(startingPosition)
    var currentPosition: Point = firstConnector(startingPosition, maze)
    var currentDirection =
        determineNextDirection(determineDirection(startingPosition, currentPosition), currentPosition, maze)
    while (currentPosition != startingPosition) {
        pipe.add(currentPosition)
        currentPosition = nextConnector(currentPosition, currentDirection)
        currentDirection = determineNextDirection(currentDirection, currentPosition, maze)
    }

    return pipe
}

private fun determineNextDirection(
    currentDirection: Direction, nextPosition: Point, maze: Array<CharArray>
): Direction {
    return when (maze[nextPosition.y][nextPosition.x]) {
        '═', '║', 'S' -> {
            currentDirection
        }

        '╗' -> {
            when (currentDirection) {
                Direction.North -> Direction.West
                else -> Direction.South
            }
        }

        '╔' -> {
            when (currentDirection) {
                Direction.North -> Direction.East
                else -> Direction.South
            }
        }

        '╝' -> {
            when (currentDirection) {
                Direction.South -> Direction.West
                else -> Direction.North
            }
        }

        '╚' -> {
            when (currentDirection) {
                Direction.South -> Direction.East
                else -> Direction.North
            }
        }

        else -> throw IllegalStateException("Broken Pipe")
    }
}

private fun findInsideNodes(pipe: List<Point>, maze: Array<CharArray>): List<Point> {
    val mazeBounds = Point(maze[0].size - 1, maze.size - 1)
    val pipeLookup = pipe.toSet()
    val starboardNonPipeTiles = ArrayList<Point>()
    val portNonPipeTiles = ArrayList<Point>()
    var clockwise: Boolean? = null
    var previousPosition = pipe[0]

    pipe.drop(1).forEach {
        val currentPosition = it
        val currentDirection = determineDirection(previousPosition, currentPosition)
        val starboard: Point
        var extraStarboardCheck: Point? = null
        val port: Point
        var extraPortCheck: Point? = null
        when (currentDirection) {
            Direction.North -> {
                starboard = it.east()
                port = it.west()
                if (maze[currentPosition.y][currentPosition.x] == '╔'){
                    extraPortCheck = it.north()
                }
                if (maze[currentPosition.y][currentPosition.x] == '╗'){
                    extraStarboardCheck = it.north()
                }
            }

            Direction.East -> {
                starboard = it.south()
                port = it.north()
                if (maze[currentPosition.y][currentPosition.x] == '╝'){
                    extraStarboardCheck = it.east()
                }
                if (maze[currentPosition.y][currentPosition.x] == '╗'){
                    extraPortCheck = it.east()
                }
            }

            Direction.South -> {
                starboard = it.west()
                port = it.east()
                if (maze[currentPosition.y][currentPosition.x] == '╝'){
                    extraPortCheck = it.south()
                }
                if (maze[currentPosition.y][currentPosition.x] == '╚'){
                    extraStarboardCheck = it.south()
                }
            }

            Direction.West -> {
                starboard = it.north()
                port = it.south()
                if (maze[currentPosition.y][currentPosition.x] == '╚'){
                    extraPortCheck = it.west()
                }
                if (maze[currentPosition.y][currentPosition.x] == '╔'){
                    extraStarboardCheck = it.west()
                }
            }
        }

        if (clockwise == null && canWalkOutOfBounds(starboard, it, mazeBounds, pipeLookup)) {
            clockwise = false
        }

        if (clockwise == null && canWalkOutOfBounds(port, it, mazeBounds, pipeLookup)) {
            clockwise = true
        }

        val starboardTile = it.add(starboard)
        if (starboardTile !in pipeLookup && !outOfBounds(starboardTile, mazeBounds)) {
            starboardNonPipeTiles.add(starboardTile)
        }

        if(extraStarboardCheck != null){
            val extraStarboardTile = it.add(extraStarboardCheck)
            if (extraStarboardTile !in pipeLookup && !outOfBounds(extraStarboardTile, mazeBounds)) {
                starboardNonPipeTiles.add(extraStarboardTile)
            }
        }

        val portTile = it.add(port)
        if (portTile !in pipeLookup && !outOfBounds(portTile, mazeBounds)) {
            portNonPipeTiles.add(portTile)
        }

        if(extraPortCheck != null){
            val extraPortTile = it.add(extraPortCheck)
            if (extraPortTile !in pipeLookup && !outOfBounds(extraPortTile, mazeBounds)) {
                portNonPipeTiles.add(extraPortTile)
            }
        }

        previousPosition = currentPosition
    }

    val insideTiles = HashSet<Point>()
    if (clockwise!!) {
        starboardNonPipeTiles.forEach {
            addAllConnectedTiles(it, insideTiles, pipeLookup)
        }
    } else {
        portNonPipeTiles.forEach {
            addAllConnectedTiles(it, insideTiles, pipeLookup)
        }
    }

    return insideTiles.toList()
}

private fun addAllConnectedTiles(tile: Point, connectedTiles: HashSet<Point>, pipe: Set<Point>) {
    if (tile in connectedTiles) {
        return
    }

    if (tile in pipe) {
        return
    }

    connectedTiles.add(tile)

    addAllConnectedTiles(tile.add(tile.north()), connectedTiles, pipe)
    addAllConnectedTiles(tile.add(tile.east()), connectedTiles, pipe)
    addAllConnectedTiles(tile.add(tile.south()), connectedTiles, pipe)
    addAllConnectedTiles(tile.add(tile.west()), connectedTiles, pipe)
}

private fun canWalkOutOfBounds(direction: Point, position: Point, mazeBounds: Point, pipe: Set<Point>): Boolean {
    val nextPosition = position.add(direction)
    if (outOfBounds(nextPosition, mazeBounds)) {
        return true
    }

    if (nextPosition in pipe) {
        return false
    }

    return canWalkOutOfBounds(direction, nextPosition, mazeBounds, pipe)
}

private fun outOfBounds(point: Point, mazeBounds: Point): Boolean {
    return point.x < 0 || point.x > mazeBounds.x || point.y < 0 || point.y > mazeBounds.y
}

private fun determineDirection(previousPosition: Point, currentPosition: Point): Direction {
    return if (currentPosition.x > previousPosition.x) {
        Direction.East
    } else if (currentPosition.x < previousPosition.x) {
        Direction.West
    } else if (currentPosition.y > previousPosition.y) {
        Direction.South
    } else {
        Direction.North
    }
}

private fun nextConnector(
    currentPosition: Point,
    currentDirection: Direction,
): Point {
    return currentDirection.nextPoint(currentPosition)
}

private fun firstConnector(
    currentPosition: Point,
    maze: Array<CharArray>,
): Point {
    if (currentPosition.x > 0) {
        val candidateConnector = currentPosition.copy(x = currentPosition.x - 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('╔', '╚', '═')) {
            return candidateConnector
        }
    }
    if (currentPosition.x < maze[0].size - 1) {
        val candidateConnector = currentPosition.copy(x = currentPosition.x + 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('╝', '╗', '═')) {
            return candidateConnector
        }
    }
    if (currentPosition.y > 0) {
        val candidateConnector = currentPosition.copy(y = currentPosition.y - 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('╗', '╔', '║')) {
            return candidateConnector
        }
    }
    if (currentPosition.y < maze.size - 1) {
        val candidateConnector = currentPosition.copy(y = currentPosition.y + 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('╝', '╚', '║')) {
            return candidateConnector
        }
    }
    throw IllegalStateException("No connection to start")
}

private fun findStartingPosition(maze: Array<CharArray>): Point {
    var startingPosition = Point(0, 0)
    maze.mapIndexed { row, a ->
        a.mapIndexed { column, c ->
            if (c == 'S') {
                startingPosition = Point(column, row)
            }
        }
    }
    return startingPosition
}

private enum class Direction {
    North, East, South, West;

    fun nextPoint(point: Point): Point {
        return when (this) {
            North -> point.copy(y = point.y - 1)
            East -> point.copy(x = point.x + 1)
            South -> point.copy(y = point.y + 1)
            West -> point.copy(x = point.x - 1)
        }
    }
}

private data class Point(val x: Int, val y: Int) {
    fun add(other: Point): Point {
        return Point(this.x + other.x, this.y + other.y)
    }

    fun north(): Point {
        return Point(0, -1)
    }

    fun east(): Point {
        return Point(1, 0)
    }

    fun south(): Point {
        return Point(0, 1)
    }

    fun west(): Point {
        return Point(-1, 0)
    }
}

private fun prettyChar(char: Char): Char {
    return when (char) {
        '-' -> '═'
        '|' -> '║'
        '7' -> '╗'
        'F' -> '╔'
        'J' -> '╝'
        'L' -> '╚'
        'S' -> char
        else -> '.'
    }
}
