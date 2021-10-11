package slatekit.common.args

interface ArgsWriter {
    fun write(arg: Arg, prefix: String? = "-", separator: String? = "=", maxLength:Int )
}