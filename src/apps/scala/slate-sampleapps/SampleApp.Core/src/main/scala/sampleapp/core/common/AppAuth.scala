/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package sampleapp.core.common

import slate.common.{Strings, Result, Files, ApiKey}
import slate.common.results.ResultSupportIn
import slate.core.apis.{Request, ApiAuth}
import slate.core.common.Conf

/**
  * Sample custom auth provider for your application.
  *
  * @param mode        : "test-mode"     - for demo purposes to return hard-coded roles
  * @param appDir      : "mycompany"     - app directory in user folder to store configs e.g. c:/users/jdoe/mycompany
  * @param user        : "john.doe"      - hard-coded user name for demo purposes
  * @param selectedKey : "dev,ops,admin" - hard-coded user roles for demo purposes
  * @param keys        : the list of api keys when using api key based authorization.
  */
class AppAuth (val mode:String, val appDir:String,
               val user:String, val selectedKey:ApiKey, keys:Option[List[ApiKey]])
  extends ApiAuth(keys, None)
with ResultSupportIn
{

  /**
    * Override and implement this method if you want to completely handle authorization your self.
    * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    * @param mode        : The auth-mode of the api ( refer to auth-mode for protocolo independent APIs )
    * @param actionRoles : The role setup on the API action
    * @param parentRoles : The role setup on the API itself
    *
    * IMPORTANT:
    * 1. all you need to do to implement your authorization is to implement the getUserRoles below
    *
    * NOTES:
    * 1. see base class implementation for details.
    * 2. the auth modes on the apis can be "app-roles" or "key-roles" ( api-keys )
    * 3. the base class properly delegates handling the auth modes.
    * @return
    */
  override def isAuthorized(cmd:Request, mode:String, actionRoles:String, parentRoles:String)
  :Result[Boolean] = {
      super.isAuthorized(cmd, mode, actionRoles, parentRoles)
  }


  /**
    * Called by system to handle authorization for an API action with auth mode = "key-roles"
    * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    * @param actionRoles : The role setup on the API action
    * @param parentRoles : The role setup on the API itself
    * @return
    */
  override def isKeyRoleValid(cmd:Request, actionRoles:String, parentRoles:String):Result[Boolean] = {

    super.isKeyRoleValid(cmd, actionRoles, parentRoles)
  }


  /**
    * Called by system to handle authorization for an API action with auth mode = "app-roles"
    * @param cmd         : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    * @param actionRoles : The role setup on the API action
    * @param parentRoles : The role setup on the API itself
    * @return
    */
  override def isAppRoleValid(cmd:Request, actionRoles:String, parentRoles:String): Result[Boolean] = {

    super.isAppRoleValid(cmd, actionRoles, parentRoles)
  }

  /**
    * Called by system to get the users roles from API request (ApiCmd)
    * @param cmd   : The protocol independent API request. e.g. http://abc.com/api/myapp/users/activate
    *
    * IMPORTANT
    * 1. for sample / demo purposes this returns the hard-coded user/roles if mode = 'test-mode'
    *
    * NOTES:
    * 1. you need to implement this method to provide the system with the roles from request.
    * 2. the api cmd could be a http request, in which case you can access the headers via cmd.opts.
    * 3. you have freedom to implement any authorization scheme you want.
    *
    * @return
    */
  override protected def getUserRoles(cmd:Request):String = {

    // CASE 1: sample demo
    if(mode == "test-mode"){
      return selectedKey.roles
    }

    // CASE 2: When running a console/cli app
    // You get get the user/roles from some local encrypted file saved on the user directory
    if(mode == "user-dir"){
      val path = Files.loadUserAppFile(appDir, "login.txt").getAbsolutePath
      val conf = new Conf(Some(path))
      val roles = conf.getString("roles")
      return roles
    }

    // CASE 3: Running a web server
    // Get the roles from the opts abstraction member representing http headers
    if(mode == "header"){
      val headers = cmd.opts
      val roles = headers.map[String]( ops => ops.getString("auth.roles")).getOrElse("")

      // NOTE: Handle encryption etc. for production
      // This is just here for sample purpose to show getting auth/user info from
      // protocol independent APIs ( see slatekit.com for more info ).
      return roles
    }
    ""
  }
}
