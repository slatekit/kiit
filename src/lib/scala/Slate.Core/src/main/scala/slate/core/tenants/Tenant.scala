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
package slate.core.tenants

import slate.common.{DateTime, Field}
import slate.entities.core.{EntityWithId}

case class Tenant (
                      @Field("",true, 50)
                      id: Long = 0L,


                      @Field("", true, 30)
                      name:String          = "",


                      @Field("", true, 30)
                      desc:String           = "",


                      @Field("", true, 30)
                      key:String        = "",


                      @Field("", true, 30)
                      folder:String      = "",


                      @Field("", true, -1)
                      isEnabled:Boolean     = false,


                      @Field("", true, 50)
                      account:String         = "",


                      @Field("", true, 50)
                      contact:String         = "",


                      @Field("", true, 50)
                      url:String         = "",


                      @Field("",true, 50)
                      uniqueId:String = "",


                      @Field("", true, -1)
                      createdAt:DateTime  = DateTime.now(),


                      @Field("", true, -1)
                      createdBy:Int  = 0,


                      @Field("", true, -1)
                      updatedAt:DateTime  =  DateTime.now(),


                      @Field("", true, -1)
                      updatedBy:Int  = 0
                      )
  extends EntityWithId {
}
