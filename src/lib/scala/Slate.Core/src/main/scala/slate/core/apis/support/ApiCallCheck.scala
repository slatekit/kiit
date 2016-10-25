/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.core.apis.support

import slate.core.apis.{Request, ApiBase}


/**
 * Represents the check / validation on an api before a method call is dynamically invoked
 *
 * e.g. "app.users.invite -email:'johndoe@gmail.com' -phone:1234567890 -promoCode:abc"
 *
 * @param success   :  Whether or not the check ( for dynamically calling a method ) was successful
 * @param area      :  The area of the api to call   ( "app" from example above )
 * @param apiName   :  The name of the api to call   ( "users" from example above  )
 * @param apiAction :  The action on the api to call ( "invite" from example above )
 * @param custom    :  not used currently
 * @param api       :  The actual api instance ( e.g. UsersApi instance )
 * @param cmd      :  The arguments supplied to the api call
 */
case class ApiCallCheck(
                         success      : Boolean ,
                         area         : String  ,
                         apiName      : String  ,
                         apiAction    : String  ,
                         custom       : Boolean ,
                         api          : ApiBase ,
                         cmd          : Request
                       )
{

}
