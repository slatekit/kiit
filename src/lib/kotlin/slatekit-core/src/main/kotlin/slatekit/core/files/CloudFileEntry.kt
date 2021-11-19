package slatekit.core.files

/**
 * @param name : "file1.txt"
 * @param path : "folder1/subfolder1"
 * @param data : data of the file
 * @param text : Text presentation of data
 * @param atts : Custom user metadata
 */
data class CloudFileEntry(override val name:String,
                      override val path: String?,
                      override val data: ByteArray,
                      override val text: String? = null,
                      override val atts: Map<String, String>? = null) : CloudFile {

    override val textOrEmpty: String = text ?: ""
}