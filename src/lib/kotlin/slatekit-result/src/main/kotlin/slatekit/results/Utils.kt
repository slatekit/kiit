package slatekit.results



fun Int.isInSuccessRange(): Boolean = this in StatusCodes.SUCCESS.code .. StatusCodes.QUEUED.code
fun Int.isFilteredOut(): Boolean = this == StatusCodes.IGNORED.code
fun Int.isInBadRequestRange(): Boolean = this in StatusCodes.BAD_REQUEST.code .. StatusCodes.UNAUTHORIZED.code
fun Int.isInFailureRange(): Boolean = this in StatusCodes.ERRORED.code .. StatusCodes.UNEXPECTED.code