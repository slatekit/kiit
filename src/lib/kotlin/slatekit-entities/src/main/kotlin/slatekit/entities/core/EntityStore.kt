/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.entities.core

/**
 * Interface for operations that do not rely on knowing the id or entity type
 */
interface EntityStore {

    fun name(): String

    /**
     * gets the total number of entities in the datastore
     * @return
     */
    fun count(): Long

    /**
     * determines if there are any entities in the datastore
     * @return
     */
    fun any(): Boolean = count() > 0

    /**
     * whether this is an empty dataset
     */
    fun isEmpty(): Boolean = !any()
}
