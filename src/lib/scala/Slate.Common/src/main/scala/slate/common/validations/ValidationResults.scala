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
package slate.common.validations

import slate.common.Reference

case class ValidationResults(success : Boolean                 ,
                             msg     : String            = ""  ,
                             ref     : Option[Reference] = None,
                             code    : Int               = 0   ,
                             results : Option[List[ValidationResult]])
{
  // Any errors ?
  val hasErrors = results.fold(false)( e => e.isEmpty )
}


object ValidationResults {

  def apply(errors:Option[List[ValidationResult]]): ValidationResults = {
    val success = errors.isEmpty || errors.fold(true)( e => e.isEmpty )
    val message = errors.fold("")( e => e.head.msg.getOrElse("") )
    val code = if (success) 1 else 0
    new ValidationResults(success, message, None, code, errors)
  }
}

