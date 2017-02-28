/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.app

import slate.common.info.{About, Host, Lang}

trait AppMetaSupport {

  def appMeta(): AppMeta = ???


  /**
   * builds a list of properties fully describing this app by adding
   * all the properties from the about, host and lang fields.
    *
    * @return
   */
  def appInfo(addSeparator:Boolean = true) : List[(String,Any)] = {
    appMeta().info(addSeparator)
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
    // Get all the metadata ( List(( fieldname, value ) )
    val items = appInfo(addSeparator)

    // Find the max metadata property with the max length
    val maxPropLength = items.maxBy( m => m._1.length)._1.length

    // Supply each prop/value to caller
    items.foreach( item => callBack(maxPropLength, item))
  }
}

