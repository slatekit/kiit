package slatekit.entities.databases.statements

import slatekit.entities.core.Entity
import slatekit.entities.core.EntityMapper
import slatekit.meta.models.Model


class Delete : Statement {

    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        // TODO: Have to support entity with other name for id
        val id = (item as Entity).identity()
        val tableName = mapper.buildName(model.table)
        return "delete from $tableName where id = $id"
    }
}