package slatekit.sampleapp.core.apis

import slatekit.apis.Api
import slatekit.common.Result
import slatekit.common.encrypt.Encryptor
import slatekit.common.results.ResultFuncs.badRequest
import slatekit.common.results.ResultFuncs.conflict
import slatekit.common.results.ResultFuncs.deprecated
import slatekit.common.results.ResultFuncs.failure
import slatekit.common.results.ResultFuncs.notFound
import slatekit.common.results.ResultFuncs.success
import slatekit.common.results.ResultFuncs.unAuthorized
import slatekit.common.results.ResultFuncs.unexpectedError
import slatekit.sampleapp.core.models.Movie


@Api(area = "samples", name = "types1", desc = "sample api to test getting different values")
class SampleTypes1Api(val enc: Encryptor) {

    /**
     * GET localhost:{port}/samples/types1/basic_string
     * GET localhost:{port}/samples/types1/basic_list
     */
    fun getBasicString()          : String            = "string"
    fun getBasicBoolean()         : Boolean           = true
    fun getBasicLong()            : Long              = 20L
    fun getBasicDouble()          : Double            = 123.45
    fun getEncrypted(text:String) : String            = enc.encrypt(text)
    fun getResult()               : Result<Int>       = success(12345, msg = "result object")
    fun getPair()                 : Pair<String,Long> = Pair("abc", 123)
    fun getBasicList()            : List<String>      = listOf("a", "b", "c")
    fun getBasicMap()             : Map<String,Int>   = mapOf("a" to 1, "b" to 2, "c" to 3)
    fun getObject()               : Movie             = Movie.samples()[0]
    fun getListOfObject()         : List<Movie>       = Movie.samples()

    /**
     * These examples use the Slate Kit Result<T> to model success and failures.
     * The Result<T> has status codes that are HTTP compliant
     */
    fun getSuccess()              : Result<Movie>     = success(Movie.samples().first())
    fun getBadRequest()           : Result<Movie>     = badRequest("Check your inputs")
    fun getNotFound()             : Result<Movie>     = notFound("Item not found")
    fun getUnauthorized()         : Result<Movie>     = unAuthorized("You can not edit this item")
    fun getConflict()             : Result<Movie>     = conflict("Item has already been changed")
    fun getFailure()              : Result<Movie>     = failure("Error finding item")
    fun getDeprecated()           : Result<Movie>     = deprecated("This feature is deprecated")
    fun getUnexpected()           : Result<Movie>     = unexpectedError("Unexpected errro occured")
}
