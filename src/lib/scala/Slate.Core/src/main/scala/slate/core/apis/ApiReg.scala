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

case class ApiReg(api:ApiBase,
                  declaredOnly:Boolean = true,
                  roles:Option[String] = None,
                  auth:Option[String] = None,
                  protocol:Option[String] = Some("*")
                 )
{
}
