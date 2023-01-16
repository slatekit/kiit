package kiit.common.ext

import kiit.results.Err


/**
 * Builds a list of errs:
 * error a
 * error b
 * error c
 */
fun Err.strings(): List<String> {
    val all = this.flatten()
    return all.map { err ->
        when(err){
            is Err.ErrorInfo  -> "Error   : " + err.msg
            is Err.ErrorField -> "Field   : name=" + err.field + ", value=" + err.value + ", msg=" + err.msg
            else              -> "${err.msg}"
        }
    }
}


/**
 * Builds a list of numbered errs:
 * 1. error a
 * 2. error b
 * 3. error c
 */
fun Err.numbered(): List<String> {
    return this.strings().mapIndexed { ndx, it -> "${ndx + 1}. $it"}
}


/**
 * Recursively flattens the err into the list provided.
 * E.g. Err could be
 * 1. @see[kiit.results.Err.ErrorInfo]
 * 2. @see[kiit.results.Err.ErrorField]
 * 3. @see[kiit.results.Err.ErrorList]
 */
fun Err.flatten(): List<Err> {
    return when (this) {
        is Err.ErrorField -> listOf(this)
        is Err.ErrorInfo -> listOf(this)
        is Err.ErrorList -> {
            val errs = this.errors
            val childErrors = errs.map { it.flatten() }
            val allErrors = childErrors.flatten()
            allErrors
        }
    }
}