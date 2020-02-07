package slatekit.cache


/**
 * Represents the actions that can be performed on the Cache and/or specific Cache Entry
 * This action is sent via the channel
 */
sealed class CacheAction(val name: String) {
    /* ktlint-disable */
    object Create    : CacheAction( "Create"    )
    object Fetch     : CacheAction( "Fetch"     )
    object Update    : CacheAction( "Update"    )
    object Delete    : CacheAction( "Delete"    )
    object DeleteAll : CacheAction( "DeleteAll" )
    object Expire    : CacheAction( "Expire"    )
    object ExpireAll : CacheAction( "ExpireAll" )
    object Refresh   : CacheAction( "Refresh"   )
    /* ktlint-enable */
}

