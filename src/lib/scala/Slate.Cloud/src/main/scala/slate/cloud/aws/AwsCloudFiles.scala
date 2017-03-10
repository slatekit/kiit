/**
  * <slate_header>
  * url: www.slatekit.com
  * git: www.github.com/code-helix/slatekit
  * org: www.codehelix.co
  * author: Kishore Reddy
  * copyright: 2016 CodeHelix Solutions Inc.
  * license: refer to website and/or github
  * about: A Scala utility library, tool-kit and server backend.
  * mantra: Simplicity above all else
  * </slate_header>
*/

package slate.cloud.aws

import java.io.File

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, ObjectMetadata}
import slate.common._

import slate.core.cloud._


class AwsCloudFiles(defaultFolder:String,
                    createDefaultFolder:Boolean,
                    path:Option[String] = None,
                    section:Option[String] = None)
  extends CloudFilesBase(defaultFolder,createDefaultFolder) with AwsSupport {

  private val SOURCE  = "aws:s3"
  private val _s3:AmazonS3Client = AwsFuncs.s3(path, section)


  /**
    * hook for any initialization
    */
  override def init():Unit = {
    if(_createDefaultFolder) {
      _s3.createBucket(_defaultFolder)
    }
  }


  /**
    * creates a root folder/bucket with the supplied name.
    *
    * @param rootFolder
    */
  override def createRootFolder(rootFolder:String):Unit =
  {
    if(!Strings.isNullOrEmpty(rootFolder) && rootFolder != _defaultFolder) {
      _s3.createBucket(rootFolder)
    }
  }


  /**
    * creates a file with the supplied folder name, file name, and content
    *
    * @param folder
    * @param name
    * @param content
    */
  override def create(folder:String, name:String, content:String):Result[String] =
  {
    put("create", folder, name, content)
  }


  /**
    * updates a file with the supplied folder name, file name, and content
    *
    * @param folder
    * @param name
    * @param content
    */
  override def update(folder:String, name:String, content:String): Result[String] =
  {
    put("update", folder, name, content)
  }


  /**
    * deletes a file with the supplied folder name, file name
    *
    * @param folder
    * @param name
    */
  override def delete(folder:String, name:String):Result[String] =
  {
    val fullName = getName(folder, name)
    executeResult[String](SOURCE, "delete", data = Some(fullName), call = () =>
    {
      _s3.deleteObject(_defaultFolder, fullName)
      fullName
    })
  }


  /**
    * gets the file specified by folder and name, as text content
    *
    * @param folder
    * @param name
    * @return
    */
  override def getAsText(folder:String, name:String):Result[String] =
  {
    val fullName = getName(folder, name)
    executeResult[String](SOURCE, "getAsText", data = Some(fullName), call = () =>
    {
      val obj = _s3.getObject(new GetObjectRequest(_defaultFolder, fullName))
      val content = toString(obj.getObjectContent())
      //val content = "simulating download of " + fullName
      content
    })
  }


  /**
    * downloads the file specified by folder and name to the local folder specified.
 *
    * @param folder
    * @param name
    * @param localFolder
    * @return
    */
  override def download(folder:String, name:String, localFolder:String): Result[String] =
  {
    val fullName = getName(folder, name)
    executeResult[String](SOURCE, "download", data = Some(fullName), call = () => {
      val content = getAsText(folder, name).getOrElse("")
      val finalFolder = Uris.interpret(localFolder).getOrElse("")
      val localFile = new File(finalFolder, fullName)
      val localFileName = localFile.getAbsolutePath
      Files.writeAllText(localFileName, content)
      localFileName
    })
  }


  /**
    * downloads the file specified by folder and name to the local folder specified.
    *
    * @param folder
    * @param name
    * @param filePath
    * @return
    */
  override def downloadToFile(folder:String, name:String, filePath:String): Result[String] =
  {
    val fullName = getName(folder, name)
    executeResult[String](SOURCE, "download", data = Some(fullName), call = () => {
      val content = getAsText(folder, name).getOrElse("")
      val localFile = Uris.interpret(filePath)
      val localFileName = localFile.getOrElse("")
      Files.writeAllText(localFileName, content)
      localFileName
    })
  }


  /**
    * uploads the file to the datasource using the supplied folder, filename, and content
    *
    * @param folder
    * @param name
    * @param content
    * @return
    */
  def put(action:String, folder:String, name:String, content:String): Result[String] =
  {
    // full name of the file is folder + name
    val fullName = getName(folder, name)

    executeResult[String](SOURCE, action, data = Some(fullName), call = () =>
    {
      _s3.putObject(_defaultFolder, fullName, toInputStream(content), new ObjectMetadata())
      fullName
    })
  }


  private def getName(folder:String, name:String):String =
  {
    // Case 1: no folder supplied, assume in root bucket
    if(Strings.isNullOrEmpty(folder))
      name

    // Case 2: folder == root folder
    else if(folder == _defaultFolder)
      name

    // Case 3: sub-folder
    else
      folder + "-" + name
  }
}
