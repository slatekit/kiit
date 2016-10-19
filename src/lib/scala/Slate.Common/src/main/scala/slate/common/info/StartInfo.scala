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

package slate.common.info



case class StartInfo (
                   args   : Option[Array[String]] = None        ,
                   log    : String = "{@app}-{@env}-{@date}.log",
                   config : String = "{@app}.config"            ,
                   env    : String = "dev"
               )
{

  def log( callback:(String,Any) => Unit) : Unit = {
    callback("args"   , args.toString )
    callback("log"     , log )
    callback("config" , config )
    callback("env"   , env )
  }
}


object StartInfo
{
  val none = new StartInfo()
}