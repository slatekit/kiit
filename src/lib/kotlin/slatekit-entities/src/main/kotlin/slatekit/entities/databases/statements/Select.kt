package slatekit.entities.databases.statements

import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import slatekit.meta.models.Model


class Select : Statement {
    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        val id = (item as Entity).identity()
        val table = mapper.tableName()
        return "select * from $table where id = $id"
    }
}