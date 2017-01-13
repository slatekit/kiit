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

trait I18nSupport {

  val resources:Option[I18nStrings] = None

  def translate(name:String):String = {
    if(resources.isDefined){
      resources.get.translate(name, "")
    }
    else
      name
  }
}
