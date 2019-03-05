package slatekit.core.cloud

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

object CloudUtils {

     fun loadFromFile(filePath: String): String {
        return File(filePath).readText()
    }

     fun toInputStream(content: String): InputStream {
        return ByteArrayInputStream(content.toByteArray())
    }

     fun toString(input: InputStream): String {
        return input.bufferedReader().use { it.readText() }
    }
}