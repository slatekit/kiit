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

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentials}


trait AwsSupport {

  def credentials(key:String, secret:String): AWSCredentials =
  {
    new BasicAWSCredentials(key, secret)
  }


  def credentialsFromLogon(): AWSCredentials =
  {
    new ProfileCredentialsProvider().getCredentials
  }
}
