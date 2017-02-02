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


  private def getTypeName(tpe:Option[Type]) : String = {
    tpe.fold("")( t => t.typeSymbol.fullName)
  }


  private def getTypeNameFromInst(tpe:Option[AnyRef]) : String = {
    tpe.fold("")( t => t.getClass.getName)
  }
}
