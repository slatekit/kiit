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
package slate.common

import java.io.{InputStreamReader, BufferedReader, InputStream}

import slate.common.http.HttpRequest
import slate.common.results.ResultCode

//http://etorreborre.blogspot.com/2011/12/pragmatic-io.html
//http://functionaltalks.org/2013/06/20/paul-chiusano-how-to-write-a-functional-program-with-io-mutation-and-other-effects/
class IO[+A](ioAction:() => A)
{
  def run(): A = ioAction()


  def map[B]( f: A => B )       : IO[B] =
  {
    new IO[B]( () => {
      f( ioAction() )
    })
  }


  def flatMap[B]( f: A => IO[B]): IO[B] =
  {
    new IO[B]( () => {
      f( ioAction() ).run()
    })
  }
}



case class Print    (msg:String     ) extends IO[Unit]  ( () => Predef.print(msg) )
case class PrintLn  (msg:String     ) extends IO[Unit]  ( () => Predef.println(msg) )
case class GetLn    (msg:String     ) extends IO[String]( () => scala.io.StdIn.readLine() )


object IO {

  def failedIO[A](msg:String): IO[Result[A]] = {
    new IO( () => new FailureResult[A](msg = Option(msg), code = ResultCode.BAD_REQUEST))
  }


  def toString(input: InputStream): String =
  {
    val reader = new BufferedReader(new InputStreamReader(input))
    val buffer = new StringBuilder()
    var moreData = true
    while (moreData) {
      val line = reader.readLine()
      if (line == null)
      {
        moreData = false
      }
      else
      {
        buffer.append(line)
      }
    }
    val content = buffer.toString()
    content
  }
}