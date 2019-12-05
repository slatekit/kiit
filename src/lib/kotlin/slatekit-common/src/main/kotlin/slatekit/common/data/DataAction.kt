package slatekit.common.data

sealed class DataAction {
    /* ktlint-disable */
    object Create          : DataAction()
    object Update          : DataAction()
    object Delete          : DataAction()
    object Fetch           : DataAction()
    object Schema          : DataAction()
    /* ktlint-enable */
}
