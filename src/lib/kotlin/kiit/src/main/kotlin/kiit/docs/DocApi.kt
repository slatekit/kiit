package kiit.docs

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.context.Context
import slatekit.common.Sources
import slatekit.results.Try

@Api(area = "slatekit", name = "docs", desc= "help doc generator",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.CLI])
class DocApi(val context: Context) {

    /**
     * Generate the markdown docs for the components
     * @param root    : /Users/kishore.reddy/dev/tmp/slatekit/slatekit
     * @param template: scripts/doc/doc_template_kotlin.md
     * @param output  : src/site/slatekit
     * slate.docs.generateAll -root="C:/Dev/github/blend-server/" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit"
     * slate.docs.generateAll -root="/Users/kishore.reddy/dev/tmp/slatekit/slatekit" -template="scripts/doc/doc_template_utils.md" -output="src/site/slatekit"
     */
    @Action(desc= "generates the markdown docs")
    fun generateAll(root:String, template:String, output:String): Try<String> {
        val doc = DocService(root, output, template)
        val result = doc.process()
        return result
    }

    
    /**
     * Generate the markdown docs for the components
     * @param root    : /Users/kishore.reddy/dev/tmp/slatekit/slatekit
     * @param template: scripts/doc/doc_template_kotlin.md
     * @param output  : src/site/slatekit
     * @param name    : name of the component
     * sys.docs.generateComponent -root="C:/Dev/github/blend-server/" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit" -name="Api"
     */
    @Action(name = "", desc= "generates the markdown docs")
    fun generateComponent(root:String, template:String, output:String, name:String): Try<String> {
        val doc = DocService(root, output, template)
        val result = doc.processComponent(name)
        return result
    }
}
