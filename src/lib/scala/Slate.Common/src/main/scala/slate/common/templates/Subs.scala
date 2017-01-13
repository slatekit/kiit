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
package slate.common.templates

import slate.common.{DateTime, ListMap}

/**
  * Performs dynamic substitutions of variables in text.
  * Similar to interpolated strings, but at runtime. This allows for creating
  * @param items
  * @param setDefaults
  */
class Subs(items:Option[List[(String,(TemplatePart)=> String)]] = None, setDefaults:Boolean = true) {

  private val _groups = new ListMap[String, (TemplatePart) => String]()

  init()

  /**
    * whether this contains a substitution with the given key
    *
    * @param key
    * @return
    */
  def contains(key:String): Boolean = _groups.contains(key)


  /**
    * Size of the substitutions
    * @return
    */
  def size:Int = _groups.size


  /**
    * gets the value with the supplied key
    *
    * @param key
    * @return
    */
  def apply(key:String):String = lookup(key)


  /**
    * gets the value with the supplied key
    *
    * @param key
    * @return
    */
  def lookup(key:String):String =
  {
    if(!_groups.contains(key)) {
      ""
    }
    else {
      val sub = _groups(key)
      sub(new TemplatePart(key, TemplateConstants.TypeText, -1, -1))
    }
  }


  private def defaults():Unit = {

    // Default functions.
    _groups + ("today"     , ( sub ) => DateTime.today().toStringYYYYMMDD()             )
    _groups + ("yesterday" , ( sub ) => DateTime.today().addDays(-1).toStringYYYYMMDD() )
    _groups + ("tomorrow"  , ( sub ) => DateTime.today().addDays(1).toStringYYYYMMDD()  )
    _groups + ("t"         , ( sub ) => DateTime.today().toStringYYYYMMDD()             )
    _groups + ("t-1"       , ( sub ) => DateTime.today().addDays(-1).toStringYYYYMMDD() )
    _groups + ("t+1"       , ( sub ) => DateTime.today().addDays(1).toStringYYYYMMDD()  )
    _groups + ("today+1"   , ( sub ) => DateTime.today().addDays(1).toStringYYYYMMDD()  )
    _groups + ("today-1"   , ( sub ) => DateTime.today().addDays(-1).toStringYYYYMMDD() )
  }


  private def init():Unit = {

    // Add the default subs/variables
    if (setDefaults) {
      defaults()
    }

    // Add the custom variables
    if (items.isDefined) {
      items.get.foreach(sub => {
        _groups +(sub._1, sub._2)
      })
    }
  }
}
