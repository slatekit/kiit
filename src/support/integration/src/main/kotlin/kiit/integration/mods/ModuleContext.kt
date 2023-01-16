/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
  *  </kiit_header>
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