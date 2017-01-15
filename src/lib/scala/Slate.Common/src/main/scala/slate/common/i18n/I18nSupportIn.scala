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

package slate.common.i18n

trait I18nSupportIn {

  protected def res():Option[I18nStrings] = None


  protected def translate(name:String):String = {
    res().fold(name)( res => res.translate(name, ""))
  }
}
