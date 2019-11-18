package slatekit.core.cache


/**
 * Represents the actions that can be performed on the Cache and/or specific Cache Entry
 * This action is sent via the channel
 */
sealed class CacheAction(val name: String) {
    /* ktlint-disable */
    object Create   : CacheAction( "Create" )
    object Fetch    : CacheAction( "Fetch"  )
    object Update   : CacheAction( "Update" )
    object Delete   : CacheAction( "Delete" )
    object Clear    : CacheAction( "Clear"  )
    object Check    : CacheAction( "Check"  )
    object Refresh  : CacheAction( "Refresh")
    /* ktlint-enable */
}
