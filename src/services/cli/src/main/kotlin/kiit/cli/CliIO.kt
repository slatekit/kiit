/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.cli

import java.io.File
import kiit.utils.writer.TextType
import kiit.utils.writer.Writer
import kiit.common.ext.flatten
import kiit.common.types.Content
import kiit.common.types.ContentType
import kiit.common.io.Files
import kiit.common.io.IO
import kiit.common.types.Contents
import kiit.results.*

open class CliIO(private val io: IO<CliOutput, Unit>,
                 private val serializer:(Any?, ContentType) -> Content) : Writer {

    /**
     * Writes the text using the TextType
     *
     * @param mode
     * @param text
     * @param endLine
     */
    override fun write(mode: TextType, text: String, endLine: Boolean) {
        io.perform(CliOutput(mode, text, endLine))
    }

    /**
     * Different types of serializers
     */
    // private val serializerCsv  by lazy { SerializerCsv(this::serialize, true) }
    // private val serializerJson by lazy { SerializerJson(this::serialize, true) }
    // private val serializerProp by lazy { SerializerProps(false, this::serialize, true) }

    /**
     * Output the results of the response
     */
    fun output(result: Try<CliResponse<*>>, outputDir: String) {
        when (result) {
            is Failure -> {
                write(TextType.Failure, "error : " + result.error.toString())
            }
            is Success -> {
                val request = result.value.request
                val response = result.value
                when(response.success){
                    false -> ex(response)
                    true  -> {
                        when(response.value){
                            null -> empty()
                            else -> {
                                write(request, response, response.value, outputDir)
                                summary(response)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * prints empty result
     */
    private fun empty() {
        important("no results/data")
        line()
    }

    /**
     * prints summary of the result.
     *
     * @param result
     */
    private fun summary(result: CliResponse<*>) {
        val textType = if(result.success) TextType.Success else TextType.Failure
        write(textType, "Success : " + result.success)
        text( "Status  : " + result.code)
        text( "Message : " + result.desc)
        text( "Tag     : " + result.tag)
    }

    /**
     * prints an item ( non-recursive )
     *
     * @param obj
     */
    private fun write(request: CliRequest, cmd: CliResponse<*>, obj: Any?, outputDir: String) {
        val format = request.args.getSysString(SysParam.Format.id) ?: "prop"
        text("===============================")
        val contentType = ContentType.parse(format)
        val content = serializer(obj, contentType)
        val text = Contents.toText(content)
        text("RESULTS: $text")
        text("===============================")

        // Writer to log
        val log = request.args.getSysStringOrElse(SysParam.Log.id, "false")
        if (log.trim() == "true") {
            val fileName = Files.fileNameAsAsTimeStamp()
            val filePath = File(outputDir, fileName)
            filePath.writeText(text ?: "")
            text("Wrote content to: ${filePath.absolutePath}")
        }
    }

    /**
     * prints an item ( non-recursive )
     *
     * @param obj
     */
    private fun ex(response:CliResponse<*>) {
        summary(response)
        line()
        val ex = response.err
        when(ex){
            null -> { }
            is ExceptionErr -> {
                text("ERRORS")
                line()
                val flattened = ex.err.flatten()
                errs(flattened)
                line()
            }
            is StatusException -> {
                failure(ex.message ?: "Status error: $ex")
            }
            else -> {
                failure(ex.message ?: "Unexpected error")
            }
        }
    }


    private fun errs(all:List<Err>){
        all.forEachIndexed { ndx, err ->
            val num = (ndx + 1).toString()
            when(err){
                is Err.ErrorInfo  -> failure("$num. Error   : " + err.msg)
                is Err.ErrorField -> failure("$num. Field   : name=" + err.field + ", value=" + err.value + ", msg=" + err.msg)
                else              -> failure("$num. ${err.msg}")
            }
        }
    }
}
