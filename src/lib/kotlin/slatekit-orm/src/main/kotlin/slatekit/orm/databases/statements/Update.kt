package slatekit.orm.databases.statements

import slatekit.orm.core.Entity
import slatekit.orm.core.EntityMapper
import slatekit.orm.core.EntityWithId
import slatekit.meta.Reflector
import slatekit.meta.models.Model

class Update : Statement {

    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        // TODO: Have to support entity with other name for id
        val id = Reflector.getFieldValue(item, EntityWithId<*>::id.name)
        val table = mapper.tableName()
        val result = mapper.mapFields(null, item, model, true)
        val updates = " " + result.joinToString( ",", transform = {it.second } )
        return "update $table set $updates where id = $id;"
    }
}