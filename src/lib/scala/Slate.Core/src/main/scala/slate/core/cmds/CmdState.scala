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

import slate.common.DateTime


/**
  *
  * @param name        : Name of the command
  * @param lastRuntime : Last time the command was run
  * @param hasRun      : Whether command has run at least once
  * @param runCount    : The total times the command was run
  * @param errorCount  : The total errors
  * @param lastResult  : The last result
  */
class CmdState(
                 var name       : String,
                 var lastRuntime: DateTime,
                 var hasRun     : Boolean,
                 var runCount   : Int,
                 var errorCount : Int,
                 var lastResult : CmdResult
              )
{

  /**
   * creates a copy of the current state.
   * NOTE: This is a not a case class because the variables representing the state are mutable
   * by the owner of the state ( The command )
   *
   * The state is never available to a caller ( it is copied and sent to the caller )
   * @return
   */
  def copy(): CmdState =
  {
    val state = new CmdState(name, lastRuntime, hasRun, runCount, errorCount, lastResult)
    state
  }
}
