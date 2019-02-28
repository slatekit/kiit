package slatekit.orm.databases.statements

import slatekit.orm.core.Entity
import slatekit.orm.core.EntityMapper
import slatekit.meta.models.Model


class Select : Statement {
    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        val id = (item as Entity<*>).identity()
        val table = mapper.tableName()
        return "select * from $table where id = $id"
    }
}