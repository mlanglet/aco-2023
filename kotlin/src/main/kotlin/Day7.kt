import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val input = Files.readAllLines(Path.of("input/day7/day7.txt"))

    val cardValues = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()
    val partOne = input.map {
        val parts = it.split(" ")
        Hand(parts[0], parts[1].toInt(), cardValues)
    }
        .sorted()
        .mapIndexed { i, hand ->
            hand.rank = i + 1
            hand.winnings()
        }.reduce { acc, i -> acc + i }

    val cardValues2 = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()
    val partTwo = input.map {
        val parts = it.split(" ")
        HandV2(parts[0], parts[1].toInt(), cardValues2)
    }
        .sorted()
        .mapIndexed { i, hand ->
            hand.rank = i + 1
            hand.winnings()
        }.reduce { acc, i -> acc + i }

    println("Part one: $partOne")
    println("Part two: $partTwo")
}

class HandV2(cards: String, bid: Int, cardValues: List<Char>) : Hand(cards, bid, cardValues) {
    override fun determineType(): HandType {
        val cardCount = HashMap<Char, Int>(5)
        cards.filter {
            it != 'J'
        }.forEach {
            cardCount.merge(it, 1, Math::addExact)
        }

        var sameMax = 0
        if (cardCount.size > 0) {
            sameMax = cardCount.values.max()
        }

        val same2nd = if (cardCount.values.size > 1) {
            cardCount.values.sorted().reversed()[1]
        } else {
            0
        }

        sameMax = if (cards.contains('J')) {
            applyJokers(sameMax, same2nd)
        } else {
            sameMax
        }

        return selectType(sameMax, same2nd)
    }

    private fun applyJokers(sameMax: Int, same2nd: Int): Int {
        val jokers = cards.count { c -> c == 'J' }
        return if (jokers == 5 || jokers == 4) {
            5
        } else if (jokers == 3 && sameMax == 2) {
            5
        } else if (jokers == 3) {
            4
        } else if (jokers == 2 && sameMax == 3) {
            5
        } else if (jokers == 2 && sameMax == 2) {
            4
        } else if (jokers == 2) {
            3
        } else if (jokers == 1 && sameMax == 4) {
            5
        } else if (jokers == 1 && sameMax == 3) {
            4
        } else if (jokers == 1 && sameMax == 2 && same2nd == 2) {
            3
        } else if (jokers == 1 && sameMax == 2) {
            3
        } else {
            2
        }
    }
}

open class Hand(val cards: String, val bid: Int, val cardValues: List<Char>) : Comparable<Hand> {

    var rank: Int = 0
    val type: HandType

    init {
        type = determineType()
    }

    open protected fun determineType(): HandType {
        val cardCount = HashMap<Char, Int>(5)
        cards.forEach {
            cardCount.merge(it, 1, Math::addExact)
        }

        val sameMax = cardCount.values.max()
        val same2nd = if (cardCount.values.size > 1) {
            cardCount.values.sorted().reversed()[1]
        } else {
            0
        }

        return selectType(sameMax, same2nd)
    }

    protected fun selectType(sameMax: Int, same2nd: Int): HandType {
        return if (sameMax == 5) {
            HandType.FIVE_OF_A_KIND
        } else if (sameMax == 4) {
            HandType.FOUR_OF_A_KIND
        } else if (sameMax == 3 && same2nd == 2) {
            HandType.FULL_HOUSE
        } else if (sameMax == 3) {
            HandType.THREE_OF_A_KIND
        } else if (sameMax == 2 && same2nd == 2) {
            HandType.TWO_PAIR
        } else if (sameMax == 2) {
            HandType.PAIR
        } else {
            HandType.HIGH_CARD
        }
    }

    fun winnings(): Int = rank * bid

    override fun compareTo(other: Hand): Int {
        return if (type === other.type) {
            compareCards(other.cards)
        } else {
            type.compareTo(other.type)
        }
    }

    private fun compareCards(other: String): Int {
        cards.forEachIndexed { i, c ->
            if (cardValues.indexOf(c) < cardValues.indexOf(other[i])) {
                return -1
            } else if (cardValues.indexOf(c) > cardValues.indexOf(other[i])) {
                return 1
            }
        }
        return 0
    }
}

enum class HandType : Comparable<HandType> {
    HIGH_CARD,
    PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND,
}
