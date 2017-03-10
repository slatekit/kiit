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

object InputFuncs {

  def interpret(value:String, functionName:String, handler:Option[(String) => String]): String = {
    if(value.startsWith(s"@{$functionName('")){
      val end = value.indexOf("')}")
      val paramVal = value.substring(4 + functionName.length, end)
      handler.fold[String]( paramVal ) ( interpreter => {
        interpreter(paramVal)
      })
    }
    else
      value
  }


  /**
   * Decrypts the text inside the value if value is "@{decrypt('abc')}"
   * @param value     : The value containing an optin @{decrypt function
   * @param decryptor : The callback to handle the decryption
   * @return
   */
  def decrypt(value:String, decryptor:Option[(String) => String] = None):String = {
    interpret(value, "decrypt", decryptor)
  }


  /**
   * converts a date string to a Date with support for aliases such as "today"
   * @param value
   * @return
   */
  def convertDate(value:String):DateTime = {
    value match {
      case null            => DateTime.now()
      case ""              => DateTime.now()
      case "@{today}"      => DateTime.today()
      case "@{tomorrow}"   => DateTime.today().addDays(1)
      case "@{yesterday}"  => DateTime.today().addDays(-1)
      case "@{now"         => DateTime.now()
      case _               => DateTime.parseNumericDate12(value)
    }
  }
}
