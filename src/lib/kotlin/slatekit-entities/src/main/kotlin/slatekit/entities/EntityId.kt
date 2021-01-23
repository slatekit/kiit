package slatekit.entities

import slatekit.data.core.Id

class EntityId<T>(val idName:String = "id") : Id<Long, T> where T: Entity<Long> {
    override fun id(): String {
        return idName
    }

    override fun isPersisted(model: T): Boolean {
        return model.isPersisted()
    }

    override fun identity(model: T): Long {
        return model.identity()
    }

    override fun convertToId(id: String): Long {
        return id.toLong()
    }
}
