package slatekit.apis.setup


sealed class Setup {
    object Annotated : Setup()
    object Methods : Setup()
}
