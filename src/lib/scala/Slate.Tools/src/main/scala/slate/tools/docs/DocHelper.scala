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

package slate.tools.docs

import java.io.File

object DocHelper {

  def buildComponentFolder(root:String, doc:Doc):String =
  {
    // s"${root}\src\lib\scala\Slate.Common\src\main\scala
    val componentFolder = doc.namespace.replaceAllLiterally(".", "\\")
    val result = s"${root}\\src\\lib\\scala\\${doc.area}\\src\\main\\scala\\${componentFolder}"
    result
  }


  def buildComponentPath(root:String, doc:Doc):String =
  {
    // s"${root}\src\lib\scala\Slate.Common\src\main\scala
    val componentFolder = doc.source.replaceAllLiterally(".", "\\") + ".scala"
    val result = s"${root}\\src\\lib\\scala\\${doc.area}\\src\\main\\scala\\${componentFolder}"
    result
  }


  def buildDistDocComponentPath(root:String, output:String, doc:Doc):String =
  {
    // s"${root}\src\lib\scala\Slate.Common\src\main\scala
    val result = s"${root}\\${output}"
    result
  }


  def buildComponentExamplePath(root:String, doc:Doc):String =
  {
    // s"${root}\src\lib\scala\Slate.Common\src\main\scala
    val result = s"${root}\\src\\apps\\scala\\slate-examples\\src\\main\\scala\\slate\\examples\\${doc.example}.scala"
    result
  }


  def buildComponentExamplePathLink(doc:Doc):String =
  {
    // s"${root}\src\lib\scala\Slate.Common\src\main\scala
    val result = s"\\src\\apps\\scala\\slate-examples\\src\\main\\scala\\slate\\examples\\${doc.example}.scala"
    result.replaceAllLiterally("\\", "/")
  }


  def buildPath(path:String): String =
  {
    path.replaceAllLiterally("\\", File.separator)
  }
}
