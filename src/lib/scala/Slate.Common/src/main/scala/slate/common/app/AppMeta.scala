package slate.common.app

import slate.common.Reflector
import slate.common.info._

/**
 * Created by kv on 11/4/2015.
 */
case class AppMeta(
                     about  : About     =  About.none    ,
                     host   : Host      =  Host.none     ,
                     lang   : Lang      =  Lang.none     ,
                     status : Status    =  Status.none   ,
                     start  : StartInfo =  StartInfo.none
                  )
{

  /**
    * builds a list of properties fully describing this app by adding
    * all the properties from the about, host and lang fields.
    *
    * @return
    */
  def info(categorize:Boolean = true) : List[(String,Any)] = {

    val items = new scala.collection.mutable.ListBuffer[(String,Any)]

    def collect(area:String, metaPairs:Map[String,Any]):Unit = {
      if(categorize){
        items.append((area, "==================================="))
      }
      metaPairs.foreach( p => items.append((p._1, p._2)))
    }

    // Collect all the metadata in corresponding areas.
    collect("ABOUT", Reflector.getFields(about))
    collect("HOST" , Reflector.getFields(host))
    collect("LANG" , Reflector.getFields(lang))

    items.toList
  }
}
