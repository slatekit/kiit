package slatekit.entities.services

import slatekit.entities.core.Entity
import slatekit.entities.core.ServiceSupport

interface EntityFeatureAll<T> : ServiceSupport<T>,
        EntityCreates<T>,
        EntityDeletes<T>,
        EntityFinds<T>,
        EntityReads<T>,
        EntityRelations<T>,
        EntitySaves<T>,
        EntityUpdates<T>

        where T : Entity