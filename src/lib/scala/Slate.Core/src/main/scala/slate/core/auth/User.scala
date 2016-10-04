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

package slate.core.auth

import slate.common.Strings


/**
 * Represents a user for authentication purposes
 * @param id               : User id
 * @param fullName         : Full name of user
 * @param firstName        : First name
 * @param lastName         : Last name
 * @param email            : Email
 * @param phone            : Primary phone
 * @param isPhoneVerified  : Whether the phone is verified
 * @param isDeviceVerified : Whether the device is verified
 * @param isEmailVerified  : Whether the email is verified
 * @param city             : The city where user is in
 * @param state            : The state where user is in
 * @param zip              : The zip where user is in
 * @param country          : The country where user is in ( 2 digit county code )
 * @param tag              : A tag used for external links
 * @param schema           : The scheam or version of this model
 */
case class User (
                    id              :String    = ""    ,
                    fullName        :String    = ""    ,
                    firstName       :String    = ""    ,
                    lastName        :String    = ""    ,
                    email           :String    = ""    ,
                    phone           :String    = ""    ,
                    isPhoneVerified :Boolean   = false ,
                    isDeviceVerified:Boolean   = false ,
                    isEmailVerified :Boolean   = false ,
                    city            :String    = ""    ,
                    state           :String    = ""    ,
                    zip             :String    = ""    ,
                    country         :String    = ""    ,
                    tag             :String    = ""    ,
                    schema          :String    = ""    ,
                    token           :String    = ""
                  )
{


  def isMatch(user:User):Boolean =
  {
    if (!Strings.isMatch(user.id, this.id))
      return false
    true
  }
}
