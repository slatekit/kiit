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

import TODO.IMPROVE
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
     * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     * @return
     */
    fun sqs(path: String? = null, section: String? = null): AmazonSQSClient {

        // Get credentials from either default location of specific config
        val credentials = creds(path, section)
        return sqs(credentials)
    }


    /**
     * build sqs client from optional conf paths
     * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     * @return
     */
    fun sqs(credentials:AWSCredentials): AmazonSQSClient {

        IMPROVE("AWS", "Allow customization of region")
        val usWest2 = Region.getRegion(Regions.US_WEST_2)
        val sqs = AmazonSQSClient(credentials)
        sqs.setRegion(usWest2)
        return sqs
    }


    /**
     * build s3 client from optional config paths
     * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     */
    fun s3(path: String? = null, section: String? = null): AmazonS3Client {
        // Get credentials from either default location of specific config
        val credentials = creds(path, section)
        return s3(credentials)
    }


    /**
     * build s3 client from optional config paths
     * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     */
    fun s3(credentials:AWSCredentials): AmazonS3Client {
        IMPROVE("AWS", "Allow customization of region")
        val usWest2 = Region.getRegion(Regions.US_WEST_2)
        val s3 = AmazonS3Client(credentials)
        s3.setRegion(usWest2)
        return s3
    }


    /**
     * Builds the aws credentials from optional conf paths
     * @param path    : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     * @return
     */
    fun creds(path: String? = null, section: String? = null): AWSCredentials {
        val credentials = path?.let { filePath ->

            // Get api key from config path supplied
            val apiKey = Config(filePath).apiLogin(section ?: "aws")

            // Build creds from api key
            credsWithKeySecret(apiKey.key, apiKey.pass)
        } ?: creds()
        return credentials
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
