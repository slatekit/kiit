/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.cloud.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.sqs.AmazonSQSClient
import slatekit.common.conf.Config

object AwsFuncs {

    /**
     * build sqs client from optional conf paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     * @return
     */
    fun sqs(path: String? = null, section: String? = null, region:String? = null): AmazonSQSClient {

        // Get credentials from either default location of specific config
        val credentials = creds(path, section)
        return sqs(credentials, region)
    }

    /**
     * build sqs client from optional conf paths
     * @return
     */
    fun sqs(credentials: AWSCredentials, region: String?): AmazonSQSClient {

        val regRaw = region(region ?: "")
        val reg = Region.getRegion(regRaw)
        val sqs = AmazonSQSClient(credentials)
        sqs.setRegion(reg)
        return sqs
    }

    /**
     * build s3 client from optional config paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     */
    fun s3(path: String? = null, section: String? = null, region:String? = null): AmazonS3Client {
        // Get credentials from either default location of specific config
        val credentials = creds(path, section)
        return s3(credentials, region)
    }

    /**
     * build s3 client from optional config paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     */
    fun s3(credentials: AWSCredentials, region: String?): AmazonS3Client {

        val regRaw = region(region ?: "")
        val reg = Region.getRegion(regRaw)
        val s3 = AmazonS3Client(credentials)
        s3.setRegion(reg)
        return s3
    }

    /**
     * Builds the aws credentials from optional conf paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     * @return
     */
    fun creds(path: String? = null, section: String? = null): AWSCredentials {
        val credentials = path?.let { filePath ->

            // Get api key from config path supplied
            val apiKey = Config.of(filePath).apiLogin(section ?: "aws")

            // Build creds from api key
            credsWithKeySecret(apiKey.key, apiKey.pass)
        } ?: creds()
        return credentials
    }


    /**
     * Gets the region from the name
     */
    fun region(name:String):Regions {
        return when(name){
            Regions.GovCloud.name       -> Regions.GovCloud
            Regions.US_EAST_1.name      -> Regions.US_EAST_1
            Regions.US_EAST_2.name      -> Regions.US_EAST_2
            Regions.US_WEST_1.name      -> Regions.US_WEST_1
            Regions.US_WEST_2.name      -> Regions.US_WEST_2
            Regions.EU_WEST_1.name      -> Regions.EU_WEST_1
            Regions.EU_WEST_2.name      -> Regions.EU_WEST_2
            Regions.EU_CENTRAL_1.name   -> Regions.EU_CENTRAL_1
            Regions.AP_SOUTH_1.name     -> Regions.AP_SOUTH_1
            Regions.AP_SOUTHEAST_1.name -> Regions.AP_SOUTHEAST_1
            Regions.AP_SOUTHEAST_2.name -> Regions.AP_SOUTHEAST_2
            Regions.AP_NORTHEAST_1.name -> Regions.AP_NORTHEAST_1
            Regions.AP_NORTHEAST_2.name -> Regions.AP_NORTHEAST_2
            Regions.SA_EAST_1.name      -> Regions.SA_EAST_1
            Regions.CN_NORTH_1.name     -> Regions.CN_NORTH_1
            Regions.CA_CENTRAL_1.name   -> Regions.CA_CENTRAL_1
            else                        -> Regions.US_EAST_1
        }
    }

    /**
     * Loads aws credentials from the default {user_dir}/.aws/credentials file
     *
     * @return
     */
    fun creds(): AWSCredentials = ProfileCredentialsProvider().credentials

    /**
     * Loads aws credentials using the key/secret supplied.
     *
     * @param key
     * @param secret
     * @return
     */
    fun credsWithKeySecret(key: String, secret: String): AWSCredentials = BasicAWSCredentials(key, secret)
}
