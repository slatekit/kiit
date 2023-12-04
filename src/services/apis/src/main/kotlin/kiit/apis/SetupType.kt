package kiit.apis

sealed class SetupType {
    object Annotated  : SetupType()
    object Config    : SetupType()
}
