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

package kiit.integration.mods

import kiit.migrations.MigrationService

data class ModuleContext(

    /**
     * referece to service for performing checks/operations on module and status.
     */
    val service: ModService,

    /**
     * service to add / manage entities models.
     */
    val setup: MigrationService
)