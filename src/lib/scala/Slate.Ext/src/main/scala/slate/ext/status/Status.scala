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

package slate.ext.status

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique,  IEntity}
import slate.core.common.tenants.ITenant

class Status extends IEntity with IEntityUnique with ITenant
{
  var id = 0L

  @Field("",true, 50)
  var uniqueId: String = ""


  @Field("", true, -1)
  var tenantId = 0


  /** uniquely identifies a status item
    *
    * @example : usa-001-ec2-web
    */
  @Field("", true, 20)
  var key = ""


  /** name of the application or service
    *
    * @example : users | sharing | reg |
    */
  @Field("", true, 20)
  var name = ""


  /** description of the application or service
    *
    * @example : users | sharing | reg |
    */
  @Field("", true, 20)
  var desc = ""


  /** uniquely identifies a group
    *
    * @example : "web" | "task" | "job"
    */
  @Field("", true, 10)
  var group = ""


  /** the category of the status
    *
    * @example : "web" | "task" | "job"
    */
  @Field("", true, 20)
  var category = ""


  /** instance of the app/service if multiple instances are running.
    *
    * @example : 001 | 002
    */
  @Field("", true, 20)
  var instance = ""


  /** the url of the app
    *
    * @example : usa | ind | eng
    */
  @Field("", true, 20)
  var url = ""


  /** version of the app/service
    *
    * @example : usa | ind | eng
    */
  @Field("", true, 20)
  var version = ""


  /** port the app/service is running on
    *
    * @example : usa | ind | eng
    */
  @Field("", true, -1)
  var port = 0


  /** tags for labeling purposes
    *
    * @example : web | email
    */
  @Field("", true, 20)
  var tags = ""


  /** environment the app/service is associated with
    *
    * @example : dv1 | qa1 | pr1
    */
  @Field("", true, 20)
  var env = ""


  @Field("", true, 30)
  var status = ""


  @Field("", true, -1)
  var errorCount = 0


  @Field("", true, 300)
  var errorLast = ""


  @Field("", true, 0)
  var timeStarted = DateTime.now()


  @Field("", true, 0)
  var timePinged = DateTime.now()


  @Field("", true, -1)
  var createdAt  = DateTime.now()


  @Field("", true, -1)
  var createdBy  = 0


  @Field("", true, -1)
  var updatedAt  =  DateTime.now()


  @Field("", true, -1)
  var updatedBy  = 0
}
