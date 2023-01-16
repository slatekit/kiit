package kiit.core.files

/**
 * @param name : "file1.txt"
 * @param path : "folder1/subfolder1"
 * @param data : data of the file
 * @param text : Text presentation of data
 * @param atts : Custom user metadata
 */
data class CloudFileEntry (
                      override val path: String?,
                      override val name:String,
                      override val data: ByteArray,
                      override val atts: Map<String, String>? = null) : CloudFile {


    override val text: String? by lazy { String(data) }
    override val textOrEmpty: String = text ?: ""
}