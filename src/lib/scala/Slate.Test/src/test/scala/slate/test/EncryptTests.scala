/**
*<slate_header>
  *author: Kishore Reddy
  *url: https://github.com/kishorereddy/scala-slate
  *copyright: 2015 Kishore Reddy
  *license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  *desc: a scala micro-framework
  *usage: Please refer to license on github for more info.
*</slate_header>
  */

package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.encrypt.Encryptor


class EncryptTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {


  def encryptor():Encryptor = {
    val enc = new Encryptor("wejklhviuxywehjk", "3214maslkdf03292")
    enc
  }


  def data(): List[(String, String)] = {
    val pairs = List[(String, String)](
      ("1234567890"                , "xW6vZHVYvoqfJT7cNfeW8A"),
      ("abcdefghijklmnopqrstuvwxyz", "hoeqMGoGwHH2HVQDV2w2eiINfVJX/qX/u+06TmDesvg"),
      ("`~!@#$%^&*()-_=+"          , "rpPgvVt28fQVNjCZhh0y5v2/jn+aq9qZI757kuAszy8"),
      ("[]\\;',./{}|:\"<>?"        , "gbi+NTCgEvul2NtTkF+LOT2H7Di4UTZbNn7GtsUm2dY")
    )
    pairs
  }


  test("can encrypt") {
    val enc = encryptor()
    val pairs = data()

    for(pair <- pairs){
      val encrypted = enc.encrypt(pair._1)
      assert( encrypted == pair._2)
    }
  }


  test("can decrypt") {
    val enc = encryptor()
    val pairs = data()

    for(pair <- pairs){
      val encrypted = enc.decrypt(pair._2)
      assert( encrypted == pair._1)
    }
  }
}
