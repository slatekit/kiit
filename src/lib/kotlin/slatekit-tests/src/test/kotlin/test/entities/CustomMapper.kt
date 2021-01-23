//package test.entities
//
//import slatekit.common.Record
//import slatekit.common.crypto.Encryptor
//import slatekit.common.data.DataAction
//import slatekit.common.data.IDb
//import slatekit.common.data.Value
//import slatekit.common.data.Values
//import slatekit.meta.Reflector
//import slatekit.meta.models.Model
//import slatekit.orm.OrmMapper
//import slatekit.orm.databases.vendors.MySqlConverter
//import test.setup.AuthorEnc
//import test.setup.StatusEnum
//import kotlin.reflect.KClass
//
//class CustomMapper1<T>(model: Model, db: IDb, kls: KClass<T>, val modeKey:String, val encryptors:Map<String, Encryptor>)
//    : OrmMapper<Long, T>(model, db, MySqlConverter<Long, T>(), Long::class, kls) where T : Any {
//
//    override fun encode(item: T, action: DataAction, enc: Encryptor?): Values {
//        val key = Reflector.getFieldValue(item, modeKey)
//        val encByKey = encryptors[key]
//        return super.encode(item, action, enc ?: encByKey)
//    }
//
//
//    override fun decode(record: Record, enc: Encryptor?): T? {
//        val key = record.getString(modeKey)
//        val encByKey = encryptors[key]
//        return super.decode(record, enc ?: encByKey)
//    }
//}
//
//
//class CustomMapper2(model: Model, db: IDb, kls: KClass<AuthorEnc>, val modeKey:String, val encryptors:Map<String, Encryptor>)
//    : OrmMapper<Long, AuthorEnc>(model, db, MySqlConverter<Long, AuthorEnc>(), Long::class, kls)  {
//
//    private val uuid      = this.model.fields.first { it.name == "uuid"      }
//    private val createdAt = this.model.fields.first { it.name == "createdAt" }
//    private val createdBy = this.model.fields.first { it.name == "createdBy" }
//    private val updatedAt = this.model.fields.first { it.name == "updatedAt" }
//    private val updatedBy = this.model.fields.first { it.name == "updatedBy" }
//    private val email     = this.model.fields.first { it.name == "email"     }
//    private val isActive  = this.model.fields.first { it.name == "isActive"  }
//    private val age       = this.model.fields.first { it.name == "age"       }
//    private val status    = this.model.fields.first { it.name == "status"    }
//    private val salary    = this.model.fields.first { it.name == "salary"    }
//    private val uid       = this.model.fields.first { it.name == "uid"       }
//    private val shardId   = this.model.fields.first { it.name == "shardId"   }
//    private val encmode   = this.model.fields.first { it.name == "encmode"   }
//
//
//    override fun encode(item: AuthorEnc, action: DataAction, enc: Encryptor?): Values {
//        val key = Reflector.getFieldValue(item, modeKey)
//        val encByKey = encryptors[key]
//        val values = listOf(
//                Value(createdAt.name, toSql(createdAt, item, false, encByKey)),
//                Value(createdBy.name, toSql(createdBy, item, false, encByKey)),
//                Value(updatedAt.name, toSql(updatedAt, item, false, encByKey)),
//                Value(updatedBy.name, toSql(updatedBy, item, false, encByKey)),
//                Value(email    .name, toSql(email    , item, false, encByKey)),
//                Value(isActive .name, toSql(isActive , item, false, encByKey)),
//                Value(age      .name, toSql(age      , item, false, encByKey)),
//                Value(status   .name, toSql(status   , item, false, encByKey)),
//                Value(salary   .name, toSql(salary   , item, false, encByKey)),
//                Value(uid      .name, toSql(uid      , item, false, encByKey)),
//                Value(shardId  .name, toSql(shardId  , item, false, encByKey)),
//                Value(encmode  .name, toSql(encmode  , item, false, encByKey))
//        )
//        return values
//    }
//
//
//    override fun decode(record: Record, enc: Encryptor?): AuthorEnc? {
//        val key = record.getString(modeKey)
//        val encByKey = encryptors[key]
//        val emailRaw = record.getString("email")
//        val email = when(enc){
//            null -> encByKey?.decrypt(emailRaw) ?: emailRaw
//            else -> enc.decrypt(emailRaw)
//        }
//        val item = AuthorEnc(
//                id        = record.getLong("id"),
//                uuid      = record.getString  ("uuid"     ),
//                createdAt = record.getDateTime("createdAt"),
//                createdBy = record.getLong    ("createdBy"),
//                updatedAt = record.getDateTime("updatedAt"),
//                updatedBy = record.getLong    ("updatedBy"),
//                email     = email,
//                isActive  = record.getBool    ("isActive" ),
//                age       = record.getInt     ("age"      ),
//                status    = StatusEnum.convert(record.getInt("status"   )) as StatusEnum,
//                salary    = record.getDouble  ("salary"   ),
//                uid       = record.getUUID    ("uid"      ),
//                shardId   = record.getUPID("shardId"  ),
//                encmode   = record.getString  ("encmode"  )
//        )
//        return item
//    }
//}