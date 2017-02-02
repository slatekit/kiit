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


trait EnWithMeta[T] {

  def isPersisted(): Boolean =  getMetaId().isDefined

  def metaId(id:EntityMetaId) = metaUpdate(Some(id))

  def metaCreated(by:Long, at:DateTime = DateTime.now()) = metaUpdate(Some(EntityCreateInfo(by, at)))

  def metaUpdated(by:Long, at:DateTime = DateTime.now()) = metaUpdate(Some(EntityUpdateInfo(by, at)))

  def metaUpdate(id:Option[EntityMetaInfo] = None) : T = ???

  def getMetaId():EntityMetaId = ???
}
