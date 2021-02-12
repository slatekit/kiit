package slatekit.db

/**
 * Most sql statements ( by the data mapper ) are single table statements
 */
data class DbSettings(val autoCommit:Boolean = true)
