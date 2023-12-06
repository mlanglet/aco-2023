import java.nio.file.Files
import java.nio.file.Path
import java.util.LinkedList
import java.util.regex.Pattern
import kotlin.math.pow

val whiteSpace = Pattern.compile("\\s+")

fun main() {
    val input = Files.readAllLines(Path.of("input/day4/day4.txt"))
    var partOne = 0
    val cards = HashMap<Int, ScratchCard>()
    input.forEach { card ->
        val parts = card.split(":")
        val cardId = parts[0].split(whiteSpace)[1].toInt()
        val numberSections = parts[1].split("|")
        val winningNumbers = numberSections[0].trim().split(whiteSpace).map { it.toInt() }.toSet()
        val numbers = numberSections[1].trim().split(whiteSpace).map { it.toInt() }.toSet()
        val intersection = winningNumbers.intersect(numbers)
        partOne += 2.0.pow((intersection.size - 1).toDouble()).toInt()
        cards[cardId] = ScratchCard(cardId, intersection.size)
    }

    val totalCards = cards.values.toCollection(java.util.ArrayList<ScratchCard>())
    collectWinnings(cards, totalCards)

    println("Part one: $partOne")
    println("Part two: ${totalCards.size}")
}

fun collectWinnings(cards: Map<Int, ScratchCard>, totalCards: ArrayList<ScratchCard>){
    cards.forEach { card ->
        collectWinnings(card.key, cards, totalCards)
    }
}

fun collectWinnings(cardId: Int, cards: Map<Int, ScratchCard>, totalCards: ArrayList<ScratchCard>){
    (1..cards[cardId]!!.numberOfWinningNumbers).forEach {
        val copyId = it + cardId
        if (copyId in cards){
            totalCards.add(cards[copyId]!!)
            collectWinnings(copyId, cards, totalCards)
        }
    }
}

data class ScratchCard(val id: Int, val numberOfWinningNumbers: Int)
