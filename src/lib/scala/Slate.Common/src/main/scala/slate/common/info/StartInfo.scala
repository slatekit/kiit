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
                   args   : String = ""                         ,
                   logFile: String = "{@app}-{@env}-{@date}.log",
                   config : String = "{@app}.config"            ,
                   env    : String = "dev"                      ,
                   rootDir: String = ""                         ,
                   confDir: String = ""
               )
{

  def log( callback:(String,Any) => Unit) : Unit = {
    callback("args"      , args    )
    callback("log"       , logFile )
    callback("config"    , config  )
    callback("env"       , env     )
    callback("rootDir"   , rootDir )
    callback("confDir"   , confDir )
  }
}


object StartInfo
{
  val none = new StartInfo()


  def apply(args:String, env:String, conf:String):StartInfo = {
    StartInfo(
      args = args,
      env  = env,
      config = conf,
      rootDir = System.getProperty("user.dir"),
      confDir = ""
    )
  }
}