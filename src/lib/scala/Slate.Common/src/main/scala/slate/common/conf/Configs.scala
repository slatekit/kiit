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

class Configs(private val _primary:ConfigBase,
              private val _secondary:ConfigBase,
              encryptor:Option[Encryptor] = None) extends ConfigBase(encryptor) {


  override def raw:Any = (_primary, _secondary)


  /// <summary>
  override def getValue(key: String): AnyVal = {
    val cfval = getObject(key)

    if (cfval.isInstanceOf[Integer]) {
      cfval.asInstanceOf[Integer].intValue()
    }
    if (cfval.isInstanceOf[Double]){
      cfval.asInstanceOf[Double]
    }
    if (cfval.isInstanceOf[Boolean]){
      cfval.asInstanceOf[Boolean]
    }
    else
      0
  }


  /// <summary>
  override def containsKey(key: String): Boolean = getOrElse(_primary, _secondary, key)


  /// <summary>
  override def getObject(key: String): AnyRef = {
    if(_primary.containsKey(key))
      _primary.getObject(key)
    else
      _secondary.getObject(key)
  }


  def getOrElse(first:ConfigBase, second:ConfigBase, key:String):Boolean = {
    if(first.contains(key)) true else second.contains(key)
  }


  override def size(): Int = {
    1000
  }
}
