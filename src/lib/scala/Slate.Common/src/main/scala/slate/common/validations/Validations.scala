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

import slate.common.Result

object Validations {

  /**
    * checks if all the rules are true against the input
    * @param rules : The rules to check
    * @param input : The input to check rules against
    * @tparam T
    * @return
    */
  def allTrue [T](rules:Seq[(T) => Boolean], input:T): Boolean = rules.forall( rule => rule(input))


  /**
    * checks if all the rules are false against the input
    * @param rules : The rules to check
    * @param input : The input to check rules against
    * @tparam T
    * @return
    */
  def allFalse[T](rules:Seq[(T) => Boolean], input:T): Boolean = rules.forall( rule => !rule(input))


  /**
    * checks if any of the rules are true against the input
    * @param rules : The rules to check
    * @param input : The input to check rules against
    * @tparam T
    * @return
    */
  def anyTrue [T](rules:Seq[(T) => Boolean], input:T): Boolean = rules.exists( rule => rule(input))


  /**
    * checks if any of the rules are false against the input
    * @param rules : The rules to check
    * @param input : The input to check rules against
    * @tparam T
    * @return
    */
  def anyFalse[T](rules:Seq[(T) => Boolean], input:T): Boolean = rules.exists( rule => !rule(input))


  def collect[T](rules:Seq[(String) => ValidationResult], text:String): List[ValidationResult] = {
    rules.map( rule => rule(text) )
         .filter( result => !result.success )
         .toList
  }


  def collect(rules:Seq[() => ValidationResult]): List[ValidationResult] = {
    rules.map( rule => rule() )
      .filter( result => !result.success )
      .toList
  }
}
