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

package slate.common.validations

import slate.common.Reference

class Validator[T] {

  def validate(item:T): ValidationResults = {
    ValidationResults(None)
  }


  def result(isValid:Boolean, ref:Option[Reference], error:String): ValidationResult = {
    if(isValid){
      ValidationResult(true, Option(error), ref, 1)
    }
    else {
      ValidationResult(false, Option(error), ref, 0)
    }
  }
}
