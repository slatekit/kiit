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
package slate.common.encrypt


/**
  * Value class to represent a decrypted integer.
  * NOTE: This is useful as a parameter especially for meta programming
  * use cases such as in the API component
  * @param value
  */
case class DecInt(value:Int) extends AnyVal



/**
  * Value class to represent a decrypted long.
  * NOTE: This is useful as a parameter especially for meta programming
  * use cases such as in the API component
  * @param value
  */
case class DecLong(value:Long) extends AnyVal



/**
  * Value class to represent a decrypted Double.
  * NOTE: This is useful as a parameter especially for meta programming
  * use cases such as in the API component
  * @param value
  */
case class DecDouble(value:Double) extends AnyVal



/**
  * Value class to represent a decrypted string.
  * NOTE: This is useful as a parameter especially for meta programming
  * use cases such as in the API component
  * @param value
  */
case class DecString(value:String) extends AnyVal


