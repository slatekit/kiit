package slatekit.orm.databases.statements

import slatekit.entities.Entity
import slatekit.entities.EntityWithId
import slatekit.orm.OrmMapper
import slatekit.meta.Reflector
import slatekit.meta.models.Model

class Update<TId, T> : Statement<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    override fun sql(item: Entity<TId>, model: Model, mapper: OrmMapper<TId, T>): String {
        slatekit.common.NOTE.IMPLEMENT("orm", "Have to support entity with other name for id")
        val id = Reflector.getFieldValue(item, EntityWithId<*>::id.name)
        val table = mapper.tableName()
        val result = mapper.mapFields(null, item, model, true)
        val updates = " " + result.joinToString( ",", transform = {it.second } )
        return "update $table set $updates where id = $id;"
    }
}