package slatekit.core.cmds

/**
 * Enforce a logical category of the command for consistency
 */
sealed class CommandCategory{
    object CLI     : CommandCategory()
    object Batch   : CommandCategory()
    object Sync    : CommandCategory()
    object Generic : CommandCategory()
    class  Other(val name:String): CommandCategory()
}
