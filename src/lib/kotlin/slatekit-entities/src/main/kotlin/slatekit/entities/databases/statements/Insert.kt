package slatekit.entities.databases.statements

import slatekit.entities.core.EntityMapper
import slatekit.meta.models.Model

class Insert : Statement {

    override fun sql(item:Any, model: Model, mapper: EntityMapper): String {
        val table = mapper.tableName()
        val result = mapper.mapFields(null, item, model, false)
        val cols = "(" + result.joinToString( ",", transform = {it.first } ) + ") "
        val vals = "VALUES (" + result.joinToString( ",", transform = {it.second } ) +  ")"
        return "insert into $table $cols $vals;"
    }
}