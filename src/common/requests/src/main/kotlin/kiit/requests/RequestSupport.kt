package kiit.requests

import kiit.common.types.ContentFile
import java.io.InputStream

/**
 * Support class for the abstract request
 */
interface RequestSupport {
    fun raw(): Any?
    fun getDoc(name: String?): ContentFile?
    fun getDoc(name: String?, callback: (InputStream) -> ContentFile): ContentFile?
    fun getFileStream(name: String?): InputStream?
}
