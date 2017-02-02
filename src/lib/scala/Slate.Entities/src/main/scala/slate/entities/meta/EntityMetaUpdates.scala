/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.entities.meta

import slate.common.DateTime

/**
  * Represents a metadata update for setting the created by, created at timestamp
  * on an entity that supports metadata
  * @param by: User id performing the create operation
  * @param at: Timestamp of the creation
  */
case class EntityCreateInfo(by:Long, at:DateTime) extends EntityMetaInfo {}


/**
  * Represents a metadata update for setting the updated by, updated at timestamp
  * on an entity that supports metadata
  * @param by: User id performing the create operation
  * @param at: Timestamp of the creation
  */
case class EntityUpdateInfo(by:Long, at:DateTime) extends EntityMetaInfo {}


/**
  * Represents a metadata update for setting the unique id ( guid )
  * on an entity that supports metadata
  * @param id: a guid
  */
case class EntityUniqueInfo(id:String) extends EntityMetaInfo {}
