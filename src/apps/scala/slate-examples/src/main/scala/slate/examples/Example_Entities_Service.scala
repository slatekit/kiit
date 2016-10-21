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


class Example_Entities_Service extends Cmd("types") with ResultSupportIn {

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
  class UserService(repo:EntityRepo[User]) extends EntityService[User]()
  {
    _repo = repo
  }
  //</doc:setup>


  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // The Service layer initialized with an repository .
    // Purpose of the service layer is to:
    //
    // 1. Delegate to underlying repository for CRUD operations after applying any business logic.
    // 2. Provide a layer to insert business logic during other operations.
    // 3. Has some( not all ) methods that match the repository CRUD, Find, Delete methods
    // 4. Subclassed to perform more complex business logic that may still involve using the repo.
    //
    val service = new UserService(repoMySql)

    // CASE 1: Create 3-4 users for showing use-cases
    service.create(new User("user", "01"))
    service.create(new User("user", "02"))
    service.create(new User("user", "03"))
    service.create(new User("user", "04"))

    // CASE 2: Get by id
    printOne( service.get( 2 ) )

    // CASE 3: Update
    val item2 = service.get( 2 )
    item2.get.firstName = "user_two"
    service.update(item2.get)

    // CASE 4: Get all
    printAll( service.getAll() )

    // CASE 5: Get recent users ( 03, 04 )
    printAll( service.recent(2) )

    // CASE 6: Get oldest users ( 01, 02 )
    printAll( service.oldest(2) )

    // CASE 7: Get first one ( oldest - 01 )
    printOne( service.first() )

    // CASE 8: Get last one ( recent - 04 )
    printOne( service.last() )

    // CASE 9: Delete by id
    service.delete( 4 )

    // CASE 10: Get total ( 4 )
    println( service.count() )

    //</doc:examples>
    ok()
  }


  def printOne(user:User):Unit =
  {
    println("User: " + user.firstName + ", " + user.lastName)
  }


  def printOne(user:Option[User]):Unit =
  {
    println("User: " + user.get.firstName + ", " + user.get.lastName)
  }


  def printAll(users:List[User]):Unit =
  {
    for(user <- users)
      printOne(user)
  }
}
