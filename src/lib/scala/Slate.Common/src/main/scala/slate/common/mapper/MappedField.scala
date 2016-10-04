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

package slate.common.mapper


import scala.reflect.runtime.universe._


case class MappedField(
                          name: String            = "",
                          desc:String             = "",
                          dataType:Type           = typeOf[String],
                          storedName:String       = "",
                          pos:Int                 = 0,
                          isRequired:Boolean      = true,
                          minLength:Int           = -1,
                          maxLength:Int           = -1,
                          defaultVal:Option[Any]  = None,
                          example:String          = ""
                      )
{

}