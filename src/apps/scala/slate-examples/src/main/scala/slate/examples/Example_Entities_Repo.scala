/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slate.examples

//<doc:import_required>
import slate.common.databases.{DbConString, Db}
import slate.common.results.ResultSupportIn
import slate.entities.core._
import slate.entities.repos._
import scala.reflect.runtime.universe._
//</doc:import_required>

//<doc:import_examples>
import slate.common.{Field, DateTime, Result}
import slate.core.cmds.Cmd
//</doc:import_examples>

class Example_Entities_Repo extends Cmd("types") with ResultSupportIn {

  //<doc:setup>
  // Example entity that the Repo with manage via CRUD operations
  class User extends Entity with IEntityUnique {

    def this(first:String, last:String)= {
      this()
      firstName = first
      lastName = last
    }

    @Field("", true, 20)
    var firstName  = ""

    @Field("", true, 20)
    var lastName  = ""

    @Field("", true, 50)
    var email  = ""

    @Field("", true, 50)
    var lastLogin  = DateTime.now()

    @Field("", true, -1)
    var isEmailVerified  = false

    @Field("", true, -1)
    var status  = 0

    @Field("", true, 50)
    var uniqueId: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  }

  // CASE 1: In-memory ( non-persisted ) repository has limited functionality
  // but is very useful for rapid prototyping of a data model when you are trying to
  // figure out what fields/properties should exist on the model
  val repo = new EntityRepoInMemory[User](typeOf[User])

  // CASE 2: My-sql ( persisted ) repository can be easily setup
  // More examples of database setup/entity registration available in Setup/Registration docs.
  // 1. First setup the database
  val db = new Db(new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/user_db", "root", "abcdefghi"))

  // 2. Setup the mapper
  val mapper = new EntityMapper(null)
  mapper.loadSchema(new User(), typeOf[User])

  // 3. Now create the repo with database and mapper
  val repoMySql = new EntityRepoMySql[User](typeOf[User])
  repoMySql.setMapper(mapper)
  repoMySql.setDb(db)

  // CASE 3: You can also extend from EntityRepositoryMySql
  class UserRepository() extends EntityRepoMySql[User](typeOf[User])
  {
  }
  val userRepo = new UserRepository()
  userRepo.setMapper(mapper)
  userRepo.setDb(db)
  //</doc:setup>


  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // CASE 1: Create 3-4 users for showing use-cases
    repo.create(new User("john", "doe-01"))
    repo.create(new User("jane", "doe-02"))
    repo.create(new User("john", "doe-03"))
    repo.create(new User("jane", "doe-04"))

    // CASE 2: Get by id
    printOne( repo.get( 2 ) )

    // CASE 3: Update
    val item2 = repo.get( 2 )
    item2.get.firstName = "user_two"
    repo.update(item2.get)

    // CASE 4: Get all
    printAll( repo.getAll() )

    // CASE 5: Get recent users ( 03, 04 )
    printAll( repo.recent(2) )

    // CASE 6: Get oldest users ( 01, 02 )
    printAll( repo.oldest(2) )

    // CASE 7: Get first one ( oldest - 01 )
    printOne( repo.first() )

    // CASE 8: Get last one ( recent - 04 )
    printOne( repo.last() )

    // CASE 9: Delete by id
    repo.delete( 4 )

    // CASE 10: Get total ( 4 )
    println( repo.count() )

    //</doc:examples>
    ok()
  }


  def printOne(user:Option[User]):Unit =
  {
    if(user.isEmpty) return
    val u = user.getOrElse(new User())
    println("User: " + u.firstName + ", " + u.lastName)
  }


  def printAll(users:List[User]):Unit =
  {
    //for(user <- users)
      //printOne(user)
  }
}
