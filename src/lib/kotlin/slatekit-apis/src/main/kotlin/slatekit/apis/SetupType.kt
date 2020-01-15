package slatekit.apis

sealed class SetupType {
    object Annotated : SetupType()
    object Methods : SetupType()
}
