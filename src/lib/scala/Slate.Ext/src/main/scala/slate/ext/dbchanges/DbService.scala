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

package slate.ext.dbchanges

import slate.common.databases.Db
import slate.common.{Ioc, Ensure, Models, Model}


class DbService(val models:Models) {

  def installModel(name:String):Unit =
  {
    Ensure.isTrue(models.contains(name), "invalid name supplied to create table")
    createTable(name)
  }


  def uninstallModel(name:String):Unit =
  {
    Ensure.isTrue(models.contains(name), "invalid name supplied to create table")
    val model = models.get(name)
    dropTable(model.name)
  }


  def createTable(name:String): Unit =
  {
    Ensure.isTrue(models.contains(name), "invalid name supplied to create table")
    val model = models.get(name)
    database.createTable(model)
  }


  def dropTable(name:String): Unit =
  {
    database.dropTable(name)
  }


  private def database:Db =
  {
    Ioc.get("db").asInstanceOf[Db]
  }
}
