package slatekit.functions.policy

import slatekit.results.Outcome

/**
 * @param I : Input type
 */
typealias Check<I> = (I) -> Outcome<I>