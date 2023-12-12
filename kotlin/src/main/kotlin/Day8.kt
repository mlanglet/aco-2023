import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap


fun main() {
    val input = Files.readAllLines(Path.of("input/day8/day8.txt"))
    var instruction = ""
    val map = HashMap<String, Pair<String, String>>()
    input.forEach {
        if (instruction == "") {
            instruction = it
            return@forEach
        }

        if (it.isBlank()) {
            return@forEach
        }

        val parts = it.split("=")
        val key = parts[0].trim()
        val pairParts = parts[1].trim().trim('(', ')').split(", ")
        map[key] = Pair(pairParts[0], pairParts[1])
    }

    println("Part one: ${countDistanceBetweenPairs(instruction, map)}")
    println("Part two: ${partTwo(instruction, map)}")
}

private fun countDistanceBetweenPairs(
    instruction: String,
    map: HashMap<String, Pair<String, String>>,
): Int {
    var partOne = 0
    var node = "AAA"
    while (true) {
        for (c in instruction) {
            when (c) {
                'L' -> node = map[node]!!.first
                'R' -> node = map[node]!!.second
            }
            partOne++
            if (node == "ZZZ") {
                return partOne
            }
        }
    }
}

private fun partTwo(
    instruction: String,
    map: HashMap<String, Pair<String, String>>
): Long {
    val nodes = map.keys.filter { it.endsWith("A") }
    val results = ConcurrentHashMap<String, Long>()

    runBlocking {
        withContext(Dispatchers.Default) {
            nodes.map {
                launch {
                    nodeProcessor(it, instruction, map, results)
                }
            }
        }
    }

    return findLCM(results.values.toList())
}

private fun findLCM(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = findLCM(result, numbers[i])
    }
    return result
}

private fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

private fun nodeProcessor(
    startingNode: String,
    instruction: String,
    map: HashMap<String, Pair<String, String>>,
    results: ConcurrentHashMap<String, Long>,
) {
    var node = startingNode
    var counter = 0L
    while (true) {
        for (c in instruction) {
            when (c) {
                'L' -> node = map[node]!!.first
                'R' -> node = map[node]!!.second
            }
            counter++
            if (node.endsWith("Z")) {
                results[startingNode] = counter
                return
            }
        }
    }
}

