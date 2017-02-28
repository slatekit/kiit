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
package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec}
import slate.common.DateTime
import slate.common.query.{QueryEncoder, Query}

class QueryTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "Sql Value Conversion") {

    it("can convert null") {
      assert( QueryEncoder.convertVal(null) == "")
    }


    it("can convert string empty") {
      assert( QueryEncoder.convertVal("") == "''")
    }


    it("can convert string non-empty") {
      assert( QueryEncoder.convertVal("slate kit") == "'slate kit'")
    }


    it("can convert string with quote") {
      assert( QueryEncoder.convertVal("slate kit's dope") == "'slate kit''s dope'")
    }


    it("can convert boolean true") {
      assert( QueryEncoder.convertVal(true) == "1")
    }


    it("can convert boolean false") {
      assert( QueryEncoder.convertVal(false) == "0")
    }


    it("can convert boolean datetime") {
      assert( QueryEncoder.convertVal(DateTime(2016, 10, 16)) == "'2016-10-16 12:00:00'")
    }


    it("can convert field") {
      assert( QueryEncoder.ensureField("a(1)2*3&4b") == "a1234b")
    }
  }


  describe ( "Sql Query Builder Filter") {

    it("can build empty") {
      assert( new Query().toFilter() == "")
    }


    it("can build filter 1") {
      assert( new Query().where("name", "=", "slate kit").toFilter() == "name = 'slate kit'")
    }


    it("can build where with 1 field of type bool") {
      assert( new Query().where("isactive", "=", true).toFilter() == "isactive = 1")
    }


    it("can build where with 1 field of type int") {
      assert( new Query().where("status", "=", 3).toFilter() == "status = 3")
    }


    it("can build where with 1 field of type datetime") {
      assert( new Query().where("date", "=", DateTime(2016, 10, 16)).toFilter() == "date = '2016-10-16 12:00:00'")
    }


    it("can convert where with 2 fields") {
      assert( new Query().where("name", "=", "slate kit's").and("version", "=", 2).toFilter()
        == "name = 'slate kit''s' and version = 2")
    }
  }


  describe ( "Sql Update Builder") {

    it("can build empty") {
      assert( new Query().toFilter() == "")
    }


    it("can build where with 1 field of type string") {
      assert( new Query().set("code", "1").where("name", "=", "slate kit").toUpdatesText() == "SET code='1' WHERE name = 'slate kit'")
    }


    it("can build where with 1 field of type bool") {
      assert( new Query().set("code", "1").where("isactive", "=", true).toUpdatesText() == "SET code='1' WHERE isactive = 1")
    }


    it("can build where with 1 field of type int") {
      assert( new Query().set("code", "1").where("status", "=", 3).toUpdatesText() == "SET code='1' WHERE status = 3")
    }


    it("can build where with 1 field of type datetime") {
      assert( new Query().set("code", "1").where("date", "=", DateTime(2016, 10, 16)).toUpdatesText() == "SET code='1' WHERE date = '2016-10-16 12:00:00'")
    }


    it("can convert where with 2 fields") {
      assert( new Query().set("code", "1").where("name", "=", "slate kit's").and("version", "=", 2).toUpdatesText()
        == "SET code='1' WHERE name = 'slate kit''s' and version = 2")
    }
  }
}
