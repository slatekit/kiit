package slatekit.docs

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.apis.security.AuthModes
import slatekit.apis.security.Protocols
import slatekit.apis.security.Verbs
import slatekit.integration.common.AppEntContext
import slatekit.results.Try

@Api(area = "slate", name = "docs", desc= "help doc generator",
        auth = AuthModes.apiKey, roles = "admin", verb = Verbs.auto, protocol = Protocols.all)
class DocApi(val context: AppEntContext) {

    /**
     * Generate the markdown docs for the components
     * @param root    : C:/Dev/github/blend-server
     * @param template: scripts/doc/doc_template_kotlin.md
     * @param output  : src/site/slatekit
     * slate.docs.generateAll -root="C:/Dev/github/blend-server/" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit"
     * slate.docs.generateAll -root="/Users/kishorereddy/git/slatekit" -template="scripts/doc/doc_template_kotlin.md" -output="src/site/slatekit"
     */
    @ApiAction(desc= "generates the markdown docs")
    fun generateAll(root:String, template:String, output:String): Try<String> {
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
    @ApiAction(name = "", desc= "generates the markdown docs")
    fun generateComponent(root:String, template:String, output:String, name:String): Try<String> {
        val doc = DocService(root, output, template)
        val result = doc.process(name)
        return result
    }
}
