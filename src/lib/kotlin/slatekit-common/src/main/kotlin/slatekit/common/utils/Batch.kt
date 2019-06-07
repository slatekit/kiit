package slatekit.common.utils

interface Batch<T> {

    fun items():List<T>


    fun batches(batchSize:Int): Int {
        val all = items()
        val totalBatches = all.size / batchSize
        val remainder = all.size % batchSize
        return when {
            remainder > 0  -> totalBatches + 1
            else           -> totalBatches
        }
    }


    fun extract(start: Int, batch: Int): List<T> {
        val end = start + batch
        return items().subList(start, end)
    }


    fun hasRemainder(batchSize: Int): Boolean {
        return items().size % batchSize > 0
    }


    fun batch(batch: Int, batchSize: Int): List<T> {

        val all = items()
        if (all.isEmpty())
            return listOf()

        // CASE 1: fewer
        if (all.size < batchSize) {
            return items().toList()
        }

        // CASE 2: = batch size
        if (all.size == batchSize) {
            return items().subList(0, batchSize)
        }

        // CASE 3: > batch size
        // Which batch to get ?
        val totalBatches = batches(batchSize)

        // CASE 3A: 1st batch
        if (batch <= 1)
            return all.subList(0, batchSize)

        // Fix invalid arg ( rather than throwing/returning nothing )
        val batchNumber = if (batch > totalBatches) totalBatches else batch

        // CASE 3B: Last batch with imperfect multiple of items
        // batch size = 7 and total = 15, batchNumber = 3 ( 1 remaining )
        // batch size = 7 and total = 23, batchNumber = 4 ( 2 remaining )
        val hasRemainder = hasRemainder(batchSize)
        val isLastBatch = batchNumber == totalBatches

        // No remainder ( e.g. batch size = 7 and total = 14 or 21.
        // Perfect multiple
        if (isLastBatch && hasRemainder) {
            val start = all.size - batchSize
            return extract(start, batchSize)
        }

        // CASE 3C: either 2nd page or last page as perfect multiple of batch size
        val start = (batchNumber - 1) * batchSize
        return extract(start, batchSize)
    }
}