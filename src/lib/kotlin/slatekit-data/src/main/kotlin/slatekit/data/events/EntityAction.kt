package slatekit.data.events

sealed class EntityAction {
    object Create : EntityAction()
    object Update : EntityAction()
    object Delete : EntityAction()
}
