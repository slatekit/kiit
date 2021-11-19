package slatekit.examples.common

import slatekit.entities.repos.InMemoryRepoWithLongId


class MovieRepository : InMemoryRepoWithLongId<Movie>(Movie::class)