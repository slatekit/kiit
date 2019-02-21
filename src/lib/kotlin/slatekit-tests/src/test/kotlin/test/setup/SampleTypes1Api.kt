package test.setup

import slatekit.apis.Api
import slatekit.common.encrypt.Encryptor
import slatekit.results.Notice
import slatekit.results.StatusCodes
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.builders.Notices
import slatekit.results.builders.Tries


@Api(area = "samples", name = "types1", desc = "sample api to test getting different values")
class SampleTypes1Api {

    val enc: Encryptor = AppEncryptor

    /**
     * GET localhost:{port}/samples/types1/basic_string
     * GET localhost:{port}/samples/types1/basic_list
     */
    fun getBasicString()          : String            = "string"
    fun getBasicBoolean()         : Boolean           = true
    fun getBasicLong()            : Long              = 20L
    fun getBasicDouble()          : Double            = 123.45
    fun getEncrypted(text:String) : String            = enc.encrypt(text)
    fun getResult()               : Notice<Int>       = Success(12345, msg = "result object")
    fun getPair()                 : Pair<String,Long> = Pair("abc", 123)
    fun getBasicList()            : List<String>      = listOf("a", "b", "c")
    fun getBasicMap()             : Map<String,Int>   = mapOf("a" to 1, "b" to 2, "c" to 3)
    fun getObject()               : Movie = Movie.samples()[0]
    fun getListOfObject()         : List<Movie>       = Movie.samples()

    /**
     * These examples use the Slate Kit Result<T> to model success and failures.
     * The Result<T> has status codes that are HTTP compliant
     */
    fun getSuccess()              : Notice<Movie>     = Success(Movie.samples().first())
    fun getBadRequest()           : Notice<Movie>     = Notices.invalid("Check your inputs")
    fun getNotFound()             : Notice<Movie>     = Notices.errored("Item not found", StatusCodes.NOT_FOUND)
    fun getUnauthorized()         : Notice<Movie>     = Notices.denied ("You can not edit this item")
    fun getConflict()             : Notice<Movie>     = Notices.errored("Item has already been changed", StatusCodes.CONFLICT)
    fun getFailure()              : Notice<Movie>     = Notices.errored("Error finding item")
    fun getDeprecated()           : Notice<Movie>     = Notices.errored("This feature is deprecated", StatusCodes.DEPRECATED)
    fun getUnexpected()           : Try<Movie>        = Tries.unexpected(Exception("Unexpected errro occured"))
}
