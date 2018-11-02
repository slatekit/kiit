package slatekit.entities.databases.statements

import slatekit.entities.core.EntityMapper
import slatekit.meta.models.Model

interface Statement {
    fun sql(item:Any, model: Model, mapper: EntityMapper):String
}