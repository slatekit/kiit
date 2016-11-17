/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
*/

package slate.cloud.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, ObjectMetadata, PutObjectRequest}
import slate.common.Strings
import slate.common.Files;

import slate.core.cloud._


class AwsCloudFiles(defaultFolder:String, createDefaultFolder:Boolean)
  extends CloudFilesBase(defaultFolder,createDefaultFolder) with AwsSupport {

  private val SOURCE  = "aws:s3"
  private var _s3:AmazonS3Client = null


  def connectWith(key:String, password:String):Unit =
  {
    execute(SOURCE, "connect", rethrow = true, data = None, call = () =>
    {
      val creds = credentials(key, password)
      connect(creds)
    })
  }


  override def connect(args:Any):Unit =
  {
    execute(SOURCE, "connect", rethrow = true, data = Some(args), call = () =>
    {
      val creds = credentialsFromLogon()
      connect(creds)
    })
  }


  override def create(folder:String, name:String, content:String):Unit =
  {
    put("create", folder, name, content)
  }


  override def update(folder:String, name:String, content:String): Unit =
  {
    put("update", folder, name, content)
  }


  override def delete(folder:String, name:String):Unit =
  {
    val fullName = getName(folder, name)
    execute(SOURCE, "delete", data = Some(fullName), call = () =>
    {
      _s3.deleteObject(_defaultFolder, fullName)
    })
  }


  override def createRootFolder(rootFolder:String):Unit =
  {
    if(Strings.isNullOrEmpty(rootFolder)) return
    if(rootFolder == _defaultFolder) return
    _s3.createBucket(rootFolder)
  }


  override def getAsText(folder:String, name:String):String =
  {
    var content = ""
    val fullName = getName(folder, name)
    execute(SOURCE, "getAsText", data = Some(fullName), call = () =>
    {
      val obj = _s3.getObject(new GetObjectRequest(_defaultFolder, fullName))
      content = toString(obj.getObjectContent())
    })
    content
  }


  override def download(folder:String, name:String, localFolder:String): Unit =
  {
    val fullName = getName(folder, name)
    execute(SOURCE, "download", data = Some(fullName), call = () => {
      val content = getAsText(folder, name)
      Files.writeAllText(localFolder + "/" + fullName, content)
    })
  }


  def put(action:String, folder:String, name:String, content:String): Unit =
  {
    // full name of the file is folder + name
    val fullName = getName(folder, name)

    execute(SOURCE, action, data = Some(fullName), call = () =>
    {
      _s3.putObject(_defaultFolder, fullName, toInputStream(content), new ObjectMetadata())
    })
  }


  protected def connect(credentials:AWSCredentials):Unit =
  {
      val usWest2 = Region.getRegion(Regions.US_WEST_2)
      _s3 = new AmazonS3Client(credentials)
      _s3.setRegion(usWest2)
      if(_createDefaultFolder) {
        _s3.createBucket(_defaultFolder)
      }
  }


  private def getName(folder:String, name:String):String =
  {
    // Case 1: no folder supplied, assume in root bucket
    if(Strings.isNullOrEmpty(folder)) return name

    // Case 2: folder == root folder
    if(folder == _defaultFolder) return name

    // Case 3: sub-folder
    folder + "-" + name
  }
}
