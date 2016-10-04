package slate.core.datasource

/**
 * Created by kv on 10/19/2015.
 */
trait DataSourceCallbacks {
  def onDataSourceAvailable(sender: AnyRef, args: DataSourceEventArgs)
  def onDataSourceError(sender: AnyRef, args: DataSourceEventArgs)
}
