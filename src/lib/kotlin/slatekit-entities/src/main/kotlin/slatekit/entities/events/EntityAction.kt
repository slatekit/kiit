package slatekit.entities.events

sealed class EntityAction {
    object Create : EntityAction()
    object Fetch  : EntityAction()
    object Update : EntityAction()
    object Save   : EntityAction()
    object Delete : EntityAction()
}
