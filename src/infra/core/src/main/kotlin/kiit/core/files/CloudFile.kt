package slatekit.core.files

interface CloudFile {
    val path:String?
    val name:String
    val data:ByteArray
    val text:String?
    val textOrEmpty:String
    val atts:Map<String,String>?
}


