package slatekit.entities.core



sealed class EntityAction {
    object EntityCreate : EntityAction()
    object EntityFetch  : EntityAction()
    object EntityUpdate : EntityAction()
    object EntitySave   : EntityAction()
    object EntityDelete : EntityAction()
}