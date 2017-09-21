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


import slatekit.common.Result
import slatekit.common.console.ConsoleWriter
import slatekit.common.serialization.SerializerProps
import slatekit.meta.Serialization


class CliPrinter(val _writer: ConsoleWriter) {

    val serializerProp = Serialization.props(true)
    val serializerJson = Serialization.json()
    val serializerCsv  = Serialization.csv()


    fun printResult(cmd:CliCommand, result: Result<Any>): Unit {
        result.value?.let { value ->
            printAny(cmd, value)
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
    fun printAny(cmd:CliCommand, obj: Any?): Unit {
        val format = cmd.args.getMetaStringOrElse("format", "props")
        _writer.text("===============================")
        val text = when(format) {
            "csv"   -> serializerCsv.serialize(obj)
            "json"  -> serializerJson.serialize(obj)
            "prop"  -> serializerProp.serialize(obj)
            else    -> serializerProp.serialize(obj)
        }
        _writer.text(text)
        _writer.text("===============================")
    }
}
