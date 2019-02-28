package slatekit.orm.databases.statements

import slatekit.orm.core.Entity
import slatekit.orm.core.EntityMapper
import slatekit.meta.models.Model


class Delete : Statement {

    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        // TODO: Have to support entity with other name for id
        val id = (item as Entity<*>).identity()
        val table = mapper.tableName()
        return "delete from $table where id = $id"
    }
}