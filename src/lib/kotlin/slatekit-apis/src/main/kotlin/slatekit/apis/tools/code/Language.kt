package slatekit.apis.tools.code

sealed class Language(val ext:String) {
    object Kotlin : Language("kt")
    object Java   : Language("java")
    object Swift  : Language("swift")
    object JS     : Language("js")
}
