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

package slate.common.app

import slate.common.info.{About, Host, Lang}
import slate.common.{Result, Reflector, Strings}

import scala.collection.mutable.ListBuffer

trait AppMetaSupport {

  def appMeta(): AppMeta = ???


  /**
   * info about this app
    *
    * @return
   */
  def appAbout: About = appMeta().about


  /**
    * url of the app
    *
    * @return
    */
  def appUrl: String =  appMeta().about.url


  /**
    * version of the app
    *
    * @return
    */
  def appVersion: String =  appMeta().about.version


  /**
    * examples of running the app
    *
    * @return
    */
  def appExamples: String =  appMeta().about.examples


  /**
    * info about the host computer hosting this app
    *
    * @return
    */
  def appHost: Host =  appMeta().host


  /**
   * info about the language and version used to run this app
    *
    * @return
   */
  def appLang: Lang =  appMeta().lang


  /**
   * builds a list of properties fully describing this app by adding
   * all the properties from the about, host and lang fields.
    *
    * @return
   */
  def appInfo(addSeparator:Boolean = true) : List[(String,Any)] = {
    val items = new ListBuffer[(String,Any)]

    if(addSeparator) {
      items.append(("ABOUT", "==================================="))
    }
    for((k,v) <- Reflector.getFields(appAbout)) { items.append((k,v)) }

    if(addSeparator){
      items.append(("HOST", "==================================="))
    }
    for((k,v) <- Reflector.getFields(appHost)) { items.append((k,v)) }

    if(addSeparator){
      items.append(("LANG", "==================================="))
    }
    for((k,v) <- Reflector.getFields(appLang)) { items.append((k,v)) }

    items.toList
  }


  def appLogStart(callback: (String, String) => Unit ): Unit = {
    val meta = appMeta()

    callback( "name             ",  meta.about.name              )
    callback( "desc             ",  meta.about.desc              )
    callback( "version          ",  meta.about.version           )
    callback( "tags             ",  meta.about.tags              )
    callback( "group            ",  meta.about.group             )
    callback( "region           ",  meta.about.region            )
    callback( "contact          ",  meta.about.contact           )
    callback( "url              ",  meta.about.url               )
    callback( "args             ",  meta.start.args.toString     )
    callback( "env              ",  meta.start.env               )
    callback( "config           ",  meta.start.config            )
    callback( "log              ",  meta.start.log               )
    callback( "started          ",  meta.status.started.toString )
    callback( "host.name        ",  meta.host.name               )
    callback( "host.ip          ",  meta.host.ip                 )
    callback( "host.origin      ",  meta.host.origin             )
    callback( "host.version     ",  meta.host.version            )
    callback( "lang.name        ",  meta.lang.name               )
    callback( "lang.version     ",  meta.lang.version            )
    callback( "lang.versionNum  ",  meta.lang.versionNum         )
    callback( "lang.java        ",  meta.lang.origin             )
    callback( "lang.home        ",  meta.lang.home               )
  }


  def appLogEnd(callback: (String, String) => Unit): Unit =
  {
    val meta = appMeta()
    callback( "name             ",  meta.about.name                )
    callback( "desc             ",  meta.about.desc                )
    callback( "version          ",  meta.about.version             )
    callback( "tags             ",  meta.about.tags                )
    callback( "group            ",  meta.about.group               )
    callback( "region           ",  meta.about.region              )
    callback( "contact          ",  meta.about.contact             )
    callback( "url              ",  meta.about.url                 )
    callback( "args             ",  meta.start.args.toString()     )
    callback( "env              ",  meta.start.env                 )
    callback( "config           ",  meta.start.config              )
    callback( "log              ",  meta.start.log                 )
    callback( "started          ",  meta.status.started.toString() )
    callback( "ended            ",  meta.status.ended.toString()   )
    callback( "duration         ",  meta.status.duration.toString())
    callback( "status           ",  meta.status.status             )
    callback( "errors           ",  meta.status.errors.toString()  )
    callback( "error            ",  meta.status.error              )
    callback( "host.name        ",  meta.host.name                 )
    callback( "host.ip          ",  meta.host.ip                   )
    callback( "host.origin      ",  meta.host.origin               )
    callback( "host.version     ",  meta.host.version              )
    callback( "lang.name        ",  meta.lang.name                 )
    callback( "lang.version     ",  meta.lang.version              )
    callback( "lang.versionNum  ",  meta.lang.versionNum           )
    callback( "lang.java        ",  meta.lang.origin               )
    callback( "lang.home        ",  meta.lang.home                 )
  }


  /**
    * iterates over all the items in the metainfo
    *
    * @param callBack
    */
  def appInfoList(addSeparator:Boolean, callBack: (Int,(String,Any)) => Unit): Unit = {
    val items = appInfo(addSeparator)
    val keys = ListBuffer[String]()
    for(item <- items ){
      keys.append(item._1)
    }
    val maxLength =Strings.maxLength(keys.toList)
    for(item <- items ){
      callBack(maxLength, item)
    }
  }
}

