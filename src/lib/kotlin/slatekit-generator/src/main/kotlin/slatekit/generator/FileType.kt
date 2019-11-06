package slatekit.generator

sealed class FileType {
    object Build  : FileType()
    object Doc    : FileType()
    object Conf   : FileType()
    object Code   : FileType()

    companion object {
        fun parse(name:String): FileType {
            return when(name) {
                "build" -> Build
                "doc"   -> Doc
                "conf"  -> Conf
                "code"  -> Code
                else    -> throw Exception("Generator: Unexpected file type: $name")
            }
        }
    }
}