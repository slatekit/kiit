/**
  <header>
    <author>Kishore Reddy</author>
    <url>https://github.com/kishorereddy/scala-slate</url>
    <copyright>2015 Kishore Reddy</copyright>
    <license>https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md</license>
    <desc>a scala micro-framework</desc>
    <usage>Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    </usage>
  </header>
  */
package slate.core.cmds

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

import slate.common.{NoResult, FailureResult, SuccessResult, DateTime}
import slate.common.DateTime._
import slate.common.Funcs._


/**
  * Light-weight implementation of a command pattern with some additional features.
  * This allows a function call/method to be wrapped inside a command that can return:
  *
  * 1. name of the command
  * 2. success/failure of the command
  * 3. message of success/failure
  * 4. result of the command
  * 5. time started
  * 6. time ended
  * 7. duration of the execution
  *
  * @param name
  */
class Cmd(val name: String) {

  /**
   * execute this command with optional arguments
   * @param args
   * @return
   */
  def execute(args:Array[Any]): CmdResult =
  {
    val resultTimed = attemptTimed( () => {
      executeInternal(args)
    })
    val result = resultTimed.result
    val data = resultTimed.result match {
      case s:SuccessResult[AnyRef] => Option(s.get)
      case f:FailureResult[AnyRef] => None
      case NoResult                => None
      case _                       => None
    }
    new CmdResult(name, result.success, result.msg.getOrElse(""), data, 0, resultTimed.start, resultTimed.end, 1)
  }


  /**
   * executes the command, this should be overridden in sub-classes
   * @param args
   * @return
   */
  protected def executeInternal(args: Any) : AnyRef = "not-implemented"
}
