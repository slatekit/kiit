/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.common.reflect

import scala.reflect.runtime.universe.{typeOf}

import slate.common.DateTime


object ReflectConsts {

  val BoolType   = typeOf[Boolean]
  val ShortType  = typeOf[Short]
  val IntType    = typeOf[Int]
  val LongType   = typeOf[Long]
  val FloatType  = typeOf[Float]
  val DoubleType = typeOf[Double]
  val DateType   = typeOf[DateTime]
  val StringType = typeOf[String]
}
