package slatekit.entities

import slatekit.data.core.Id
import slatekit.entities.core.EntityUtils

class EntityId<TId, T>(val idName:String = "id", val converter:(String) -> TId) : Id<TId, T> where TId: Comparable<TId>, T: Entity<TId> {
    override fun name(): String {
        return idName
    }

    override fun isPersisted(model: T): Boolean {
        return model.isPersisted()
    }

    override fun isPersisted(id: TId): Boolean {
        return EntityUtils.isCreated(id)
    }

    override fun identity(model: T): TId {
        return model.identity()
    }

    override fun convertToId(id: String): TId {
        return converter(id)
    }
}


class EntityIntId<T>(val idName:String = "id") : Id<Int, T> where T: Entity<Int> {
    override fun name(): String {
        return idName
    }

    override fun isPersisted(model: T): Boolean {
        return model.isPersisted()
    }

    override fun isPersisted(id: Int): Boolean {
        return id > 0
    }

    override fun identity(model: T): Int {
        return model.identity()
    }

    override fun convertToId(id: String): Int {
        return id.toInt()
    }
}


class EntityLongId<T>(val idName:String = "id") : Id<Long, T> where T: Entity<Long> {
    override fun name(): String {
        return idName
    }

    override fun isPersisted(model: T): Boolean {
        return model.isPersisted()
    }

    override fun isPersisted(id: Long): Boolean {
        return id > 0L
    }

    override fun identity(model: T): Long {
        return model.identity()
    }

    override fun convertToId(id: String): Long {
        return id.toLong()
    }
}
