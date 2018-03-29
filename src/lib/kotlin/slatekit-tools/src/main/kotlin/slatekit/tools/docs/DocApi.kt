package slatekit.tools.docs

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.Result
import slatekit.core.common.AppContext
import slatekit.integration.common.AppEntContext

@Api(area = "sys", name = "docs", desc= "help doc generator",
        roles= "@admin", auth = "app", verb = "post", protocol = "*")
class DocApi(val context: AppEntContext) {

    /**
     * Generate the markdown docs for the components
     * @param root    : C:/Dev/github/blend-server
     * @param template: scripts/doc/doc_template_kotlin.md
     * @param output  : src/site/slatekit
     * sys.docs.generateAll -root="C:/Dev/github/blend-server/" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit"
     * sys.docs.generateAll -root="/Users/kishorereddy/git/slatekit" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit"
     */
    @ApiAction(name = "", desc= "generates the markdown docs", roles= "", verb = "@parent", protocol = "@parent")
    fun generateAll(root:String, template:String, output:String): Result<String> {
        val doc = DocService(root, output, template)
        val result = doc.process()
        return result
    }

    
    /**
     * Generate the markdown docs for the components
     * @param root    : C:/Dev/github/blend-server
     * @param template: scripts/doc/doc_template_kotlin.md
     * @param output  : src/site/slatekit
     * @param name    : name of the component
     * sys.docs.generateComponent -root="C:/Dev/github/blend-server/" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit" -name="Api"
     */


    @ApiAction(name = "", desc= "generates the markdown docs", roles= "", verb = "@parent", protocol = "@parent")
    fun generateComponent(root:String, template:String, output:String, name:String): Result<String> {
        val doc = DocService(root, output, template)
        val result = doc.process(name)
        return result
    }
}
