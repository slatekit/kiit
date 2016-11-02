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

import slate.common.info.Host
import slate.common.utils.Sample
import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique, IEntity}
import slate.core.common.tenants.ITenant

/**
  * Created by kreddy on 2/24/2016.
  */
class Resource extends IEntity with IEntityUnique with Sample  with ITenant
{
  var id = 0L


  @Field("",true, 50)
  var uniqueId: String = ""


  @Field("", true, -1)
  var tenantId = 0


  /** uniquely identifies a resource, this is in the format
    * {country}-{region}-{category}-{name}
    *
    * @example : usa-001-ec2-users | usa-001-ec2-share | usa-001-s3-files
   */
  @Field("", true, 20)
  var key = ""


  /** a name for a specific resource
    *
    * @example : users | sharing | reg |
    */
  @Field("", true, 20)
  var name = ""


  /** uniquely identifies a resource category
    *
    * @example : ec2 | s3 | sqs | rds
    */
  @Field("", true, 10)
  var category = ""


  /** uniquely identifies a country
    *
    * @example : usa | ind | eng
    */
  @Field("", true, 20)
  var country = ""


  /** uniquely identifies a region in a country, this allows having
    * multiple resources based on the country
    *
    * @example : 001 | 002 | 300 | 400
    */
  @Field("", true, 20)
  var region = ""


  /** uniquely identifies a specific instance, this allows having
    * multiple instance based on the region
    *
    * @example : 001 | 002 | 300 | 400
    */
  @Field("", true, 20)
  var instance = ""


  /** calculated field that allows for aggregating up resources based on country and region
    * {country}-{region}
    *
    * @example : usa-001
    */
  @Field("", true, 20)
  var aggRegion = ""


  /** calculated field that allows for aggregating up resources based on country, region, instance
    * {country}-{region}-{instance}
    *
    * @example : usa-001-ec2
    */
  @Field("", true, 20)
  var aggInstance= ""


  /** calculated field that allows for aggregating up resources based on country, region, category
    * {country}-{region}-{instance}-{category}
    *
    * @example : usa-001-ec2
    */
  @Field("", true, 20)
  var aggCategory = ""


  @Field("", true, 50)
  var links = ""


  @Field("", true, 20)
  var owner = ""


  @Field("", true, 0)
  var availability = 0


  @Field("", true, 30)
  var status = ""


  @Field("", true, 500)
  var hostInfo = ""


  @Field("", true, 0)
  var recordState = 0


  /** tag to refer to an external id
    * {country}-{region}-{category}-{name}
    *
    * @example : usa-001-ec2-users | usa-001-ec2-share | usa-001-s3-files
    */
  @Field("", true, 20)
  var refTag = ""


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0


  def isOwned(): Boolean =
  {
    status == ResourceConstants.STATUS_OWNED;
  }


  def isFree(): Boolean =
  {
    status == ResourceConstants.STATUS_FREE;
  }


  def calculateKeys(): Unit =
  {
    this.aggRegion   = this.country + "_" + this.region
    this.aggInstance = this.country + "_" + this.region + "_" + this.instance
    this.aggCategory = this.country + "_" + this.region + "_" + this.category
    this.key = this.country + "_" + this.region + "_" + this.instance + "_" + this.category
  }


  def asSample(): Unit =
  {
    this.name = "share"
    this.category = "que"
    this.country = "usa"
    this.region = "001"
    this.instance = "001"
    this.links = "usa-001-001-que"
    this.owner = "user01"
    this.availability = ResourceConstants.AVAIILABLE
    this.status = "active"
    this.hostInfo = Host.local().toString
    this.refTag = ""
    this.createdAt = DateTime.now()
    this.updatedAt = DateTime.now()
    this.calculateKeys()
  }
}
