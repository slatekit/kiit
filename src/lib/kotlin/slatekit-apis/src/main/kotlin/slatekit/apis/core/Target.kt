package slatekit.apis.core

import slatekit.apis.Verb
import slatekit.common.Source

data class Target(val api: Api, val action: Action, val instance: Any) {

    /**
     * Determines if the action matches the expected Verb ( e.g. Verb.Post )
     * NOTES: The action.verb could be:
     * 1. empty      -> so get its parent
     * 2. @parent    -> indicating reference to its parent
     * 3. auto       -> via annotation, but converted to Get/Post/Put/Patch/Delete/ etc during loading
     * 4. some value -> indicating exact requirement
     */
    fun isMatchingVerb(expected: Verb):Boolean {
        // E.g. action could be reference to parent e.g. "@parent"
        val actualVerb = action.verb.orElse(api.verb)
        return actualVerb == expected
    }

    /**
     * Determines if the action matches the expected Protocol ( e.g. Protocol.CLI / Web )
     * NOTES: The action.protocol could be:
     * 1. empty      -> so get its parent
     * 2. @parent    -> indicating reference to its parent
     * 3. *          -> via annotation, indicating any protocol
     * 4. some value -> indicating exact requirement
     */
    fun isMatchingProtocol(expected: Source):Boolean {
        // E.g. action could be reference to parent e.g. "@parent"
        val actualProtocols = when {
            // 1.
            action.protocols.isEmpty -> api.protocols
            action.protocols.isParentReference -> api.protocols
            else -> action.protocols
        }
        return actualProtocols.isMatchOrAll(expected)
    }
}
