package slatekit.core.common


/**
 * Represents a logical category of function types
 */
sealed class FunctionType{
    object CLI     : FunctionType()
    object Batch   : FunctionType()
    object Sync    : FunctionType()
    object Generic : FunctionType()
    class  Other(val name:String): FunctionType()
}
