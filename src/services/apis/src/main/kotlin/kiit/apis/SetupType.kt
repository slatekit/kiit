package kiit.apis

sealed class SetupType {
    object Annotated  : SetupType()
    object Methods    : SetupType()
    object Configured : SetupType()
}
