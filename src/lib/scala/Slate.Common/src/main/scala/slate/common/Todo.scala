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


object Todo {

  /**
   * Indicates that code is not implemented
   * @param tag
   * @param msg
   * @param callback
   */
  def remove(tag: String = "", msg: String = "", callback: Option[() => Unit] = None) =
  {
    exec("TODO(remove): " + msg, tag, callback)
  }


  /**
   * Indicates that code is not implemented
   * @param tag
   * @param msg
   * @param callback
   */
  def notImplemented(tag: String = "", msg: String = "", callback: Option[() => Unit] = None) =
  {
    exec("TODO(not_implement): " + msg, tag, callback)
  }


  /**
   * Indicates that an implementation is required
   * @param tag
   * @param msg
   * @param callback
   */
    def implement(tag: String = "", msg: String = "", callback: Option[() => Unit] = None) =
    {
      exec("TODO(implement): " + msg, tag, callback)
    }


  /**
   * Indicates that a refactoring is required
   * @param tag
   * @param msg
   * @param callback
   */
    /// <param name="desc"></param>
    def refactor(tag: String = "", msg: String = "", callback: Option[() => Unit] = None) =
    {
      exec("TODO(refactor): " + msg, tag, callback)
    }


  /**
   * Indicates a bug
   * @param tag
   * @param msg
   * @param bugId
   * @param callback
   */
    def bug(tag: String = "", msg: String = "",  bugId: String = "", callback: Option[() => Unit] = None) =
    {
      exec("TODO(bug): " + msg, tag, callback)
    }


    private def exec(msg: String, tag: String, callback: Option[() => Unit] = None): Unit =
    {
      if(!callback.isEmpty)
      {
        callback.get()
      }
    }
}
