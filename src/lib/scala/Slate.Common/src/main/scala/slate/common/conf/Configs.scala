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

package slate.common.conf

import slate.common.encrypt.Encryptor
import slate.common.info.Folders
import slate.common.subs.Subs

class Configs(private val _primary:ConfigBase,
              private val _secondary:ConfigBase,
              encryptor:Option[Encryptor] = None) extends ConfigBase(encryptor) {


  override def raw:Any = (_primary, _secondary)


  /// <summary>
  override def getValue(key: String): AnyVal = {
    val cfval = getObject(key)

    if (cfval.isInstanceOf[Integer]) {
      return cfval.asInstanceOf[Integer].intValue()
    }
    if (cfval.isInstanceOf[Double]){
      return cfval.asInstanceOf[Double]
    }
    if (cfval.isInstanceOf[Boolean]){
      return cfval.asInstanceOf[Boolean]
    }
    0
  }


  /// <summary>
  override def containsKey(key: String): Boolean = {
    if(_primary.containsKey(key))
      return true
    if(_secondary.contains(key))
      return true
    false
  }


  /// <summary>
  override def getObject(key: String): AnyRef = {
    if(_primary.containsKey(key))
      return _primary.getObject(key)
    if(_secondary.contains(key))
      return _secondary.getObject(key)
    null
  }


  override def size(): Int = {
    1000
  }
}
