import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.max

fun main() {
    val input = Files.readAllLines(Path.of("input/day2/day2.txt"))
    var partOne = 0
    var partTwo = 0
    input.forEach { game ->
        val parts = game.split(":")
        val gameId = parts[0].split(" ")[1].toInt()
        val rounds = parts[1].split(";")
        var maxRed = 0
        var maxGreen = 0
        var maxBlue = 0
        rounds.forEach { round ->
            val colors = round.split(",")
            colors.forEach { color ->
                val count = color.split(" ")[1].toInt()
                if (color.contains("red")) {
                    maxRed = max(maxRed, count)
                }
                if (color.contains("green")) {
                    maxGreen = max(maxGreen, count)
                }
                if (color.contains("blue")) {
                    maxBlue = max(maxBlue, count)
                }
            }
        }
        if (maxRed <= 12 && maxGreen <= 13 && maxBlue <= 14) {
            partOne += gameId
        }
        partTwo += maxRed * maxGreen * maxBlue
    }
    println("Part one: $partOne")
    println("Part two: $partTwo")
}
