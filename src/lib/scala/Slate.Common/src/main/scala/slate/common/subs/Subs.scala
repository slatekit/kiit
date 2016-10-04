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
package slate.common.subs

import slate.common.{DateTime, ListMap}

class Subs(setDefaults:Boolean = true) {
  private val _groups = new ListMap[String, (Sub) => String]()

  if(setDefaults) {
    defaults()
  }

  def contains(key:String): Boolean = _groups.contains(key)


  /**
    * adds a key/value to this collection
    *
    * @param key
    * @param value
    */
  def update(key:String, value:(Sub) => String) = {
    _groups(key) = value
  }


  /**
    * gets the value with the supplied key
    *
    * @param key
    * @return
    */
  def apply(key:String):String =
  {
    if(!_groups.contains(key))
      return ""
    val sub = _groups(key)
    sub(new Sub(key, SubConstants.TypeText, -1, -1))
  }


  /**
    * parses the text into a list of substitutions
    *
    * @param text
    * @return
    */
  def parse(text:String): List[Sub] = {
    new SubParser(text).parse()
  }


  def resolve(text:String): Option[String] = {
    val tokens = parse(text)
    if(tokens == null || tokens.size == 0){
      return Some(text)
    }
    var finalText = ""
    for(token <- tokens){
      if(token.subType == SubConstants.TypeText){
        finalText += token.text
      }
      else if(token.subType == SubConstants.TypeSub){
        finalText += this(token.text)
      }
    }
    Some(finalText)
  }


  private def defaults():Unit = {

    // Default functions.
    _groups("today"     )  = ( sub ) => { DateTime.today().toStringYYYYMMDD()             }
    _groups("yesterday" )  = ( sub ) => { DateTime.today().addDays(-1).toStringYYYYMMDD() }
    _groups("tomorrow"  )  = ( sub ) => { DateTime.today().addDays(1).toStringYYYYMMDD()  }
    _groups("t"         )  = ( sub ) => { DateTime.today().toStringYYYYMMDD()             }
    _groups("t-1"       )  = ( sub ) => { DateTime.today().addDays(-1).toStringYYYYMMDD() }
    _groups("t+1"       )  = ( sub ) => { DateTime.today().addDays(1).toStringYYYYMMDD()  }
    _groups("today+1"   )  = ( sub ) => { DateTime.today().addDays(1).toStringYYYYMMDD()  }
    _groups("today-1"   )  = ( sub ) => { DateTime.today().addDays(-1).toStringYYYYMMDD() }
  }
}
