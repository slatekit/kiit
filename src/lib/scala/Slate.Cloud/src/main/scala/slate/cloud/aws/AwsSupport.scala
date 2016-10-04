/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
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
