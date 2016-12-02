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
  * Represents either a plain text or variable in the template.
  * e.g.
  * "Hi @{user.name}, Welcome to @{startup.name}, please click @{verifyUrl} to verify your email."
  *
  * In the above, "Hi" is a part of type plain text
  * In the above, "user.name" is a part of type substitution
  *
  * @param text    : The text represented. e.g. Plain text "Hi" or Variable "user.name"
  * @param subType : Whether this is plain text or represents a substitution variable
  * @param pos     : The position of the text
  * @param length  : The length of the text
  */
case class TemplatePart(text:String, subType:Short, pos:Int, length:Int) {
}