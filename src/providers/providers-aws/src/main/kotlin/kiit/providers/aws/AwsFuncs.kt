/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 *
 *  </kiit_header>
 */

package kiit.providers.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.sqs.AmazonSQSClient
import kiit.common.conf.Config
import kiit.results.Try
import kiit.results.builders.Tries


/**
 * Gets the region from the name
 */
fun String?.toRegion(): Regions? {
    // Use .getName instead of name as .name uses the Enum.name instead of the "us-east-1"
    return when (this) {
        null -> null
        Regions.GovCloud.getName() -> Regions.GovCloud
        Regions.US_EAST_1.getName() -> Regions.US_EAST_1
        Regions.US_EAST_2.getName() -> Regions.US_EAST_2
        Regions.US_WEST_1.getName() -> Regions.US_WEST_1
        Regions.US_WEST_2.getName() -> Regions.US_WEST_2
        Regions.EU_WEST_1.getName() -> Regions.EU_WEST_1
        Regions.EU_WEST_2.getName() -> Regions.EU_WEST_2
        Regions.EU_CENTRAL_1.getName() -> Regions.EU_CENTRAL_1
        Regions.AP_SOUTH_1.getName() -> Regions.AP_SOUTH_1
        Regions.AP_SOUTHEAST_1.getName() -> Regions.AP_SOUTHEAST_1
        Regions.AP_SOUTHEAST_2.getName() -> Regions.AP_SOUTHEAST_2
        Regions.AP_NORTHEAST_1.getName() -> Regions.AP_NORTHEAST_1
        Regions.AP_NORTHEAST_2.getName() -> Regions.AP_NORTHEAST_2
        Regions.SA_EAST_1.getName() -> Regions.SA_EAST_1
        Regions.CN_NORTH_1.getName() -> Regions.CN_NORTH_1
        Regions.CA_CENTRAL_1.getName() -> Regions.CA_CENTRAL_1
        else -> null
    }
}


fun <T> build(region: String, op: (Regions) -> T): Try<T> {
    val regions = region.toRegion()
    return when (regions) {
        null -> Tries.invalid("Invalid region: $region")
        else -> Tries.of { op(regions) }
    }
}


/**
 * See: https://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html
 */
fun formatBucket(name: String): String {
    return name.trim().replace("_", "-").toLowerCase()
}

object AwsFuncs {

    /**
     * build sqs client from optional conf paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     * @return
     */
    fun sqs(cls: Class<*>, path: String? = null, section: String? = null, region: Regions?): AmazonSQSClient {

        // Get credentials from either default location of specific config
        val credentials = creds(cls, path, section)
        return sqs(credentials, region ?: Regions.US_EAST_1)
    }

    /**
     * build sqs client from optional conf paths
     * @return
     */
    fun sqs(credentials: AWSCredentials, region: Regions): AmazonSQSClient {

        val reg = Region.getRegion(region)
        val sqs = AmazonSQSClient(credentials)
        sqs.setRegion(reg)
        return sqs
    }

    /**
     * build s3 client from optional config paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     */
    fun s3(cls: Class<*>, path: String? = null, section: String? = null, region: Regions?): AmazonS3Client {
        // Get credentials from either default location of specific config
        val credentials = creds(cls, path, section)
        return s3(credentials, region ?: Regions.US_EAST_1)
    }

    /**
     * build s3 client from optional config paths
     * @param path : Path to config ( None => default aws {user_dir}/.aws/credentials file}
     * @param section : Section name in config containing slate kit ApiCredentials
     */
    fun s3(credentials: AWSCredentials, region: Regions): AmazonS3Client {

        val reg = Region.getRegion(region)
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
    fun creds(cls: Class<*>, path: String? = null, section: String? = null): AWSCredentials {
        val credentials = path?.let { filePath ->

            // Get api key from config path supplied
            val apiKey = Config.of(cls, filePath).apiLogin(section ?: "aws")

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
