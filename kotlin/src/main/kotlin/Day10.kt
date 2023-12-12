import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val input = Files.readAllLines(Path.of("input/day10/day10.txt"))

    val maze = Array(input.size) { i -> input[i].toCharArray() }
    val startingPosition = findStartingPosition(maze)
    val pipe = buildPipe(startingPosition, maze)

    println("Part one: ${pipe.size / 2}")
    println("Part two: ${0}")
}

private fun buildPipe(
    startingPosition: Point,
    maze: Array<CharArray>,
): HashSet<Point> {
    val pipe = HashSet<Point>(listOf(startingPosition))
    var currentPosition: Point? = firstConnector(startingPosition, maze)
    while (currentPosition != null) {
        pipe.add(currentPosition)
        currentPosition = nextConnector(currentPosition, pipe, maze)
    }
    return pipe
}

private fun nextConnector(
    currentPosition: Point,
    pipe: HashSet<Point>,
    maze: Array<CharArray>,
): Point? {
    return when (maze[currentPosition.y][currentPosition.x]) {
        '-' -> {
            candidateNotInPipe(
                listOf(
                    currentPosition.copy(x = currentPosition.x + 1),
                    currentPosition.copy(x = currentPosition.x - 1),
                ), pipe
            )
        }

        '|' -> {
            candidateNotInPipe(
                listOf(
                    currentPosition.copy(y = currentPosition.y + 1),
                    currentPosition.copy(y = currentPosition.y - 1),
                ), pipe
            )
        }

        'F' -> {
            candidateNotInPipe(
                listOf(
                    currentPosition.copy(x = currentPosition.x + 1),
                    currentPosition.copy(y = currentPosition.y + 1),
                ), pipe
            )
        }

        'J' -> {
            candidateNotInPipe(
                listOf(
                    currentPosition.copy(x = currentPosition.x - 1),
                    currentPosition.copy(y = currentPosition.y - 1),
                ), pipe
            )
        }

        'L' -> {
            candidateNotInPipe(
                listOf(
                    currentPosition.copy(y = currentPosition.y - 1),
                    currentPosition.copy(x = currentPosition.x + 1),
                ), pipe
            )
        }

        '7' -> {
            candidateNotInPipe(
                listOf(
                    currentPosition.copy(y = currentPosition.y + 1),
                    currentPosition.copy(x = currentPosition.x - 1),
                ), pipe
            )
        }

        else -> {
            throw IllegalStateException("Broken pipe! hehehe")
        }
    }
}

private fun firstConnector(
    currentPosition: Point,
    maze: Array<CharArray>,
): Point {
    if (currentPosition.x > 0) {
        val candidateConnector = currentPosition.copy(x = currentPosition.x - 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('F', 'L', '-')) {
            return candidateConnector
        }
    }
    if (currentPosition.x < maze[0].size - 1) {
        val candidateConnector = currentPosition.copy(x = currentPosition.x + 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('J', '7', '-')) {
            return candidateConnector
        }
    }
    if (currentPosition.y > 0) {
        val candidateConnector = currentPosition.copy(y = currentPosition.y - 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('7', 'F', '|')) {
            return candidateConnector
        }
    }
    if (currentPosition.y < maze.size - 1) {
        val candidateConnector = currentPosition.copy(y = currentPosition.y + 1)
        if (maze[candidateConnector.y][candidateConnector.x] in charArrayOf('J', 'L', '|')) {
            return candidateConnector
        }
    }
    throw IllegalStateException("No connection to start")
}


private fun candidateNotInPipe(candidates: List<Point>, pipe: HashSet<Point>): Point? {
    return candidates.find { candidate ->
        !pipe.contains(candidate)
    }
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

private data class Point(val x: Int, val y: Int)
