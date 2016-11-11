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

package slate.common

trait Validation {

  /**
   * Validates the sequence of items. This is basically a foldLeft with the sequence supplied.
   *
   * @param startValue : The starting value
   * @param items      : The items to validate
   * @param f          : The function to use to validate
   * @tparam R         : The type of the result
   * @tparam S         : The type of the item
   * @return           : Type R result
   */
  protected def validate[S,R](startValue: R, items:Seq[S])(f: (R, S) => R): R = {
    var acc = startValue
    var these = items
    while (!these.isEmpty) {
      acc = f(acc, these.head)
      these = these.tail
    }
    acc
  }

  /**
    * Validates the sequence of items. This is basically a foldLeft with the sequence supplied.
    *
    * @param startValue : The starting value
    * @param items      : The items to validate
    * @param f          : The function to use to validate
    * @tparam R         : The type of the result
    * @tparam S         : The type of the item
    * @return           : Type R result
    */
  protected def validateResults[S,R](startValue: Result[R], items:Seq[S])(f: (S) => Result[R])
    : Result[R] =
  {
    var res = startValue
    var these = items
    var success = true
    while (!these.isEmpty && success) {
      res = f(these.head)
      these = these.tail
      success = res.success
    }
    res
  }
}
