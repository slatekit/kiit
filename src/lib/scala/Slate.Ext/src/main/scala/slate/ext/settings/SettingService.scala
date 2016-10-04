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

package slate.ext.settings

import slate.common.Result
import slate.core.common.svcs.EntityServiceWithSupport

class SettingService  extends EntityServiceWithSupport[Setting]() {

  /**
   * creates a new setting.
   * @param group
   * @param name
   * @param valueDefault
   * @param valueType
   * @param value
   */
  def create(group:String, name:String, valueDefault:String, valueType:String, value:String):Unit = {
    val setting = new Setting()
    setting.group = group
    setting.name = name
    setting.valueDefault = valueDefault
    setting.valueType = valueType
    setting.value = value
    create(setting)
  }


  /**
   * maps multiple settings into the properties of the settings object supplied.
   * @param group
   * @param settings
   * @return
   */
  def map[T](group:String, settings:T):Result[T] = {
    notImplemented[T]()
  }
}
