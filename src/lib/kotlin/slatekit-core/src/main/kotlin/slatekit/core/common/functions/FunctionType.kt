package slatekit.core.common.functions


/**
 * Represents a logical category of function types
 */
sealed class FunctionType{
    object CLI     : FunctionType()
    object Batch   : FunctionType()
    object Sync    : FunctionType()
    object Event   : FunctionType()
    object Generic : FunctionType()
    class  Other(val name:String): FunctionType()
}
