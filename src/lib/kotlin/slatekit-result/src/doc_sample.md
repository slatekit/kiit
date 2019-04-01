# Result&lt;T,E> Sample
This is a small example of using Result<T,E> in a simple user registration 
workflow. Kotlin code and idioms are kept 
to a minimum to focus on the usage of Result<T,E>. 


## Setup
Assume 2 simple models for User and HttpResponse( e.g Kotlin Ktor, Java Spark/Jersey )
```kotlin        
  // Simple User model for sample purposes
  data class User(val id:Long, val userName:String, val email:String)
  
  
  
  // Mimic an Http Response for sample purposes
  data class HttpResponse(val code:Int, val content:Any)
```

## Utils
Extension utility method to convert Result<T,E> to a compatible HttpResponse ( used below )
```kotlin        
  import slatekit.results.*
  
  // Extension method on Result<T,E> to convert to compatible HttpResponse
  fun <T, E> Result<T, E>.toHttpResponse():HttpResponse {
      // Convert to http code
      val code:Int = StatusCodes.toHttp(this.status).first
  
      // Serialize ( toString for sample app - but use some library like Jackson )
      val content = when(this){
          is Success -> this.value.toString()
          is Failure -> this.error.toString()
      }
      return HttpResponse(code, content)
  }
```

## API
Result<T,E> can keep API methods as simple 1 liners. 
**Accurately** model Successes / Failures in your **Service** layer and have them properly
propagated to your API layer and converted to HttpResponses.

```kotlin
  class UserApi(private val service:UserService) {
  
      /**
       * Your controller method now becomes just 1 line.
       * Result<T,E> keeps this clean by ACCURATELY representing both
       * successes and failures and making them easily convertible to HTTP
       */
      fun register(name:String, email:String):HttpResponse  {
          return service.register(User(0, name, email) ).toHttpResponse()
      }
  }
```

## Service
Model your successes / failures as precisely or generically as your want.
```kotlin
  import slatekit.results.*
  import slatekit.results.builders.Results.denied
  import slatekit.results.builders.Results.invalid
  import slatekit.results.builders.Results.success
  import slatekit.results.builders.Results.errored
  
  /**
   * Sample Service to handle User Registration and management
   */
  class UserService(private val repo:UserRepo) {
  
      /**
       * Sample method to register a User.
       * This highlights different ways of building accurate failures
       */
      fun register(user:User):Result<User, Err> {
          // Case 1: Create using [Failure] branch of Result
          // [Err] is an empty marker interface so you can create errors of any type.
          // A convenient Err.of method is supplied to build an [Err] from a string.
          if(user.id > 0 )
              return Failure(Err.of("already registered"))
  
          // Case 2: Create using builder methods in [Results]
          // Same as Failure(Err.of("user name not supplied"), StatusCodes.INVALID)
          if(user.userName.isEmpty())
              return invalid("user name not supplied")
  
          // Case 3: Create using builder + explicit status code
          // Many of the builder methods have optional parameters
          if(repo.exists(user.email))
              return errored("duplicate user name", StatusCodes.CONFLICT)
  
          // Case 4: Sample rule: prevent registration via API of special emails.
          // This doesn't allocate any new object for [Err] or [Status]
          // Same as errored(StatusCodes.UNAUTHORIZED, StatusCodes.UNAUTHORIZED)
          if (user.email.toLowerCase().contains("@justice-league.com"))
              return denied()
  
          val userWithId = repo.create(user)
          // or success(userWithId) where status code = SUCCESS
          return success(userWithId, StatusCodes.CREATED)
      }
  
  
      /**
       * Use the Outcome<T> alias for Result<T,Err>
       */
      fun updateName(id:Long, name:String):Outcome<User> {
  
          val user = repo.getById(id)
          if( user == null )
              return invalid("User with id : $id not found")
  
          // Sample rule: prevent name update via API if justice league.
          // Could also return errored(StatusCodes.UNAUTHORIZED)
          if (user.email.toLowerCase().contains("@justice-league.com"))
              return denied()
  
          repo.update(user.copy(userName = name))
          // or success(userWithId) where status code = SUCCESS
          return success(user, StatusCodes.UPDATED)
      }
  }
```

## Repo
In-memory Repository to mimic a database for this sample
```kotlin
  import java.util.concurrent.atomic.AtomicLong
  
  /**
   * User Repo ( mimic a database for sample purposes )
   */
  class UserRepo {
      // Mimic database auto-inc key
      private val nextId = AtomicLong(0)
  
      // Mimic a database for sample purposes
      private val users = mutableListOf(
          User(nextId.incrementAndGet(), "batman", "batman@justice-league.com"),
          User(nextId.incrementAndGet(), "superman", "superman@justice-league.com"),
          User(nextId.incrementAndGet(), "wonderwoman", "wonderwoman@justice-league.com")
      )
  
      fun exists(email:String):Boolean = users.firstOrNull { it.email == email } != null
  
      fun getById(id:Long):User? = users.firstOrNull { it.id == id }
  
      fun create(user:User):User {
          val id = nextId.incrementAndGet()
          val userWithId = user.copy(id = id)
          users.add(userWithId)
          return userWithId
      }
  
      fun update(user:User) {
          val index = users.indexOfLast { it.email == user.email }
          users.removeAt(index)
          users.add(user)
      }
  }
```