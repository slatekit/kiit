import slate.common._
import slate.common.envs.EnvItem
import slate.common.results.ResultSupportIn

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


object CommonTests extends ResultSupportIn  {
  def main(args:Array[String]):Unit = {
    println("common app")
    testBuilding()
  }


  def testReturnValue(code:Int): Result[Boolean] = {
    if(code <= 0 )
      return new FailureResult[Boolean](code = -1)
    new SuccessResult[Boolean](true,1)
  }


  def testBuilding(): Unit = {
     print( no())
     print( yes())

     print( success(true))
     print( success(1))
     print( success(1, tag = Some("map test")).map( i => i + 1))
     print( success("slate kit"))
     print( success(EnvItem("dev", "dev")))

     print( confirm(true))
     print( confirm(1))
     print( confirm("slate kit"))
     print( confirm(EnvItem("dev", "dev")))

    print( unAuthorized())
    print( failure())
    print( failure(tag = Some("map Int from failure")).map[Int](i => 0))
    print( failure(tag = Some("map Int from results")).asInstanceOf[Result[Int]].map[Int](i => 0))
    print( failure(tag = Some("map String")).map[String](i => "0"))

    print( NoResult )
    print( "Non Result Type")
  }


  def print(res:Any): Unit= {
    println("=========================")
    println(res)
    res match {
      case b:SuccessResult[x]                     => println("case: SuccessResult")
      case c:FailureResult[x]                     => println("case: ErrorResult")
      case NoResult                               => println("case: NoResult")
      case _                                      => println("case: Unknown")
    }
    if(res.isInstanceOf[Result[Any]]) res.asInstanceOf[Result[Any]].print()
    println()
  }
}
