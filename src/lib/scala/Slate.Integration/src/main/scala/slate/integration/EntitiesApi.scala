/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.integration

import slate.common.Result
import slate.common.databases.DbConString
import slate.core.apis.{Api, ApiAction, ApiBase}
import slate.entities.models.{EntitySetupService, ModelSettings}


@Api(area = "sys", name = "models", desc = "api to access and manage data models",
  roles= "admin", auth = "key-roles", verb = "post", protocol = "cli")
class EntitiesApi(val _settings:ModelSettings) extends ApiBase
{
  @ApiAction(name = "", desc = "installs the model to the database shard", roles= "@parent", verb = "@parent", protocol = "@parent")
  def install(name:String, version:String = "", dbKey:String = "", dbShard:String = "")
    : Result[Boolean] =
  {
    service.install(name, version, dbKey, dbShard)
  }


  @ApiAction(name = "", desc = "installs all the models in the default database", roles= "@parent", verb = "@parent", protocol = "@parent")
  def installAll(): Result[String] =
  {
    service.installAll()
  }


  @ApiAction(name = "", desc = "installs all the models in the default database", roles= "@parent", verb = "@parent", protocol = "@parent")
  def names(): List[String] =
  {
    service.names()
  }


  @ApiAction(name = "", desc = "generates sql install files for the model", roles= "@parent", verb = "@parent", protocol = "@parent")
  def generateSql(name:String, version:String = ""): Result[String] =
  {
    service.generateSql(name, version)
  }


  @ApiAction(name = "", desc = "generates sql install files for all models", roles= "@parent", verb = "@parent", protocol = "@parent")
  def generateSqlAll(): Result[String] =
  {
    service.generateSqlAll()
  }


  @ApiAction(name = "", desc = "gets the default db connection", roles= "@parent", verb = "@parent", protocol = "@parent")
  def connectionDefault(): Result[DbConString] =
  {
    service.connection("", "")
  }


  @ApiAction(name = "", desc = "get the database connection for the db shard", roles= "@parent", verb = "@parent", protocol = "@parent")
  def connection(dbKey:String = "", dbShard:String = ""): Result[DbConString] =
  {
    service.connection(dbKey, dbShard)
  }


  private def service: EntitySetupService =
  {
    new EntitySetupService(context.ent, _settings, context.dirs)
  }
}
