package slatekit.orm.databases.statements

import slatekit.orm.core.EntityMapper
import slatekit.meta.models.Model

interface Statement {
    fun sql(item:Any, model: Model, mapper: EntityMapper):String
}