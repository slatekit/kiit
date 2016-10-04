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
package slate.ext.users

case class UserId(version:Int, id:Long, guid:String, country:String, region:String ) {

  def delimited():String = {
    version + "," + id + "," + guid + "," + country + "," + region
  }
}


object UserId {

  val empty = new UserId (1, 0, "", "", "")
}
