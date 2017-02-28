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


object Random {

  val NUMS = "0123456789"
  val LETTERS_LCASE = "abcdefghijklmnopqrstuvwxyz"
  val LETTERS_ALL   = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
  val ALPHA = "0123456789abcdefghijklmnopqrstuvwxyz"
  val ALPHASYM = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=[]{}|;:,./<>?"
  val rnd = new scala.util.Random()


  def string3():String = stringN(3)


  def string6():String = stringN(6)


  def digits3():Int = digitsN(3)


  def digits6():Int = digitsN(6)


  def alpha3():String = alphaN(3)


  def alpha6():String = alphaN(6)


  def alphaSym3():String = alphaN(3)


  def alphaSym6():String = alphaN(6)


  def digitsN(n:Int):Int = Integer.parseInt(randomize(n, NUMS))


  def stringN(n:Int, allowUpper:Boolean = true):String = {
    if(allowUpper) randomize(n, LETTERS_ALL) else randomize(n, LETTERS_LCASE)
  }


  def alphaN(n:Int):String = randomize(n, ALPHA)


  def alphaSymN(n:Int):String = randomize(n, ALPHASYM)


  def stringGuid(includeDashes:Boolean = true):String =
  {
    val result = java.util.UUID.randomUUID().toString().toUpperCase()
    if(!includeDashes)
      result.replaceAllLiterally("-","")
    else
      result
  }


  def randomize(n:Int, allowedChars:String):String =
  {
    val text = 0.until(n).foldLeft("")( (s, ndx) => {
      s + allowedChars.charAt(rnd.nextInt(allowedChars.length()))
    })
    text
  }
}
