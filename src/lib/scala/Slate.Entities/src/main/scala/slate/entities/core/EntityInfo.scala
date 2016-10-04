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

package slate.entities.core

import slate.common.Strings

import scala.reflect.runtime.universe.Type


/**
  *
  * @param entityType            : the type of the entity
  * @param entityServiceType     : the type of the service    ( EntityService[T] or derivative )
  * @param entityRepoType        : the type of the repository ( EntityRepository[T] or derivative )
  * @param entityMapperType      : the type of the mapper     ( EntityMapper[T] or derivative )
  * @param entityServiceInstance : an instance of the service ( singleton usage )
  * @param entityRepoInstance    : an instance of the repo    ( singleton usage )
  * @param entityMapperInstance  : an instance of the mapper  ( singleton usage )
  * @param isSqlRepo             : whether or not the repo is sql based or memory based
  * @param dbType                : the database provider type
  * @param dbKey                 : a key identifying the database connection
  *                               ( see DbLookup / Example_Database.scala )
  * @param dbShard               : a key identifying the database shard
  *                               ( see DbLookup / Example_Database.scala )
  */
case class EntityInfo(
                       entityType            : Option[Type]   = None,
                       entityServiceType     : Option[Type]   = None,
                       entityRepoType        : Option[Type]   = None,
                       entityMapperType      : Option[Type]   = None,
                       entityServiceInstance : Option[AnyRef] = None,
                       entityRepoInstance    : Option[AnyRef] = None,
                       entityMapperInstance  : Option[AnyRef] = None,
                       isSqlRepo             : Boolean = true,
                       dbType                : String = "",
                       dbKey                 : String = "",
                       dbShard               : String = ""
                     )
{

  def toStringDetail():String =
  {
    val text = "entity type  : " + getTypeName(entityType)                     + Strings.newline() +
               "svc     type : " + getTypeName(entityServiceType)              + Strings.newline() +
               "svc     inst : " + getTypeNameFromInst(entityServiceInstance)  + Strings.newline() +
               "repo    type : " + getTypeName(entityRepoType)                 + Strings.newline() +
               "repo    inst : " + getTypeNameFromInst(entityRepoInstance)     + Strings.newline() +
               "mapper  type : " + getTypeName(entityMapperType)               + Strings.newline() +
               "mapper  inst : " + getTypeNameFromInst(entityMapperInstance)   + Strings.newline() +
               "is sql repo  : " + isSqlRepo                                   + Strings.newline() +
               "db type      : " + dbType                                      + Strings.newline() +
               "db key       : " + dbKey                                       + Strings.newline() +
               "db shard     : " + dbShard                                     + Strings.newline()
    text
  }


  private def getTypeName(tpe:Option[Type]) : String =
  {
    if(tpe.nonEmpty && tpe.get != null)
      return tpe.get.typeSymbol.fullName

    ""
  }


  private def getTypeNameFromInst(tpe:Option[AnyRef]) : String =
  {
    if(tpe.nonEmpty && tpe.get != null)
      return tpe.get.getClass.getName
    ""
  }
}
