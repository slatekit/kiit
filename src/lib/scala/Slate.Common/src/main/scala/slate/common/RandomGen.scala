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

package slate.common


import scala.util.Random


object RandomGen {

  val NUMS = "0123456789"
  val LETTERS = "abcdefghijklmnopqrstuvwxyz"
  val ALPHA = "0123456789abcdefghijklmnopqrstuvwxyz"
  val rnd = new Random()


  def alpha3():String = alphaN(3)


  def alpha6():String = alphaN(6)


  def digits3():Int = digitsN(3)


  def digits6():Int = digitsN(6)


  def string3():String = stringN(3)


  def string6():String = stringN(6)


  def digitsN(n:Int):Int =
  {
    val LEN = n
    val sb = new StringBuilder(LEN)
    for (i <- 0 until LEN)
      sb.append(NUMS.charAt(rnd.nextInt(NUMS.length())))

    val number = Integer.parseInt(sb.toString())
    number
  }


  def stringN(n:Int):String =
  {
    val LEN = n
    val sb = new StringBuilder(LEN)
    for (i <- 0 until LEN)
      sb.append(LETTERS.charAt(rnd.nextInt(LETTERS.length())))

    val result = sb.toString()
    result
  }


  def alphaN(n:Int):String =
  {
    val LEN = n
    val sb = new StringBuilder(LEN)
    for (i <- 0 until LEN)
      sb.append(ALPHA.charAt(rnd.nextInt(ALPHA.length())))

    val result = sb.toString()
    result
  }


  def stringGuid(includeDashes:Boolean = true):String =
  {
    val result = java.util.UUID.randomUUID().toString().toUpperCase()
    if(!includeDashes)
      return result.replaceAllLiterally("-","")
    result
  }
}
