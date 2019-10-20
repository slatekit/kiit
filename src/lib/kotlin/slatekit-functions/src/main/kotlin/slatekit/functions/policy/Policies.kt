package slatekit.functions.policy

import slatekit.results.Outcome

/**
 * Policy to rewrite / transform the input before applying the operation
 * @param I : Input type
 * @param O : Output type
 */
object Policies {

    fun <I, O> chain(all: List<Policy<I, O>>, last: suspend (I) -> Outcome<O>): suspend (I) -> Outcome<O> {
        return all.foldRight(last) { acc, call ->
            compose(acc, call)
        }
    }


    fun <I, O> compose(p: Policy<I, O>, op: suspend (I) -> Outcome<O>): suspend (I) -> Outcome<O> {
        return { i ->
            p.run(i, op)
        }
    }
}