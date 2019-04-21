/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.examples.common

import slatekit.entities.EntityRepo
import slatekit.entities.EntityService
import slatekit.entities.Entities

// Service class for user
// 1. extends the EntityService class with parameterized type
// 2. this wraps the repo for purpose of adding optional business logic
//    before and/or after any crud operations
// 3. can also add additional methods specific to user here.
class UserService(entities: Entities, repo: EntityRepo<Long, User>) : EntityService<Long, User>(repo)
{
  // validate for login
  fun validate(email:String, pass:String) : Boolean {
    // perform email/pass validation here
    return false
  }
}
