package slatekit.entities.services

import slatekit.common.query.IQuery
import slatekit.common.query.Query
import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntityCounts<T> : ServiceSupport<T> where T : Entity {


}