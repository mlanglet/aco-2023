import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

val digits = setOf('1', '2', '3', '4', '5', '6', '7', '8', '9')
val digitsAsStrings = listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
val digitPattern: Pattern = Pattern.compile("one|two|three|four|five|six|seven|eight|nine|[1-9]")

fun main() {
    val input = Files.readAllLines(Path.of("input/day1/day1.txt"))
    var partOne = 0
    var partTwo = 0
    input.forEach {
        partOne += "${it.find { c -> digits.contains(c) }}${it.findLast { c -> digits.contains(c) }}".toInt()
        partTwo += "${findFirstDigit(it)}${findLastDigit(it)}".toInt()
    }
    println("Part one: $partOne")
    println("Part two: $partTwo")
}

fun findFirstDigit(input: String): Int {
    val matcher = digitPattern.matcher(input)
    var firstDigit = "0"
    if (matcher.find()) {
        firstDigit = matcher.group()
    }
    return getDigitAsInt(firstDigit)
}

fun findLastDigit(input: String): Int {
    val matcher = digitPattern.matcher(input)
    var lastDigit = "0"
    while (matcher.find()) {
        lastDigit = matcher.group()
    }
    return getDigitAsInt(lastDigit)
}

fun getDigitAsInt(digit: String): Int {
    return if (digitsAsStrings.contains(digit)) {
        digitsAsStrings.indexOf(digit)
    } else {
        digit.toInt()
    }
}
