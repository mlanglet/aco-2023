import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

val numberPattern = Pattern.compile("\\d+")
val symbolPattern = Pattern.compile("[^\\d|^[.]]?")

fun main() {
    val input = Files.readAllLines(Path.of("input/day3/day3.txt"))
    var partOne = 0
    var partTwo = 0
    var prev2Numbers = emptyList<PartNumber>()
    var prevNumbers = emptyList<PartNumber>()
    var prevSymbols = emptyList<Symbol>()
    input.forEach { line ->
        val numberMatcher = numberPattern.matcher(line)
        val numbers = ArrayList<PartNumber>()
        while (numberMatcher.find()) {
            numbers.add(PartNumber(numberMatcher.start(), numberMatcher.end(), numberMatcher.group().toInt()))
        }

        val symbolMatcher = symbolPattern.matcher(line)
        val symbols = ArrayList<Symbol>()
        while (symbolMatcher.find()) {
            if (symbolMatcher.group().isNotBlank()) {
                symbols.add(Symbol(symbolMatcher.start(), symbolMatcher.group()))
            }
        }

        for (symbol in symbols) {
            for (number in numbers) {
                if (isAdjacent(number, symbol)) {
                    partOne += number.value
                }
            }

            for (number in prevNumbers) {
                if (isAdjacent(number, symbol)) {
                    partOne += number.value
                }
            }
        }

        for (symbol in prevSymbols) {
            for (number in numbers) {
                if (isAdjacent(number, symbol)) {
                    partOne += number.value
                }
            }

            val adjacentNumbers = ArrayList<PartNumber>()
            if (symbol.value == "*") {
                for (number in numbers) {
                    if (isAdjacent(number, symbol)) {
                        adjacentNumbers.add(number)
                    }
                }
                for (number in prevNumbers) {
                    if (isAdjacent(number, symbol)) {
                        adjacentNumbers.add(number)
                    }
                }
                for (number in prev2Numbers) {
                    if (isAdjacent(number, symbol)) {
                        adjacentNumbers.add(number)
                    }
                }
                if (adjacentNumbers.size == 2) {
                    partTwo += adjacentNumbers[0].value * adjacentNumbers[1].value
                }
            }
        }

        prev2Numbers = prevNumbers
        prevNumbers = numbers
        prevSymbols = symbols
    }
    println("Part one: $partOne")
    println("Part two: $partTwo")
}

fun isAdjacent(partNumber: PartNumber, symbol: Symbol): Boolean {
    return !(partNumber.start - 1 > symbol.position || partNumber.end < symbol.position)
}

data class PartNumber(val start: Int, val end: Int, val value: Int)
data class Symbol(val position: Int, val value: String)
