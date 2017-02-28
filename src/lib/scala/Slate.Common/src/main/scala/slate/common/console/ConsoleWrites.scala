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

import slate.common.{IoAction, Strings}

trait ConsoleWrites {

  val settings:ConsoleSettings
  val TAB = "\t"

  /**
    * IO abstraction for system.println.
    * Assists with testing and making code a bit more "purely functional"
    * This is a simple, custom alternative to the IO Monad.
    * Refer to IO.scala for details.
    */
  val _io:IoAction[Any,Unit]


  /**
    * Map the text type to functions that can implement it.
    */
  val lookup = Map[TextType, (String,Boolean) => Unit ](
    Title      ->  title     ,
    Subtitle   ->  subTitle  ,
    Url        ->  url       ,
    Important  ->  important ,
    Highlight  ->  highlight ,
    Success    ->  success   ,
    Error      ->  error     ,
    Text       ->  text
  )

  /**
    * Write many items based on the semantic modes
    *
    * @param items
    */
  def writeItems(items:List[(TextType,String,Boolean)]): Unit = {
    items.foreach( item => { writeItem( item._1, item._2, item._3) })
  }


  /**
    * Write many items based on the semantic modes
    *
    * @param items
    */
  def writeItemsByText(items:List[(String,String,Boolean)]): Unit = {
    items.foreach( item => { writeItem( convert(item._1), item._2, item._3) })
  }


  /**
    * Write a single item based on the semantic mode
    *
    * @param mode
    * @param msg
    * @param endLine
    */
  def writeItem(mode:TextType, msg:String, endLine:Boolean): Unit = {
    if(lookup.contains(mode)){
      lookup(mode)(msg, endLine)
    }
  }


  /**
    * Converts the string representation of a semantic text to the strongly typed object
    *
    * @param mode
    */
  def convert(mode:String): TextType = {
    mode.toLowerCase() match {
      case "title"      =>  Title
      case "subtitle"   =>  Subtitle
      case "url"        =>  Url
      case "important"  =>  Important
      case "highlight"  =>  Highlight
      case "success"    =>  Success
      case "srror"      =>  Error
      case "text"       =>  Text
      case _            =>  Text
    }
  }


  /**
    * prints text in the color supplied.
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def write(color:String, text:String, endLine:Boolean): Unit =
  {
    val finalText = if(endLine)
      color + " " + text + Strings.newline()
    else
      color + " " + text

    _io.run(finalText)
  }


  /**
    * prints a empty line
    */
  def line():Unit = _io.run(Strings.newline())


  /**
   * prints a empty line
   */
  def lines(count:Int): Unit = 0.to(count).foreach(i => line())


  /**
    * prints a tab count times
    *
    * @param count
    */
  def tab(count:Int = 1): Unit = 0.to(count).foreach(i => print(TAB))


  /**
    * Writes the text using the TextType
    *
    * @param mode
    * @param text
    * @param endLine
    */
  def write(mode:TextType, text:String, endLine:Boolean = true): Unit = {
    write(mode.color, mode.format(text), endLine)
  }


  /**
    * prints text in title format ( UPPERCASE and BLUE )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def title(text:String, endLine:Boolean = true):Unit = write(Title, text, endLine)


  /**
    * prints text in subtitle format ( CYAN )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def subTitle(text:String, endLine:Boolean = true):Unit = write(Subtitle, text, endLine)


  /**
    * prints text in url format ( BLUE )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def url(text:String, endLine:Boolean = true):Unit = write(Url, text, endLine)


  /**
    * prints text in important format ( RED )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def important(text:String, endLine:Boolean = true):Unit = write(Important, text, endLine)


  /**
    * prints text in highlight format ( YELLOW )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def highlight(text:String, endLine:Boolean = true):Unit = write(Highlight, text, endLine)



  /**
    * prints text in title format ( UPPERCASE and BLUE )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def success(text:String, endLine:Boolean = true):Unit = write(Success, text, endLine)


  /**
    * prints text in error format ( RED )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def error(text:String, endLine:Boolean = true):Unit = write(Error, text, endLine)


  /**
    * prints text in normal format ( WHITE )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def text(text:String, endLine:Boolean = true):Unit = write(Text, text, endLine)


  /**
    * prints text in normal format ( WHITE )
    *
    * @param text    : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def label(text:String, endLine:Boolean = true):Unit =
  {
    val color = if(settings.darkMode) Console.BLACK else Console.WHITE
    write(color, text, endLine)
  }


  /**
    * Prints a list of items with indentation
    *
    * @param items
    * @param isOrdered
    */
  def list(items:Seq[_], isOrdered:Boolean = false ):Unit = {

    items.indices.foreach( ndx => {
      val item = items(ndx)
      val value = Strings.serialize(item)
      val prefix = if(isOrdered) (ndx + 1).toString + ". " else "- "
      text(TAB + prefix + value, endLine = true)
    })
    line()
  }


  /**
    * prints text using a label : value format
    *
    * @param key     :
    * @param value   : the text to print
    * @param endLine : whether or not to include a newline at the end
    */
  def keyValue(key:String, value:String, endLine:Boolean = true):Unit =
  {
    label(key + " = ", false)
    text(value, endLine)
  }
}
