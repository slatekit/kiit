package slatekit.examples.common

import slatekit.entities.repos.EntityRepoInMemoryWithLongId


class MovieRepository : EntityRepoInMemoryWithLongId<Movie>(Movie::class)