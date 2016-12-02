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
package slate.common.templates

/**
  * Represents a template that can be processed for variables/substitutions.
  * e.g. An email template such as :
  *
  * "Hi @{user.name}, Welcome to @{startup.name}, please click @{verifyUrl} to verify your email."
  *
  * @param name    : The name of the template ( e.g. "welcome_email" )
  * @param content : The text content of the template
  * @param parsed  : Whether this template has been parsed into its parts.
  * @param valid   : Whether this template is valid ( after parsing )
  * @param status  : Status message of the template ( if invalid )
  * @param group   : Optional group this template belongs to ( for organizing templates )
  * @param path    : Optional path of the template if coming from a file
  * @param parts   : Optional path of the template if coming from a file
  */
case class Template( name    : String,
                     content : String,
                     parsed  : Boolean = false,
                     valid   : Boolean = false,
                     status  : Option[String] = None,
                     group   : Option[String] = None,
                     path    : Option[String] = None ,
                     parts   : Option[List[TemplatePart]] = None) {
}
