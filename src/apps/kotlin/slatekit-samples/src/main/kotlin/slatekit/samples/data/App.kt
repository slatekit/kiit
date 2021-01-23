package slatekit.samples.data

import slatekit.app.App
import slatekit.app.AppOptions
import slatekit.common.args.ArgsSchema
import slatekit.common.utils.B64Java8
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DbConString
import slatekit.common.data.Vendor
import slatekit.common.info.About
import slatekit.connectors.entities.AppEntContext
import slatekit.data.FullRepo
import slatekit.data.support.InMemoryRepo
import slatekit.data.support.LongIdGenerator
import slatekit.db.Db
import slatekit.samples.common.models.Movie


/**
 * Slate Kit Application template
 * This provides support for command line args, environment selection, confs, life-cycle methods and help usage
 * @see https://www.slatekit.com/arch/app/
 */
class App(ctx: AppEntContext) : App<AppEntContext>(ctx, AppOptions(showWelcome = true)) {

    companion object {

        // setup the command line arguments.
        // NOTE:
        // 1. These values can can be setup in the env.conf file
        // 2. If supplied on command line, they override the values in .conf file
        // 3. If any of these are required and not supplied, then an error is display and program exists
        // 4. Help text can be easily built from this schema.
        val schema = ArgsSchema()
                .text("","env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                .text("","log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")


        /**
         * Default static info about the app.
         * This can be overriden in your env.conf file
         */
        val about = About(
                company = "slatekit",
                area = "samples",
                name = "app",
                desc = "Sample Console Application with command line args, environments, logs, and help docs",
                region = "",
                url = "myapp.url",
                contact = "",
                tags = "app",
                examples = ""
        )

        /**
         * Encryption support
         */
        val encryptor = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042", B64Java8)
    }


    override suspend fun init() {
        //val con = DbConString(Vendor.MySql, "jdbc:mysql://localhost/slatekit", "user", "banksy1020")
        //val cons = Connections.of(con)
        //val ent = Entities({c -> Db(con)}, cons)
        //ent.register<Long, Movie>(Long::class, Movie::class, )
        val con = DbConString(Vendor.MySql, "jdbc:mysql://localhost/slatekit", "user", "banksy1020")
        val db = Db(con)
        val repo:FullRepo<Long, Movie> = InMemoryRepo(Movie::id.name, "movies", { m -> m.id }, LongIdGenerator())

        // Ordered
        repo.first()
        repo.last()
        repo.oldest(2)
        repo.recent(2)

        // Size
        repo.count()
        repo.any()
        repo.isEmpty()

        // CRUD
        val movie = Movie.sample()
        val id = repo.create(movie)
        val movie2 = repo.getById(id)
        movie2?.let { repo.update(movie2) }
        repo.delete(movie2)
        repo.save(movie)

        // Misc
        val all = repo.getAll()
        repo.deleteAll()
        repo.save(movie2)

        // Meta
        val movieId = repo.identity(movie)
        val moveHasId = repo.isPersisted(movie)

        println("initializing")
    }


    override suspend fun exec(): Any? {
        println("executing")
        println("Your work should be done here...")
        return OK
    }


    override suspend fun done(result:Any?) {
        println("ending")
    }
}