package slatekit.examples.common

import slatekit.entities.repos.EntityRepoInMemory
import kotlin.reflect.KClass


class MovieRepository() : EntityRepoInMemory<Movie>(Movie::class) {
}
