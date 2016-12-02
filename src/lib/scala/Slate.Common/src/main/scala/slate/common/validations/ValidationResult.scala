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

case class ValidationResult(
                             success : Boolean                 ,
                             msg     : Option[String]    = None,
                             ref     : Option[Reference] = None,
                             code    : Int               = 0
)
{
}
