package slatekit.orm.services

import slatekit.orm.core.*

object ActiveDB {

    private lateinit var _entities:Entities


    fun configure(entities:Entities) {
        _entities = entities
    }


    val entities:Entities by lazy { _entities }
}