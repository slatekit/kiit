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
