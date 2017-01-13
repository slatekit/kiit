/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.common

/**
 * represents a container for content and the conetnt type.
 * e.g.
 *
 * 1. string, json
 * 2. string, csv
 *
 * @param text
 * @param format
 */
case class Content(text:Option[String], format:String) {

  /**
   * whether this content is empty
   * @return
   */
  def isEmpty: Boolean = text.isEmpty


  /**
   * whether this content is present
   * @return
   */
  def isDefined: Boolean = text.isDefined


  /**
   * the length of the content
   * @return
   */
  def size : Int = text.fold(0)( t => t.length )


  /**
   * extension of the format
   * @return
   */
  def extension: String = format
}


object Content {

  def csv(text:String):Content = new Content(Option(text), "csv")


  def json(text:String):Content = new Content(Option(text), "json")


  def props(text:String):Content = new Content(Option(text), "props")
}
