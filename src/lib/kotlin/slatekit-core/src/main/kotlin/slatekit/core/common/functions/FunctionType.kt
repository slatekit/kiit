package slatekit.core.common.functions


/**
 * Represents a logical category of function types
 */
sealed class FunctionType {
    abstract val name:String

    object CLI     : FunctionType() { override val name = "cli" }
    object Batch   : FunctionType() { override val name = "batch" }
    object Sync    : FunctionType() { override val name = "sync" }
    object Event   : FunctionType() { override val name = "event" }
    object Generic : FunctionType() { override val name = "generic" }
    class  Other(override val name:String): FunctionType()
}
