package slatekit.functions.common

interface Function {
    val info: FunctionInfo
    val name: String get() { return info.name }
}
