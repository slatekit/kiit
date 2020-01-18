/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.cli

import java.io.File
import slatekit.common.console.TextType
import slatekit.common.console.Writer
import slatekit.common.types.Content
import slatekit.common.types.ContentType
import slatekit.common.io.Files
import slatekit.common.io.IO
import slatekit.common.serialization.Serializer
import slatekit.results.Failure
import slatekit.results.Success
import slatekit.results.Try

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
                response.value?.let { value ->
                    write(request, response, value, outputDir)
                    summary(response)
                } ?: empty()
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
        text("Success : " + result.success)
        text("Status  : " + result.code)
        text("Message : " + result.msg)
        text("Tag     : " + result.tag)
    }

    /**
     * prints an item ( non-recursive )
     *
     * @param obj
     */
    private fun write(request: CliRequest, cmd: CliResponse<*>, obj: Any?, outputDir: String) {
        val format = request.args.getStringOrElse(SysParam.Format.id, "prop")
        text("===============================")
        val contentType = ContentType.parse(format)
        val content = serializer(obj, contentType)
        val text = content.text
        text(text)
        text("===============================")

        // Writer to log
        val log = request.args.getSysStringOrElse(SysParam.Log.id, "false")
        if (log.trim() == "true") {
            val fileName = Files.fileNameAsAsTimeStamp()
            val filePath = File(outputDir, fileName)
            filePath.writeText(text)
            text("Wrote content to: ${filePath.absolutePath}")
        }
    }

    /**
     * recursive serialization for a object.
     *
     * @param item: The object to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param depth: The the depth of this object in a nested heirarchy
     */
    private fun serialize(serializer: Serializer, item: Any, depth: Int) {

        // Handle enum
        if (item is Enum<*>) {
            val enumVal = (item).ordinal
            serializer.serializeValue(enumVal, depth)
            return
        }

        // Begin
        serializer.onContainerStart(item, Serializer.ParentType.OBJECT_TYPE, depth)

        // Get fields
        val fields = item.javaClass.declaredFields

        // Standardize the display of the props
        val maxLen = if (serializer.standardizeWidth) {
            fields.maxBy { it.name.length }?.name?.length ?: 0
        } else {
            0
        }

        fields.forEachIndexed { index, field ->
            // Get name/value
            val propName = field.name.trim()

            // Standardized width
            val finalPropName = if (serializer.standardizeWidth) {
                propName.padEnd(maxLen)
            } else {
                propName
            }
            val value = field.get(item)

            // Entry
            serializer.onMapItem(item, depth, index, finalPropName, value)
        }

        // End
        serializer.onContainerEnd(item, Serializer.ParentType.OBJECT_TYPE, depth)
    }
}
