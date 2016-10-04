/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.core.tasks

/**
  * Created by kreddy on 2/26/2016.
  */
case class TaskEventArgs(name:String, status:String, state:TaskState, result:Any) {

}
