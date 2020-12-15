package slatekit.apis.core

sealed class HelpType {
    object All    : HelpType()
    object Area   : HelpType()
    object Api    : HelpType()
    object Action : HelpType()
}
