package slatekit.policy

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
                        val result = processor.process(v)
                        result
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
                        val result = processor.process(raw, input, v)
                        result
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


    companion object {
        fun <I, O> chain(all: List<Process<I, O>>, last: suspend (I) -> Outcome<O>): suspend (I) -> Outcome<O> {
            return all.foldRight(last) { acc, call ->
                compose(acc, call)
            }
        }

        fun <I, O> compose(p: Process<I, O>, op: suspend (I) -> Outcome<O>): suspend (I) -> Outcome<O> {
            return { i ->
                p.process(i, op)
            }
        }
    }
}
