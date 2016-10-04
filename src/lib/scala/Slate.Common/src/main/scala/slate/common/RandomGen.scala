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


  def digits6():Int =
  {
    val LEN = 6
    val sb = new StringBuilder(LEN)
    for (i <- 0 until LEN)
      sb.append(NUMS.charAt(rnd.nextInt(NUMS.length())))

    val number = Integer.parseInt(sb.toString())
    number
  }


  def digits3():Int =
  {
    val LEN = 3
    val sb = new StringBuilder(LEN)
    for (i <- 0 until LEN)
      sb.append(NUMS.charAt(rnd.nextInt(NUMS.length())))

    val number = Integer.parseInt(sb.toString())
    number
  }


  def string6():String =
  {
    val LEN = 6
    val sb = new StringBuilder(LEN)
    for (i <- 0 until LEN)
      sb.append(LETTERS.charAt(rnd.nextInt(LETTERS.length())))

    val result = sb.toString()
    result
  }


  def alpha6():String =
  {
    val LEN = 6
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
