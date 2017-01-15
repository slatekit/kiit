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
    * This stops processing further items if one fails
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
    if(items == null || items.size == 0 ) {
      startValue
    }
    else {
      var res = startValue
      Loops.repeat(items.size, (ndx) => {
        val item = items(ndx)
        res = f(item)
        res.success
      })
      res
    }
  }
}
