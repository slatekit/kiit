package slatekit.functions

import slatekit.results.Failure
import slatekit.results.Outcome
import slatekit.results.Success

class Processor<I, O> {

    suspend fun input(items: List<Input<I>>, start: Outcome<I>): Outcome<I> {
        return when (start) {
            is Failure -> start
            is Success -> {
                when (items.isEmpty()) {
                    true -> start
                    false -> process(0, items.size - 1, start) { ndx, v ->
                        val processor = items[ndx]
                                processor.process(v)
                    }
                }
            }
        }
    }

    suspend fun output(raw:I, input: Outcome<I>, output: Outcome<O>, items: List<Output<I, O>>): Outcome<O> {
        return when (output) {
            is Failure -> output
            is Success -> {
                when (items.isEmpty()) {
                    true -> output
                    false -> process(0, items.size - 1, output) { ndx, v ->
                        val processor = items[ndx]
                                processor.process(raw, input, v)
                    }
                }
            }
        }
    }

    /**
     * "takeWhile" iteration alternative.
     * this provides a way for the caller to dictate the next index.
     *
     * NOTE: this is ideal for low-level character / string / lexical parsing
     * @param condition
     */

    private tailrec suspend fun <T> process(ndx: Int, end: Int, startValue: Outcome<T>, condition: suspend (Int, Outcome<T>) -> Outcome<T>): Outcome<T> {
        val result = condition(ndx, startValue)
        return if (!result.success)
            result
        else if (ndx >= end)
            result
        else
            process(ndx + 1, end, result, condition)
    }
}
