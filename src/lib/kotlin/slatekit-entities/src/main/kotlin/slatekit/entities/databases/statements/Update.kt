package slatekit.entities.databases.statements

import slatekit.entities.core.EntityMapper
import slatekit.meta.models.Model

class Update : Statement {

    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        val tableName = mapper.buildName(model.table)
        val result = mapper.mapFields(null, item, model, true)
        val updates = " " + result.joinToString( ",", transform = {it.second } ) + ") "
        return "update $tableName set $updates;"
    }
}