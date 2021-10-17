package slatekit.requests

import slatekit.common.types.Doc
import java.io.InputStream

/**
 * Support class for the abstract request
 */
interface RequestSupport {
    fun raw(): Any?
    fun getDoc(name: String): Doc?
    fun getDoc(name: String, callback: (InputStream) -> Doc): Doc?
    fun getFileStream(name: String): InputStream?
}
