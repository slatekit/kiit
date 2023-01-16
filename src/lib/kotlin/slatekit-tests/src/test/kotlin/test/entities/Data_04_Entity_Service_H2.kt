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

package test.entities

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import slatekit.common.data.*
import slatekit.data.sql.vendors.ifNotExists
import kiit.db.Db
import slatekit.entities.*
import test.setup.Group
import test.setup.Member
import test.setup.MyEncryptor
import test.setup.User5


class Data_04_Entity_Service_H2 : Data_04_Entity_Service_MySql() {


    @Before
    override fun setup() {
        val con = DbConString(Vendor.H2, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "", "")
        val cons = Connections.of(con)
        val db = Db.of(con)
        db.execute(USERS_DDL.ifNotExists("User5"))
        db.execute(GROUP_DDL.ifNotExists("Group"))
        db.execute(MEMBER_DDL.ifNotExists("Member"))

        entities = Entities({ _ -> db }, cons, MyEncryptor)
        entities.register<Long, User5>(EntityLongId(), vendor = Vendor.H2) { repo -> UserService(repo) }
        entities.register<Long, Member>(EntityLongId(), vendor = Vendor.H2) { repo -> EntityService(repo) }
        entities.register<Long, Group>(EntityLongId(), vendor = Vendor.H2) { repo -> EntityService(repo) }
    }


    @Test
    override fun can_create_an_item() {
        super.can_create_an_item()
    }


    @Test
    override fun can_update_an_item() {
        super.can_update_an_item()
    }


    @Test
    override fun can_count_any() {
        super.can_count_any()
    }


    @Test
    override fun can_count_size() {
        super.can_count_size()
    }


    @Test
    override fun can_get_first() {
        super.can_get_first()
    }


    @Test
    override fun can_get_last() {
        super.can_get_last()
    }


    @Test
    override fun can_get_recent() {
        super.can_get_recent()
    }


    @Test
    override fun can_get_oldest() {
        super.can_get_oldest()
    }


    @Test
    override fun can_get_all() {
        super.can_get_all()
    }


    @Test
    override fun can_find_by_field() {
        super.can_find_by_field()
    }


    @Test
    override fun can_get_aggregates() {
        super.can_get_aggregates()
    }


    @Test
    override fun can_find_by_query() {
        super.can_find_by_query()
    }


    @Test
    override fun can_patch_by_query() {
        super.can_patch_by_query()
    }


    @Test
    override fun can_get_relation() {
        super.can_get_relation()
    }


    @Test
    override fun can_get_relations() {
        super.can_get_relations()
    }


    @Test
    override fun can_get_relation_with_object() {
        super.can_get_relation_with_object()
    }


    companion object {
        val USERS_DDL = """
            CREATE TABLE `User5` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `email` varchar(100) NOT NULL,
              `isactive` bit(1) NOT NULL,
              `level` int(11) DEFAULT NULL,
              `salary` double NOT NULL,
              `createdat` datetime NOT NULL,
              `createdby` bigint(20) NOT NULL,
              `updatedat` datetime NOT NULL,
              `updatedby` bigint(20) NOT NULL,
              `uniqueid` varchar(50) NOT NULL,
              PRIMARY KEY (`id`)
            );
        """.trimIndent()


        val GROUP_DDL = """
            CREATE TABLE `Group` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `name` varchar(30) NOT NULL,
              PRIMARY KEY (`id`)
            );
        """.trimIndent()


        val MEMBER_DDL = """
            CREATE TABLE `Member` (
              `id` bigint(20) NOT NULL AUTO_INCREMENT,
              `groupid` bigint(20) NOT NULL,
              `userid` bigint(20) NOT NULL,
              PRIMARY KEY (`id`)
            );
        """.trimIndent()
    }
}
