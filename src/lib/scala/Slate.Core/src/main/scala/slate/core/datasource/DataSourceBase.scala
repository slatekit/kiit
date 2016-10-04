package slate.core.datasource

/**
 * Created by kv on 10/19/2015.
 */
abstract class DataSourceBase(val sourceName: String ) {

  protected var _dataSource: Any = Nil
  protected var _errorLoading = false
  protected var _callback:DataSourceCallbacks = null


  /**
   * clears the existing data fetched from source
   */
  def clear() =
  {
    _dataSource = null
  }


  /**
   * data is available
   */
  def isAvailable() =
  {
    _dataSource != null && !_errorLoading
  }


  /**
   * error loading data, data not available
   */
  def isUnAvailable() =
  {
    _dataSource == null && _errorLoading;
  }


  /**
   * loaded data from source, but no data
   */
  def isNothing() =
  {
    _dataSource == null && !_errorLoading;
  }


  /**
   * Gets the data fetched from source
   * @return
   */
  def get():Any =
  {
    _dataSource
  }


  def getOrReload() : Any =
  {
    if (isAvailable())
      return get()

    reload()
  }


  def reload(): Any =
  {
    _dataSource = reloadInternal();
    _dataSource
  }


  protected def reloadInternal():Any


  def subscribe(callback: DataSourceCallbacks)
  {
    _callback = callback;
  }


  def notifySubscribers()
  {
    if(_callback != null)
    {
      if(isAvailable())
      {
        _callback.onDataSourceAvailable(this, new DataSourceEventArgs(sourceName, "", _dataSource));
      }
      else
      {
        _callback.onDataSourceError(this, new DataSourceEventArgs(sourceName, "", _dataSource));
      }
    }
  }

}
