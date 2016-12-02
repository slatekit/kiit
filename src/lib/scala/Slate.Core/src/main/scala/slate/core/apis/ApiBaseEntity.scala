/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis


import slate.common.query.Query
import slate.entities.core.{EntityService, IEntity}

/**
  * Base class for an Api that is used to access/manage database models / entities using the
  * Slate Kit Orm ( Entities ).
  * @tparam T
  */
class ApiBaseEntity[T >: Null <: IEntity] extends ApiBase {

  protected var _service:EntityService[T] = null


  @ApiAction(name = "", desc= "gets the total number of users", roles= "@parent", verb = "get", protocol = "@parent")
  def total():Long =
  {
    _service.count()
  }


  @ApiAction(name = "", desc= "gets all items", roles= "@parent", verb = "get", protocol = "@parent")
  def getAll():List[T] =
  {
    _service.getAll()
  }


  @ApiAction(name = "", desc= "gets the first item", roles= "@parent", verb = "get", protocol = "@parent")
  def first(): Option[T] =
  {
    _service.first()
  }


  @ApiAction(name = "", desc= "gets the first item", roles= "@parent", verb = "get", protocol = "@parent")
  def getById(id:Long): Option[T] =
  {
    _service.get(id)
  }


  @ApiAction(name = "", desc= "gets the last item", roles= "@parent", verb = "get", protocol = "@parent")
  def last(): Option[T] =
  {
    _service.last()
  }


  @ApiAction(name = "", desc= "gets recent items in the system", roles= "@parent", verb = "get", protocol = "@parent")
  def recent(count:Int = 5): List[T] =
  {
    _service.recent(count)
  }


  @ApiAction(name = "", desc= "gets oldest items in the system", roles= "@parent", verb = "get", protocol = "@parent")
  def oldest(count:Int = 5): List[T] =
  {
    _service.oldest(count)
  }


  @ApiAction(name = "", desc= "gets distinct items based on the field", roles= "@parent", verb = "get", protocol = "@parent")
  def distinct(field:String): List[Any] =
  {
    List[Any]()
  }


  @ApiAction(name = "", desc= "finds items by field name and value", roles= "@parent", verb = "get", protocol = "@parent")
  def findBy(field:String, value:String): List[T] =
  {
     _service.find(new Query().where(field, "=", value))
  }


  @ApiAction(name = "", desc= "finds items by field name and value", roles= "@parent", verb = "post", protocol = "@parent")
  def updateField(id:Long, field:String, value:String): Unit =
  {
    _service.update(id, field, value)
  }


  @ApiAction(name = "", desc= "deletes an item by its id", roles= "@parent", verb = "delete", protocol = "@parent")
  def delete(id:Long):Boolean =
  {
    _service.delete(id)
  }
}
