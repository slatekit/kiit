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
  * The result of command ( cmd ) that was run
  * @param name     : Name of the command
  * @param success  : Whether it was successful
  * @param message  : Message for success/error
  * @param result   : A resulting return value
  * @param totalMs  : Total time in milliseconds
  * @param start    : The start time of the command
  * @param end      : The end time of the command
  * @param runCount : The total time the command was run
  */
case class CmdResult(
                       name    : String          ,
                       success : Boolean         ,
                       message : String          ,
                       result  : Option[AnyRef]  ,
                       totalMs : Long            ,
                       start   : DateTime        ,
                       end     : DateTime        ,
                       runCount: Int
                    )
{
}
