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
import slate.common.IocRunTime
import slate.common.logging._
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe.{Type,typeOf}

class LoggerTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }



  class LoggerTest(level:LogLevel       = Warn,
                   name:String          = "",
                   logType:Option[Type] = None
                  ) extends LoggerBase(level, name, logType ) {


    val logs = ListBuffer[LogEntry]()


    /**
      * Logs an entry
      *
      * @param entry
      */
    override protected def performLog(entry: LogEntry): Unit = {
      logs.append(entry)
      println(logs.length)
    }
  }


  describe ( "Log Setup") {

    it("can setup all fields") {
      val logger = new LoggerTest(Debug, "logtest", Some(typeOf[LoggerTest]))
      logger.info("info1")
      assert( logger.level   == Debug)
      assert( logger.name    == "logtest"         )
      assert( logger.logType == Some(typeOf[LoggerTest]))
    }


    it("can log tag") {
      val logger = new LoggerTest(Debug, "logtest", Some(typeOf[LoggerTest]))
      logger.info("info1", tag = Some("ABC123"))
      assertLogEntry ( logger.logs(0), "logtest", Info, "info1", Some("ABC123") )
    }


    it("can log err") {
      val logger = new LoggerTest(Debug, "logtest", Some(typeOf[LoggerTest]))
      logger.info("info1", ex = Some(new Exception("sample err")))
      assertLogEntry ( logger.logs(0), "logtest", Info, "info1", None )
      assert( logger.logs(0).ex.get.getMessage == new Exception("sample err").getMessage)
    }


    it("can create new logger") {
      val logOrig = new LoggerTest(Debug, "logtest", Some(typeOf[LoggerTest]))
      val logger = logOrig.getLogger( None, "logtest2", Some(typeOf[LoggerTests])).asInstanceOf[LoggerConsole]

      assert( logger.level   == Debug)
      assert( logger.name    == "logtest2"         )
      assert( logger.logType == Some(typeOf[LoggerTests]))
    }
  }


  describe ( "Log levels") {

    it("can log all") {
      val logger = new LoggerTest(Debug, "logtest")
      addEntries(logger)
      assert ( logger.logs.size == 5 )
      assertLogEntry ( logger.logs(0), "logtest", Debug, "debug1", None )
      assertLogEntry ( logger.logs(1), "logtest", Info , "info1" , None )
      assertLogEntry ( logger.logs(2), "logtest", Warn , "warn1" , None )
      assertLogEntry ( logger.logs(3), "logtest", Error, "error1", None )
      assertLogEntry ( logger.logs(4), "logtest", Fatal, "fatal1", None )
    }


    it("can log info only") {
      val logger = new LoggerTest(Info, "logtest")
      addEntries(logger)
      assert ( logger.logs.size == 4 )
      assertLogEntry ( logger.logs(0), "logtest", Info , "info1" , None )
      assertLogEntry ( logger.logs(1), "logtest", Warn , "warn1" , None )
      assertLogEntry ( logger.logs(2), "logtest", Error, "error1", None )
      assertLogEntry ( logger.logs(3), "logtest", Fatal, "fatal1", None )
    }


    it("can log warn only") {
      val logger = new LoggerTest(Warn, "logtest")
      addEntries(logger)
      assert ( logger.logs.size == 3 )
      assertLogEntry ( logger.logs(0), "logtest", Warn , "warn1" , None )
      assertLogEntry ( logger.logs(1), "logtest", Error, "error1", None )
      assertLogEntry ( logger.logs(2), "logtest", Fatal, "fatal1", None )
    }


    it("can log error only") {
      val logger = new LoggerTest(Error, "logtest")
      addEntries(logger)
      assert ( logger.logs.size == 2 )
      assertLogEntry ( logger.logs(0), "logtest", Error, "error1", None )
      assertLogEntry ( logger.logs(1), "logtest", Fatal, "fatal1", None )
    }


    it("can log fatal only") {
      val logger = new LoggerTest(Fatal, "logtest")
      addEntries(logger)
      assert ( logger.logs.size == 1 )
      assertLogEntry ( logger.logs(0), "logtest", Fatal, "fatal1", None )
    }
  }


  def addEntries(logger:LoggerTest):Unit = {
    logger.debug("debug1")
    logger.info ("info1" )
    logger.warn ("warn1" )
    logger.error("error1")
    logger.fatal("fatal1")
  }


  def assertLogEntry(entry:LogEntry, name:String, level:LogLevel, msg:String, tag:Option[String]):Unit = {
    assert(entry.level == level)
    assert(entry.msg == msg)
    assert(entry.tag == tag)
    assert(entry.name == name)
  }
}
