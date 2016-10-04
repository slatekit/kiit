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
package slate.common

/**
  * Represents credentials for accessing an API. This is useful for representing credentials
  * for other systems such as AWS keys, Twilio, SendGrid, etc.
  * @param account : The account for the API
  * @param key     : The key for the API
  * @param pass    : The password for the API  ( optional )
  * @param env     : Optional environment of the API ( e.g. dev, qa )
  * @param tag     : Optional tag
  */
case class ApiCredentials(
                           account  : String = "",
                           key      : String = "",
                           pass     : String = "",
                           env      : String = "",
                           tag      : String = ""
)
{

}
