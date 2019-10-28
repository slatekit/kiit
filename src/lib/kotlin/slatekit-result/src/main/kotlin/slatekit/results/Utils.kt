package slatekit.results

fun Int.isInSuccessRange(): Boolean = this in Codes.SUCCESS.code..Codes.QUEUED.code
fun Int.isFilteredOut(): Boolean = this == Codes.IGNORED.code
fun Int.isInBadRequestRange(): Boolean = this in Codes.BAD_REQUEST.code..Codes.UNAUTHORIZED.code
fun Int.isInFailureRange(): Boolean = this in Codes.ERRORED.code..Codes.UNEXPECTED.code
