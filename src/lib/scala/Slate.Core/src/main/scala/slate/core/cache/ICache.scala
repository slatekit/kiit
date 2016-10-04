package slate.core.cache

/**
 * Created by kv on 10/23/2015.
 */
trait ICache {
  def get(key:String) : Any
  def put(key:String, obj:Any, callback: Option[() => AnyRef])
  def contains(key:String): Boolean
  def remove(key:String)
  def clear(key:String)
}
