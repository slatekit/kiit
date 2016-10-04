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

package slate.core.apis


case class ApiArg (name: String = "", desc:String = "", required:Boolean = true,
                   defaultVal:String = "", eg:String = "" )
  extends scala.annotation.StaticAnnotation
{

}