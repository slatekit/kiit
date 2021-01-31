package slatekit.common.data

/**
 * Represents CRUD like actions being done on any data
 * Used mostly for the slatekit.entities
 */
sealed class DataAction {
    /* ktlint-disable */
    object Create          : DataAction()
    object Update          : DataAction()
    object Delete          : DataAction()
    object Select          : DataAction()
    object Schema          : DataAction()
    /* ktlint-enable */
}
