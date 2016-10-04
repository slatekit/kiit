package slate.common


import java.lang.annotation.Annotation
import java.lang.reflect.Method
import scala.collection.mutable.ListBuffer


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


object ReflectorJ {

  /**
   * Gets a member annotaction of the type supplied. This works only for the
   * class level annotations.
   * @param instance
   * @param anoType
   * @return
   */
  def getClassAnnotation[T <: java.lang.annotation.Annotation](instance:AnyRef, anoType:Class[T]): T =
  {
    val cls = instance.getClass
    val annotation = cls.getAnnotation[T](anoType)
    annotation.asInstanceOf[T]
  }

/*
  /**
   * Gets a member annotaction of the type supplied. This works only for the
   * class level annotations.
   * @param instance
   * @param anoType
   * @return
   */
  def getFieldAnnotations[T <: java.lang.annotation.Annotation](instance:AnyRef,
      anoType:Class[T], callback: (String, EntityField) => Unit ): Unit =
  {
    val cls = instance.getClass
    val fields = cls.getDeclaredFields()
    for(field <- fields)
    {
      val anno = field.getAnnotation[T](anoType)
      if(anno != null)
      {
        callback(field.getName, anno.asInstanceOf[EntityField])
      }
    }
  }


  /**
   * Gets a member annotaction of the type supplied. This works only for the
   * class level annotations.
   * @param instance
   * @param anoType
   * @return
   */
  def getMemberAnnotations[T <: java.lang.annotation.Annotation](instance:AnyRef,
        anoType:Class[T], callback: (String, EntityField) => Unit ): Unit =
  {
    val cls = instance.getClass
    val fields = cls.getDeclaredFields
    for(field <- fields)
    {
      val anno = field.getAnnotation[T](anoType)
      if(anno != null)
      {
        callback(field.getName, anno.asInstanceOf[EntityField])
      }
    }
  }
*/

  def getMethod(instance:AnyRef, name:String): Method =
  {
    val cls = instance.getClass()
    val methods = cls.getMethods().find( m => m.getName() == name)
    if ( methods.isEmpty )
      return null

    methods.get
  }


  def getMethodsWithAnnotations[T, A <: java.lang.annotation.Annotation](cls:Class[T],
      anoType:Class[A]) : ListBuffer[(Method,Annotation)] =
  {
    val matches = ListBuffer[(Method,Annotation)]()
    val methods = cls.getMethods

    for(method <- methods)
    {
      val anno = method.getAnnotation[A](anoType)
      if(anno != null)
      {
        matches.append((method, anno))
      }
    }
    matches
  }


  def callMethod(instance:AnyRef, method:Method, args:Map[String, Any] ): Unit =
  {
    method.invoke(instance, null)
  }


  def printParams(method:Method): Unit =
  {
    val parameters = method.getParameterTypes()
    for(parameter <- parameters)
    {
      println( s"name: ${parameter.getName}")
    }
  }

}
