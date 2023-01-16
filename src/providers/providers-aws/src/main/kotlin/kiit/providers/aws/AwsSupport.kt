/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.providers.aws

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider

interface AwsSupport {

    fun credentials(key: String, secret: String): AWSCredentials = BasicAWSCredentials(key, secret)

    fun credentialsFromLogon(): AWSCredentials = ProfileCredentialsProvider().credentials
}
