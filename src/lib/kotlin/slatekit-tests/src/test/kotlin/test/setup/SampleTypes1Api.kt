package test.setup

import slatekit.apis.Api
import slatekit.common.Result
import slatekit.common.Try
import slatekit.common.Notice
import slatekit.common.encrypt.Encryptor
import slatekit.results.Notice
import slatekit.results.Success
import slatekit.results.Try


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
    fun getSuccess()              : Notice<Movie>     = success(Movie.samples().first())
    fun getBadRequest()           : Notice<Movie>     = badRequest("Check your inputs")
    fun getNotFound()             : Notice<Movie>     = notFound("Item not found")
    fun getUnauthorized()         : Notice<Movie>     = unAuthorized("You can not edit this item")
    fun getConflict()             : Notice<Movie>     = conflict("Item has already been changed")
    fun getFailure()              : Notice<Movie>     = failure("Error finding item")
    fun getDeprecated()           : Notice<Movie>     = deprecated("This feature is deprecated")
    fun getUnexpected()           : Try<Movie>        = unexpectedError(Exception("Unexpected errro occured"))
}
