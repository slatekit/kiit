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

import slate.common.DateTime


/**
  * Base entity with long id as primary key, timestamps, audit ( created by, updated by ) fields.
  *
  */
class Entity extends IEntity {
  override var id: Long = _


  override var createdAt  = DateTime.now()


  override var createdBy  = 0


  override var updatedAt  =  DateTime.now()


  override var updatedBy  = 0
}
