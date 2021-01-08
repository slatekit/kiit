package test.apis

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Test

class ResponseTests {

    @Test
    fun prettyPrintJson(){
        val text = """{"success": true, "name": "SUCCESS",   "code": 200001, "value" : { }}""".trimIndent()
        val json = JSONParser().parse(text) as JSONObject
        val formatted = json.toJSONString()
        println(formatted)

        val json2 = org.json.JSONObject(text)
        val fmt2 = json2.toString(4)
        println(fmt2)
    }


    fun Any.prettyPrint(): String {

        var indentLevel = 0
        val indentWidth = 4

        fun padding() = "".padStart(indentLevel * indentWidth)

        val toString = toString()

        val stringBuilder = StringBuilder(toString.length)

        var i = 0
        while (i < toString.length) {
            when (val char = toString[i]) {
                '(', '[', '{' -> {
                    indentLevel++
                    stringBuilder.appendln(char).append(padding())
                }
                ')', ']', '}' -> {
                    indentLevel--
                    stringBuilder.appendln().append(padding()).append(char)
                }
                ',' -> {
                    stringBuilder.append(char).append(padding())
                    // ignore space after comma as we have added a newline
                    val nextChar = toString.getOrElse(i + 1) { char }
                    if (nextChar == ' ') i++
                }
                else -> {
                    stringBuilder.append(char)
                }
            }
            i++
        }

        return stringBuilder.toString()
    }
}