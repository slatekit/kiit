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
package slate.tests.common

import slate.ext.reg.RegInfo

object RegTestHelper {

  def appendAppData(info:RegInfo):Unit = {
    info.version        = "1"
    info.appName        = "blend"
    info.appVersion     = "1.0"
    info.devicePlatform = "android"
    info.deviceType     = "phone"
    info.deviceModel    = "galaxy7"
    info.deviceOS       = "kitkat"
  }


  def appendUserData(info:RegInfo):Unit = {
    info.userName          = "john doe"
    info.email             = "johndoe@gmail.com"
    info.password          = ""
    info.phone             = "12223334444"
    info.countryCode       = "us"
    info.regId             = "123456789123456789"
    info.firstName         = ""
    info.lastName          = ""
    info.country           = "us"
    info.city              = ""
    info.state             = ""
    info.zip               = ""
    info.refTag            = ""
  }
}
