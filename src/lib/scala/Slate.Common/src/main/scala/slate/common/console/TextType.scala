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
package slate.common.console

abstract class TextType(val color:String, val upperCase:Boolean ) {

  def format(text:String):String = {
    val checkedText = Option(text).getOrElse("")
    if ( upperCase ) checkedText.toUpperCase() else checkedText
  }
}


case object Title     extends TextType( Console.BLUE  , true  )
case object Subtitle  extends TextType( Console.CYAN  , false )
case object Url       extends TextType( Console.BLUE  , false )
case object Important extends TextType( Console.RED   , false )
case object Highlight extends TextType( Console.YELLOW, false )
case object Success   extends TextType( Console.GREEN , false )
case object Error     extends TextType( Console.RED   , false )
case object Text      extends TextType( Console.WHITE , false )
