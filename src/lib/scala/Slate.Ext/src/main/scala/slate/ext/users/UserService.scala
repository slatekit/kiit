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

package slate.ext.users

import slate.common.Strings
import slate.common.databases.Db
import slate.common.query.Query
import slate.core.common.svcs.EntityServiceWithSupport


class UserService extends EntityServiceWithSupport[User]()
{


  def getPhoneConfirmCode(id:Long): Long = {
    // {call testScalarBit(1,?)}
    val code = new Db(context.con.get).getScalarLong("{call getPhoneConfirmCode(?)}", Some(List[Any](id)))
    code
  }



  def getUserByEmail(email:String): Option[User] = {
    // {call testScalarBit(1,?)}
    val model = new Db(context.con.get).mapOne[User]("{call getUserByEmail(?)}",_repo.mapper(), Some(List[Any](email)))
    model
  }


  def getUserById(id:String, refUserId:Option[String], refUser:Option[User]):Option[User] = {

    // CASE 1: Phone
    if(id.startsWith("phone:")){
      return getByPhone(id.substring(6).trim)
    }
    // CASE 2: Email
    else if (id.startsWith("email:")){
      return getByEmail(id.substring(6).trim)

    }
    // CASE 3: Reference to user
    else if (Strings.isMatch(id, "@user") && !refUser.isDefined && refUserId.isDefined){
      val decrypted = decrypt(refUserId.get)
      val userId = UserHelper.parseUserId(decrypted)
      val userCheck = getByUserId(userId.guid)
      return userCheck
    }
    // CASE 4: Reference to user supplied
    else if (Strings.isMatch(id, "@user") && refUser.isDefined){
      return refUser
    }
    // CASE 5: Full user id
    val decrypted = decrypt(id)
    val userId = UserHelper.parseUserId(decrypted)
    val userCheck = getByUserId(userId.guid)
    userCheck
  }


  def getByUserId(guid:String):Option[User] =
  {
    findFirst(new Query().where("userId", "=", guid))
  }


  def getByEmail(email:String):Option[User] =
  {
    findFirst(new Query().where("email", "=", email))
  }


  def getByPhone(phone:String):Option[User] =
  {
    findFirst(new Query().where("primaryPhone", "=", phone))
  }
}
