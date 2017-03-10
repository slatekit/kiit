/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.cloud.aws

import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentials}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.sqs.AmazonSQSClient
import slate.core.common.Conf

object AwsFuncs {

  /**
    * build sqs client from optional conf paths
    * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
    * @param section : Section name in config containing slate kit ApiCredentials
    * @return
    */
  def sqs(path:Option[String] = None, section:Option[String] = None):AmazonSQSClient = {

    // Get credentials from either default location of specific config
    val credentials = creds(path, section)

    // TODO: Default to west, look at determining this from conf
    val usWest2 = Region.getRegion(Regions.US_WEST_2)
    val sqs = new AmazonSQSClient(credentials)
    sqs.setRegion(usWest2)
    sqs
  }


  /**
    * build s3 client from optional config paths
    * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
    * @param section : Section name in config containing slate kit ApiCredentials
    */
  def s3(path:Option[String] = None, section:Option[String] = None):AmazonS3Client = {
    // Get credentials from either default location of specific config
    val credentials = creds(path, section)

    // TODO: Default to west, look at determining this from conf
    val usWest2 = Region.getRegion(Regions.US_WEST_2)
    val s3 = new AmazonS3Client(credentials)
    s3.setRegion(usWest2)
    s3
  }


  /**
    * Builds the aws credentials from optional conf paths
    * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
    * @param section : Section name in config containing slate kit ApiCredentials
    * @return
    */
  def creds(path:Option[String] = None, section:Option[String] = None):AWSCredentials = {
    val credentials = path.fold(creds())( filePath => {

      // Get api key from config path supplied
      val apiKey = new Conf(path).apiKey( section.getOrElse("s3"))

      // Ensure
      require(apiKey.isDefined, "AWS S3 config file not present")

      // Build creds from api key
      val key = apiKey.get
      creds(key.key, key.pass)
    })
    credentials
  }


  /**
    * Loads aws credentials from the default {user_dir}/.aws/credentials file
    *
    * @return
    */
  def creds():AWSCredentials = new ProfileCredentialsProvider().getCredentials


  /**
    * Loads aws credentials using the key/secret supplied.
    *
    * @param key
    * @param secret
    * @return
    */
  def creds(key:String, secret:String): AWSCredentials = new BasicAWSCredentials(key, secret)
}
