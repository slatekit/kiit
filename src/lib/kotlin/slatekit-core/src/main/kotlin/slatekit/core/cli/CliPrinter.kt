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

package slatekit.core.cli


import slatekit.common.Files
import slatekit.common.Result
import slatekit.common.console.ConsoleWriter
import slatekit.common.serialization.SerializerProps
import slatekit.meta.Serialization
import java.io.File


class CliPrinter(val _writer: ConsoleWriter) {

    private val serializerProp = Serialization.props(true)
    private val serializerJson = Serialization.json()
    private val serializerCsv  = Serialization.csv()


    fun printResult(cmd:CliCommand, result: Result<Any>, outputDir:String): Unit {
        result.value?.let { value ->
            printAny(cmd, value, outputDir)
            printSummary(result)
        } ?: printEmpty()
    }


    /**
     * prints empty result
     */
    fun printEmpty(): Unit {
        _writer.important("no results/data")
        _writer.line()
    }


    /**
     * prints summary of the result.
     *
     * @param result
     */
    fun printSummary(result: Result<Any>): Unit {

        // Stats.
        _writer.text("Success : " + result.success)
        _writer.text("Status  : " + result.code)
        _writer.text("Message : " + result.msg)
        _writer.text("Tag     : " + result.tag)
    }


    /**
     * prints an item ( non-recursive )
     *
     * @param obj
     */
    fun printAny(cmd:CliCommand, obj: Any?, outputDir:String): Unit {
        val format = cmd.args.getSysStringOrElse(CliConstants.SysFormat, "props")
        _writer.text("===============================")
        val text = when(format) {
            "csv"   -> serializerCsv.serialize(obj)
            "json"  -> serializerJson.serialize(obj)
            "prop"  -> serializerProp.serialize(obj)
            else    -> serializerProp.serialize(obj)
        }
        _writer.text(text)
        _writer.text("===============================")

        // Writer to log
        val log = cmd.args.getSysStringOrElse(CliConstants.SysLog, "false")
        if(log.trim() == "true") {
            val fileName = Files.fileNameAsAsTimeStamp()
            val filePath = File(outputDir, fileName)
            filePath.writeText(text)
            _writer.text("Wrote content to: ${filePath.absolutePath}")
        }
    }
}
