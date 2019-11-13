package slatekit.common.requests

import slatekit.common.types.Doc
import java.io.InputStream

/**
 * Support class for the abstract request
 */
interface RequestSupport {
    fun raw(): Any?
    fun getDoc(name: String): Doc?
    fun getFile(name: String, callback: (InputStream) -> Doc): Doc?
    fun getFileStream(name: String, callback: (InputStream) -> Unit)
}
