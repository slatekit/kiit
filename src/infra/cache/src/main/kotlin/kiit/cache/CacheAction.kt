package kiit.cache


/**
 * Represents the actions that can be performed on the Cache and/or specific Cache Entry
 * This action is sent via the channel
 */
sealed class CacheAction(val name: String) {
    /* ktlint-disable */
    object Exists    : CacheAction( "Exists"    )
    object Create    : CacheAction( "Create"    )
    object Fetch     : CacheAction( "Fetch"     )
    object Update    : CacheAction( "Update"    )
    object Stats     : CacheAction( "Stats"     )
    object Refresh   : CacheAction( "Refresh"   )
    object Expire    : CacheAction( "Expire"    )
    object ExpireAll : CacheAction( "ExpireAll" )
    object Delete    : CacheAction( "Delete"    )
    object DeleteAll : CacheAction( "DeleteAll" )
    /* ktlint-enable */
}

