/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.ext.resources

import slate.common.Strings
import slate.common.DateTime
import slate.entities.core.{EntityService, EntityRepo}
import slate.common.query.Query
import slate.core.common.svcs.EntityServiceWithSupport

import scala.collection.mutable.ListBuffer


class ResourceService extends EntityServiceWithSupport[Resource]() {

  def applyDefaults()
  {
    if (!any())
    {
      addRegion(ResourceConstants.COUNTRY_USA, "001")
      addRegion(ResourceConstants.COUNTRY_IND, "001")
    }
  }


  def addRegion(country:String, region:String)
  {
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_DB   , country, region, "001", "")
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_QUEUE, country, region, "001", "")
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_QUEUE, country, region, "002", "")
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_QUEUE, country, region, "003", "")
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_WEB  , country, region, "001", "")
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_WEB  , country, region, "002", "")
    create(ResourceConstants.HOST_AWS, ResourceConstants.TYPE_WEB  , country, region, "003", "")
  }


  def queues(country:String):List[Resource] =
  {
    findByCategory(ResourceConstants.TYPE_QUEUE, country)
  }


  def servers(country:String):List[Resource] =
  {
    findByCategory(ResourceConstants.TYPE_WEB, country)
  }


  def countries():List[String] =
  {
    distinct[String]("Country")
  }


  def categories():List[String] =
  {
    distinct[String]("Type")
  }


  def aggGroups():List[String] =
  {
    distinct[String]("Group")
  }


  def keys():List[String] =
  {
    distinct[String]("Key")
  }


  def owners():List[String] =
  {
    distinct[String]("Owner")
  }


  def create(host:String, category:String, country:String, region:String,
             instance:String, linked:String): String =
  {
    val res = new Resource()
    res.name        = category
    res.category    = Strings.valueOrDefault(category   , "" )
    res.country     = Strings.valueOrDefault(country    , "" )
    res.region      = Strings.valueOrDefault(region     , "" )
    res.instance    = Strings.valueOrDefault(instance   , "" )
    res.owner       = Strings.valueOrDefault(host       , "" )
    res.refTag      = Strings.valueOrDefault(""         , "" )
    res.status      = ResourceConstants.STATUS_FREE
    res.recordState = 0
    res.createdAt     = DateTime.now()
    res.updatedAt     = DateTime.now()
    res.calculateKeys()
    if (!Strings.isNullOrEmpty(linked))
    {
      res.links = res.aggRegion + "_" + linked
    }

    res.id = create(res)
    res.id + ": " + res.key
  }


  def isValidCountry(country:String): Boolean =
  {
    if (Strings.isNullOrEmpty(country) || Strings.compare(country, "n/a") == 0)
      return false
    true
  }


  def findByCategory(category:String, country:String): List[Resource] =
  {
    if(!isValidCountry(country))
      find(new Query().where("category", "=", category))
    else
      find(new Query().where("category", "=", category).and("country", "=", country))
  }
}
