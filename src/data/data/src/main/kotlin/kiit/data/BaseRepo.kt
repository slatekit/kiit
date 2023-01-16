package kiit.data

import slatekit.common.DateTimes
import slatekit.common.data.DataAction
import slatekit.common.data.DataEvent
import slatekit.common.data.DataHooks
import kiit.data.core.Meta

abstract class BaseRepo<TId, T>(override val meta: Meta<TId, T>,
                                protected val hooks: DataHooks<TId, T>?) : Repo<TId, T> where TId : Comparable<TId>, T:Any {

    protected val tableName: String get() { return meta.name }

    protected fun notify(action: DataAction, id:TId, entity:T?, success:Boolean) {
        val ts = DateTimes.now()
        if(success) {
            when (action) {
                DataAction.Create -> hooks?.onDataEvent(DataEvent.DataCreated<TId, T>(meta.name, id, entity, ts))
                DataAction.Update -> hooks?.onDataEvent(DataEvent.DataUpdated<TId, T>(meta.name, id, entity, ts))
                DataAction.Delete -> hooks?.onDataEvent(DataEvent.DataDeleted<TId, T>(meta.name, id, entity, ts))
                else -> {
                }
            }
        }
        else {
            hooks?.onDataEvent(DataEvent.DataErrored<TId, T>(meta.name, id, entity, null, ts))
        }
    }
}
