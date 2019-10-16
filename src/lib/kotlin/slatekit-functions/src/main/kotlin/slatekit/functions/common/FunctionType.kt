package slatekit.functions.common

import slatekit.common.EnumLike


/**
 * Represents a logical category of function types
 */
sealed class FunctionType(override val value:Int, override val name:String) : EnumLike {
    object Cmd      : FunctionType(0, "Cmd"     ) // Command for CLI
    object Event    : FunctionType(1, "Event"   ) // Event handler
    object Misc     : FunctionType(2, "Misc"    ) // Generic / Misc
    object Sync     : FunctionType(3, "Sync"    ) // Synchronization
    object Task     : FunctionType(4, "Task"    ) // Scheduled task
    object Job      : FunctionType(5, "Job"     ) // Background job
    class  Other(name:String): FunctionType(6, name)
}
