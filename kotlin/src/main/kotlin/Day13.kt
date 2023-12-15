import java.nio.file.Files
import java.nio.file.Path


fun main() {
    val input = Files.readAllLines(Path.of("input/day13/day13.txt"))
    var partOne = 0
    var partTwo = 0

    val mirrors = mutableListOf<List<String>>()
    var mirror = mutableListOf<String>()
    input.forEach { row ->
        if (row.isEmpty()) {
            mirrors.add(mirror)
            mirror = mutableListOf()
        } else {
            mirror.add(row)
        }
    }
    mirrors.add(mirror)

    mirrors.forEach { m ->
        var isPerfectRowReflection = true
        val prevRows: List<String> = mutableListOf()
        var rowMatchCount = 0
        var rowCount = 0
        var rowMatchMiddleIndex = 0
        m.forEach { row ->
            if (prevRows.isNotEmpty() && row == prevRows.last()) {
                rowMatchMiddleIndex = rowCount
                rowMatchCount++
            } else if (rowMatchMiddleIndex > 0) {
                val mirrorIndex = rowMatchMiddleIndex - rowMatchCount - 1
                if (mirrorIndex < 0 || mirrorIndex >= m.size && isPerfectRowReflection){
                } else if(row == prevRows[mirrorIndex]) {
                    rowMatchCount++
                } else {
                    isPerfectRowReflection = false
                }
            }

            prevRows.addLast(row)
            rowCount++
        }

        if(isPerfectRowReflection){
            partOne += rowMatchMiddleIndex * 100
        }

        var isPerfectColumnReflection = true
        val prevColumns: List<String> = mutableListOf()
        var columnMatchCount = 0
        var columnCount = 0
        var columnMatchMiddleIndex = 0
        (0..<m.first().length).forEach { columnIndex ->
            val columnBuilder = StringBuilder()
            m.forEach { row ->
                columnBuilder.append(row[columnIndex])
            }
            val column = columnBuilder.toString()

            if(prevColumns.isNotEmpty() && column == prevColumns.last()){
                columnMatchMiddleIndex = columnCount
                columnMatchCount++
            } else if (columnMatchMiddleIndex > 0) {
                val columnMirrorIndex = columnMatchMiddleIndex - columnMatchCount - 1
                if(columnMirrorIndex < 0 || columnMirrorIndex >= m.first().length && isPerfectColumnReflection){
                } else if (column == prevColumns[columnMirrorIndex]) {
                    columnMatchCount++
                } else {
                    isPerfectColumnReflection = false
                }
            }

            prevColumns.addLast(column)
            columnCount++
        }

        if(isPerfectColumnReflection){
            partOne += columnMatchMiddleIndex
        }
    }

    println("Part one: $partOne") // 24662 too low
    println("Part two: $partTwo")
}
