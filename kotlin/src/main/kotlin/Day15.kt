import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern


fun main() {
    val input = Files.readAllLines(Path.of("input/day15/day15.txt"))
    var partOne = 0
    val boxes = HashMap<Int, ArrayList<String>>(256)
    input.forEach { row ->
        row.split(",").forEach { step ->
            partOne += HASH(step)


            val operationExtractor = "[-|=]".toPattern()
            val matcher = operationExtractor.matcher(step)
            matcher.find()
            val operation = matcher.group().toCharArray()[0]
            val value = step.replace("-", " ").replace("=", " ")
            val label = value.split(" ")[0]
            val boxId = HASH(label)

            when (operation) {
                '-' -> {
                    if (boxes[boxId] != null) {
                        val boxLabel = boxes[boxId]!!.find { it.startsWith(label) }
                        if (boxLabel != null) {
                            boxes[boxId]?.removeAt(boxes[boxId]!!.indexOf(boxLabel))
                        }
                    }
                }

                '=' -> {
                    if (boxes[boxId] == null) {
                        boxes[boxId] = arrayListOf(value)
                    } else {
                        val boxLabel = boxes[boxId]!!.find { it.startsWith(label) }
                        if (boxLabel != null) {
                            boxes[boxId]!![boxes[boxId]!!.indexOf(boxLabel)] = value
                        } else {
                            boxes[boxId]!!.add(value)
                        }
                    }
                }

                else -> throw IllegalStateException("Unknown operation $operation")
            }
        }
    }

    val partTwo = boxes.map { entry ->
        println("Box ${entry.key}: ${entry.value}")
        entry.value.mapIndexed { index, lens ->
            (1 + entry.key) * (index + 1) * lens.split(" ")[1].toInt()
        }
    }.flatten().sum()


    println("Part one: $partOne")
    println("Part two: $partTwo") // 5139 too low
}

fun HASH(step: String): Int {
    var hashValue = 0
    step.forEach { char ->
        hashValue += char.code
        hashValue *= 17
        hashValue %= 256
    }

    return hashValue
}
