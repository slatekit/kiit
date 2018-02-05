package test.common

import slatekit.apis.*
import slatekit.apis.support.ApiBase
import slatekit.common.*
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import slatekit.common.results.ResultFuncs.success
import slatekit.common.types.Email
import slatekit.common.types.PhoneUS
import slatekit.integration.common.AppEntContext
import slatekit.sampleapp.core.models.Movie


@Api(area = "app", name = "tests", desc = "sample to test features of Slate Kit APIs", roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class SampleApi(context: AppEntContext): ApiBase(context) {

    // For unit-tests
    var inc = 0


    @ApiAction(desc = "accepts supplied basic data types from request", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun hello(greeting: String): String {
        return "$greeting back"
    }


    @ApiAction(desc = "increments a simple counter", roles = "", verb = "@parent", protocol = "@parent")
    fun counter(): Int {
        inc += 1
        return inc
    }


    // SAMPLE API ACTIONS:
    // These sample API actions/methods serve as an example of handling different types
    // of use cases when dealing with requests/end-points. These use-cases include
    // 1. request parameter types  : taking in basic types, lists, maps, objects etc
    // 2. response outputs types   : returning basic types, lists, maps, objects etc
    // 3. encrypted inputs         : auto-decrypting encrypted ints, doubles, strings
    // 5. using raw request        : handling the request intead of auto-conversion of parameters
    // 4. roles / security         : enforcing security via roles ( different auth models available )
    // 6. file handling            : accepting a file input
    // 7. error-handling           : handling errors either at the method, api level, globally
    // 8. reference to parent      : specifying meta-data using parent references via @parent
    //====================================================================================
    // INPUT / PARAMETER TESTS
    //====================================================================================
    @ApiAction(desc = "accepts supplied basic data types from request", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun inputBasicTypes(string1: String, bool1: Boolean, numShort: Short, numInt: Int, numLong: Long, numFloat: Float, numDouble: Double, date: DateTime): String {
        return "$string1, $bool1, $numShort $numInt, $numLong, $numFloat, $numDouble, $date"
    }


    @ApiAction(desc = "access the request model directly instead of auto-conversion", roles= "*", verb = "post", protocol = "@parent")
    fun inputRequest(req: Request): Result<String> {
        return success("ok", "raw request id: " + req.data!!.getInt("id"))
    }


    @ApiAction(desc = "auto-convert json to objects", roles= "*", verb = "post", protocol = "@parent")
    fun inputObject(movie: Movie): Movie {
        return movie
    }


    @ApiAction(desc = "auto-convert json to objects", roles= "*", verb = "post", protocol = "@parent")
    fun inputObjectlist(movies:List<Movie>): List<Movie> {
        return movies
    }


    @ApiAction(desc = "accepts a list of strings from request", roles= "*", verb = "post", protocol = "@parent")
    fun inputListString(items:List<String>): Result<String> {
        return success("ok", items.fold("", { acc, curr -> acc + "," + curr } ))
    }


    @ApiAction(desc = "accepts a list of integers from request", roles= "*", verb = "post", protocol = "@parent")
    fun inputListInt(items:List<Int>): Result<String> {
        return success("ok", items.fold("", { acc, curr -> acc + "," + curr.toString() } ))
    }


    @ApiAction(desc = "accepts a map of string/ints from request", roles= "*", verb = "post", protocol = "@parent")
    fun inputMapInt(items:Map<String,Int>): Result<String> {
        val sortedPairs = items.keys.toList().sortedBy{ k:String -> k }.map{ key -> Pair(key, items[key]) }
        val delimited = sortedPairs.fold("", { acc, curr -> acc + "," + curr.first + "=" + curr.second } )
        return success("ok", delimited)
    }


    @ApiAction(desc = "accepts an encrypted int that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecInt(id: EncInt): Result<String> {
        return success("ok", "decrypted int : " + id.value)
    }


    @ApiAction(desc = "accepts an encrypted long that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecLong(id: EncLong): Result<String> {
        return success("ok", "decrypted long : " + id.value)
    }


    @ApiAction(desc = "accepts an encrypted double that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecDouble(id: EncDouble): Result<String>
    {
        return success("ok", "decrypted double : " + id.value)
    }


    @ApiAction(desc = "accepts an encrypted string that will be decrypted", roles= "*", verb = "@parent", protocol = "@parent")
    fun inputDecString(id: EncString): Result<String>
    {
        return success("ok", "decrypted string : " + id.value)
    }


    @ApiAction(desc = "accepts a smart string of phone", roles= "?", verb = "@parent", protocol = "@parent")
    fun smartStringPhone(text: PhoneUS): String = "${text.isValid} - ${text.isEmpty} - ${text.text}"


    @ApiAction(desc = "accepts a smart string of email", roles= "?", verb = "@parent", protocol = "@parent")
    fun smartStringEmail(text: Email): String = "${text.isValid} - ${text.isEmpty} - ${text.text}"


    //====================================================================================
    // OUTPUT / RESULT TESTS
    //====================================================================================
    @ApiAction(desc = "test getting return type result[T]", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputResult(): Result<Int> = success(12345, msg="result object")


    @ApiAction(desc = "test getting return type string", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputBasicString(): String = "string"


    @ApiAction(desc = "test getting return type boolean", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputBasicBoolean(): Boolean  = true


    @ApiAction(desc = "test getting return type long", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputBasicLong(): Long = 20L


    @ApiAction(desc = "test getting return type double", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputBasicDouble(): Double = 123.45


    @ApiAction(desc = "test getting return type pair", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputPair(): Pair<String,Long> = Pair("abc", 123)


    @ApiAction(desc = "test encrypting the input value", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputEncrypted(text:String): String = context.enc?.encrypt(text) ?: text


    @ApiAction(desc = "test getting return type list of basic types", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputBasicList(): List<String> = listOf("a", "b", "c")


    @ApiAction(desc = "test getting return type map of basic types", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputBasicMap(): Map<String,Int> = mapOf("a" to 1, "b" to 2, "c" to 3)


    @ApiAction(desc = "test getting return type custom object", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputObject(): Movie = Movie.samples()[0]


    @ApiAction(desc = "test getting return type list of custom objects", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputListOfObject(): List<Movie> = Movie.samples()


    @ApiAction(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputContentCsv(): Content = Content.csv("user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @ApiAction(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputContentHtml(): Content = Content.html("<html><head><title>content html</title></head><body>Explicitly set content type</body></html>")


    @ApiAction(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputContentText(): Content = Content.text("user: kishore")


    @ApiAction(desc = "test getting content as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputContentXml(): Content = Content.xml("<user><name>kishore</name></user>")


    @ApiAction(desc = "test getting Doc as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputDocCsv(): Doc = Doc.csv("file1.csv", "user1,u1@a.com,true,1234\r\nuser2,u2@a.com,true,1234")


    @ApiAction(desc = "test getting Doc as xml", roles = "@parent", verb = "@parent", protocol = "@parent")
    fun outputDocHtml(): Doc = Doc.html("file1.html", "<html><head><title>Doc html</title></head><body>Explicitly set Doc type</body></html>")


    @ApiAction(desc = "test getting Doc as xml", roles = "", verb = "@parent", protocol = "@parent")
    fun outputDocText(): Doc = Doc.text("file1.txt", "user: kishore")


    @ApiAction(desc = "test getting Doc as xml", roles = "", verb = "@parent", protocol = "@parent")
    fun outputDocXml(): Doc = Doc.xml("file1.xml", "<user><name>kishore</name></user>")


    //====================================================================================
    // ROLES
    //====================================================================================
    @ApiAction(desc = "no roles allows access by anyone", roles= "", verb = "@parent", protocol = "@parent")
    fun rolesNone(code:Int, tag:String): String {
        return "rolesNone $code $tag"
    }


    @ApiAction(desc = "* roles allows access by any authenticated in user", roles= "*", verb = "@parent", protocol = "@parent")
    fun rolesAny(code:Int, tag:String): String {
        return "rolesAny $code $tag"
    }


    @ApiAction(desc = "allows access by specific role", roles= "dev", verb = "@parent", protocol = "@parent")
    fun rolesSpecific(code:Int, tag:String): String  {
        return "rolesSpecific $code $tag"
    }


    @ApiAction(desc = "@parent refers to its parent role", roles= "@parent", verb = "@parent", protocol = "@parent")
    fun rolesParent(code:Int, tag:String): String {
        return "rolesParent $code $tag"
    }


    //====================================================================================
    // FILES
    //====================================================================================
    @ApiAction(desc = "accepts auto-extracted the file as a document", roles= "*", verb = "post", protocol = "@parent")
    fun fileSingle(doc: Doc): String {
        return doc.content
    }


    @ApiAction(desc = "accepts auto-extracted multiple files as documents", roles= "*", verb = "post", protocol = "@parent")
    fun fileMultiple(docs: List<Doc>): List<String> {
        return docs.map { it.content }
    }
}
