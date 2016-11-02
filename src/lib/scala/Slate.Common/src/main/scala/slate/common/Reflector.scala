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

package slate.common


import slate.common.utils.Temp

import scala.collection.mutable.{ListBuffer}
import scala.collection.immutable.{List}
import scala.reflect.runtime.{universe => ru}
import ru._


object Reflector {


  /**
    * Creates an instance of the type supplied ( assumes existance of a 0 param constructor )
    * Only works for non-inner classes and for types with 0 parameter constructors.
    * @tparam T
    * @return
    */
  def createInstance[T:TypeTag]() : T = {
    createInstance(typeOf[T]).asInstanceOf[T]
  }


  /**
    * Creates an instance of the type dynamically.( assumes existance of a 0 param constructor )
    * Only works for non-inner classes and for types with 0 parameter constructors.
    * @param tpe
    * @return
    */
  def createInstance(tpe:Type): Any = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val clsSym = tpe.typeSymbol.asClass
    val clsMirror = mirror.reflectClass(clsSym)

    val conSym = tpe.decls.filter( s =>
    {
      s.isMethod && s.isConstructor && s.asMethod.paramLists.flatten.isEmpty
    }).toList.apply(0).asMethod

    if(conSym == null)
      return null

    val conMirror = clsMirror.reflectConstructor(conSym)
    val instance = conMirror()
    return instance
  }


  /**
    * Creates an instance of the type dynamically using a constructor with 0 parameters.
    * @param tpe
    * @return
    */
  def createInstanceWithNoParams(tpe:Type): Any = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val clsSym = tpe.typeSymbol.asClass
    val clsMirror = mirror.reflectClass(clsSym)
    val conSym = tpe.decl(ru.termNames.CONSTRUCTOR).asMethod
    val conMirror = clsMirror.reflectConstructor(conSym)
    val instance = conMirror()
    instance
  }
  

  /**
    * Creates an instance of the type dynamically using the parameters supplied.
    * @param tpe
    * @return
    */
  def createInstanceWithParams(tpe:Type, args:List[Any]): Any = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val clsSym = tpe.typeSymbol.asClass
    val clsMirror = mirror.reflectClass(clsSym)
    val conSym = tpe.decl(ru.termNames.CONSTRUCTOR).asMethod
    val conMirror = clsMirror.reflectConstructor(conSym)
    val instance = conMirror(args: _*)
    return instance
  }


  /**
    * gets the scala.reflect.Type of a class using the instance.
    * @param inst
    * @return
    */
  def getTypeFromInstance(inst:Any): Type =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(inst)
    val clsSym = im.symbol
    val tpe = clsSym.selfType
    tpe
  }


  /**
    * Gets a member annotaction of the type supplied. This works only for the
    * class level annotations.
    * @param clsType : The type of the class to check for the annotation
    * @param anoType : The type of the annotation to check/retrieve
    * @return
    */
  def getClassAnnotation(clsType:Type, anoType:Type): Any =
  {
    val clsSym = clsType.typeSymbol.asClass
    getAnnotation(clsType, anoType, clsSym)
  }


  /**
    * Gets a member annotation of the type supplied.
    * NOTE: This does not yet populate the annotation values
    * TODO: figure out how to fill the annotation values
    * @param clsType   : The type of the class to check for the annotation
    * @param anoType   : The type of the annotation to check/retrieve
    * @param fieldName : The name of the field containing the annotations
    * @return
    */
  def getMemberAnnotation(clsType:Type, anoType:Type, fieldName:String): Any =
  {
    val memSym = clsType.member(ru.TermName(fieldName)).asMethod
    getAnnotation(clsType, anoType, memSym)
  }


  /**
   * Gets a member annotation of the type supplied.
   * NOTE: This does not yet populate the annotation values
   * TODO: figure out how to fill the annotation values
   * @param clsType   : The type of the class to check for the annotation
   * @param anoType   : The type of the annotation to check/retrieve
   * @return
   */
  def getFieldAnnotation(clsType:Type, anoType:Type, fieldName:String): Any =
  {
    val fieldSym = clsType.decl(ru.TermName(fieldName)).asTerm.accessed.asTerm
    getAnnotation(clsType, anoType, fieldSym)
  }


  /**
   * Gets a member annotation of the type supplied.
   * NOTE: This does not yet populate the annotation values
   * TODO: figure out how to fill the annotation values
   * @param clsType
   * @param anoType
   * @return
   */
  def getAnnotation(clsType:Type, anoType:Type, mem:Symbol): Any =
  {
    val annotations = mem.annotations
    val annotation = annotations.find(a => a.tree.tpe == anoType)
    if(annotation.isEmpty)
      return null

    val annotationArgs = annotation.get.tree.children.tail
    val annotationInputs = annotationArgs.map(a =>
    {
      var annoValue:Any = null

      // NOTE: could use pattern matching
      val pe0 = a.productElement(0)
      if(pe0.isInstanceOf[ru.Constant])
        annoValue = a.productElement(0).asInstanceOf[ru.Constant].value
      else if (a.children != null && a.children.size > 1 && a.productElement(1)
        .isInstanceOf[ru.Literal])
        annoValue = a.productElement(1).asInstanceOf[ru.Literal].value.asInstanceOf[Constant].value

      annoValue
    })
    val anoInstance = createInstanceWithParams(anoType, annotationInputs)
    anoInstance
  }


  def getFieldType(tpe:Type, fieldName:String): Type =
  {
    val fieldX = tpe.decl(ru.TermName(fieldName)).asTerm.accessed.asTerm
    fieldX.typeSignature.resultType
  }


  def getFieldTypeString(): Type = {
    getFieldType(typeOf[Temp], "typeString")
  }


  def getFields(cc: AnyRef) :Map[String,Any]=
  (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) => f.setAccessible(true)
     a + (f.getName -> f.get(cc))
  }


  def getFieldsDeclared(item:Any): List[FieldMirror] =
  {
    val info = ListBuffer[FieldMirror]()

    for(mem <- item.getClass.getDeclaredFields)
    {
      val fieldName = mem.getName
      val fieldMirror = getField(item, fieldName)
      info.append(fieldMirror)
    }
    info.toList
  }


  def getFields(item:Any, tpe:Type): String =
  {
    var info = ""

    for(mem <- tpe.members)
    {
      // Method + Public + declared in type
      //println(mem.fullName, mem.isMethod, mem.isTerm)

      if(mem.isPublic && !mem.isMethod && mem.isTerm && mem.owner == tpe.typeSymbol)
      {
        info = info + "\r\n" + mem.fullName
        info = info + "\r\n" + mem.name + ": " + getFieldValue(item, mem.name.toString)
      }
    }
    info
  }


  /**
    * Gets a field value from the instance
    * @param item: The instance to get the field value from
    * @param fieldName: The name of the field to get
    * @return
    */
  def getField(item:Any, fieldName:String) : FieldMirror =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(item)
    val clsSym = im.symbol
    val tpe = clsSym.selfType
    val fieldX = tpe.decl(ru.TermName(fieldName)).asTerm.accessed.asTerm
    val fmX = im.reflectField(fieldX)
    fmX
  }


  /**
    * Gets a field value from the instance
    * @param item: The instance to get the field value from
    * @param fieldName: The name of the field to get
    * @return
    */
  def getFieldValue(item:Any, fieldName:String) : Any =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(item)
    val clsSym = im.symbol
    val tpe = clsSym.selfType
    val fieldX = tpe.decl(ru.TermName(fieldName)).asTerm.accessed.asTerm
    val fmX = im.reflectField(fieldX)
    val result = fmX.get
    result
  }


  /**
    * Sets a field value in the instance
    * @param item: The instance to set the field value to
    * @param fieldName: The name of the field to set
    * @param v
    */
  def setFieldValue(item:Any, fieldName:String, v:Any) =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(item)
    val clsSym = im.symbol
    val tpe = clsSym.selfType
    val fieldX = tpe.decl(ru.TermName(fieldName)).asTerm.accessed.asTerm
    val fmX = im.reflectField(fieldX)
    fmX.set(v)
  }


  /**
    * calls a method on the instance supplied
    * @param inst
    * @param name
    * @param inputs
    */
  def callMethod(inst:Any, name: String, inputs: Array[Any]):Any =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(inst)
    val mem = im.symbol.typeSignature.member(ru.TermName(name)).asMethod
    val result = if(inputs == null)
      im.reflectMethod(mem).apply()
    else
      im.reflectMethod(mem).apply(inputs:_*)
    result
  }


  /**
   * Gets a method on the instance with the supplied name
   * @param instance
   * @param name
   * @return
   */
  def getMethod(instance:Any, name:String) : Symbol =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(instance)
    val mem = im.symbol.typeSignature.member(ru.TermName(name)).asMethod
    mem
  }


  /**
   * gets a handle to the scala method mirror which can be used for an optimized approach
   * to invoking the method later
   * @param instance
   * @param name
   * @return
   */
  def getMethodMirror(instance:Any, name:String) : MethodMirror =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(instance)
    val mem = im.symbol.typeSignature.member(ru.TermName(name)).asMethod
    val mirror = im.reflectMethod(mem)
    mirror
  }


  /**
   * gets a list of all the method parameters
   * @param mem
   * @return tuple(name:String, type:String, position:Index)
   */
  def getMethodParameters(mem: Symbol): List[ReflectedArg] =
  {
    val list = ListBuffer[ReflectedArg]()
    val args = mem.typeSignature.paramLists
    if(args == null || args.size == 0) return List[ReflectedArg]()

    for(arg <- args)
    {
      var pos = 0
      for(sym <- arg)
      {
        val term = sym.asTerm
        val isDefault = term.isParamWithDefault
        val typeSym = sym.typeSignature.typeSymbol
        list.append(new ReflectedArg(sym.name.toString, typeSym.name.toString, pos, typeSym, isDefault))
        pos = pos + 1
      }
    }
    list.toList
  }


  def getMethodsWithAnnotations(instance:AnyRef, clsTpe:Type, anoTpe:Type,
                                declaredInSelfType:Boolean = true):
      ListBuffer[(String,MethodSymbol,MethodMirror,Any)] =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(instance)

    val matches = ListBuffer[(String,MethodSymbol,MethodMirror,Any)]()
    for(mem <- clsTpe.members)
    {
      // Method + Public + declared in type
      if(mem.isMethod && mem.isPublic && (!declaredInSelfType ||
        ( declaredInSelfType && mem.owner == clsTpe.typeSymbol)))
      {
        val anno = getAnnotation(clsTpe, anoTpe, mem)
        if(anno != null )
        {
          val methodName = mem.name.toString()
          val methodMirror = im.reflectMethod(mem.asMethod)
          matches.append((methodName,mem.asMethod,methodMirror, anno))
        }
      }
    }
    matches
  }


  def getFieldsWithAnnotations(instance:AnyRef, clsTpe:Type, anoTpe:Type,
                               declaredInSelfType:Boolean = true):
      ListBuffer[(String,TermSymbol,FieldMirror,Any,Type)] =
  {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(instance)

    val matches = ListBuffer[(String,TermSymbol,FieldMirror,Any,Type)]()
    for(mem <- clsTpe.members)
    {
      // Method + Public + declared in type
      if(mem.isMethod && mem.isPublic && mem.asMethod.isGetter && (!declaredInSelfType ||
        ( declaredInSelfType && mem.owner == clsTpe.typeSymbol)))
      {
        val fieldSym = mem.asTerm.accessed.asTerm
        val fieldType = mem.typeSignature.resultType
        val anno = getAnnotation(clsTpe, anoTpe, fieldSym)
        if(anno != null )
        {
          val memberName = mem.name.toString()
          val memberMirror = im.reflectField(fieldSym)
          matches.append((memberName,fieldSym,memberMirror, anno, fieldType))
        }
      }
    }
    matches
  }


  def toFields(item:AnyRef):List[(String,Any)] = {
    val items = new ListBuffer[(String,Any)]

    items.append(("ABOUT", "==================================="))
    for((k,v) <- Reflector.getFields(item)) {
      items.append((k,v))
    }
    items.toList
  }


  def printParams(mem: Symbol): Unit =
  {
    val args = mem.typeSignature.paramLists
    if(args == null || args.size == 0) return

    for(arg <- args)
    {
      for(sym <- arg)
      {
        println(sym.name + " : " + sym.typeSignature.typeSymbol.name)
      }
    }
  }


  def printMethods(tpe:Type, anoTpe:Type): Unit =
  {
    for(mem <- tpe.members)
    {
      // Method + Public + declared in type
      if(mem.isMethod && mem.isPublic && mem.owner == tpe.typeSymbol)
      {
        println("METHOD     : " + mem.fullName)
        println("owner      : " + mem.owner)
        println("name       : " + mem.name)
        println("type sig   : " + mem.typeSignature)
        printParams(mem)
        printAnnotations(tpe, anoTpe, mem)
        println("returns    : " + mem.typeSignature.resultType)
        println()
      }
    }
  }


  def printFields(tpe:Type, anoTpe:Type): Unit =
  {
    for(mem <- tpe.members)
    {
      // Method + Public + declared in type
      if(mem.isMethod && mem.isPublic && mem.asMethod.isGetter && mem.owner == tpe.typeSymbol)
      {
        println("FIELD      : " + mem.fullName)
        println("owner      : " + mem.owner)
        println("name       : " + mem.name)
        println("type sig   : " + mem.typeSignature)
        println("returns    : " + mem.typeSignature.resultType)
        val anno = getAnnotation(tpe, anoTpe, mem.asTerm.accessed.asTerm)
        if(anno != null ) println(anno.toString)
        println()
      }
    }
  }


  def printAnnotations(tpe:Type, anoTpe:Type, mem: Symbol): Unit =
  {
    val annos = mem.annotations
  //  val annos = tpe.typeSymbol.annotations
    if(annos == null || annos.size == 0) return

    for(anno <-annos)
    {
      println(anno.tree.tpe.typeSymbol.name)
      for(child <- anno.tree.children)
      {
        //println(child)
        println(child.productPrefix)
        for(pe <- child.productIterator)
        {
          println(pe)
        }
      }
    }
  }


  /*
  def methodAnnotations[T: TypeTag]: Map[String, Map[String, Map[String, Any]]] = {
    typeOf[T].decls.collect { case m: MethodSymbol => m }.withFilter {
      _.annotations.length > 0
    }.map { m =>
      m.name.toString -> m.annotations.map { a =>
        a.tree.tpe.typeSymbol.name.toString -> a.tree.children.withFilter {
          _.productPrefix eq "AssignOrNamedArg"
        }.map { tree =>
          tree.productElement(0).toString -> tree.productElement(1)
        }.toMap
      }.toMap
    }.toMap
  }


  def methodAnnotations2(typ:Type): Map[String, Map[String, Map[String, Any]]] = {
    typ.decls.collect { case m: MethodSymbol => m }.withFilter {
      _.annotations.length > 0
    }.map { m =>
      m.name.toString -> m.annotations.map { a =>
        a.tree.tpe.typeSymbol.name.toString -> a.tree.children.withFilter {
          _.productPrefix eq "AssignOrNamedArg"
        }.map { tree =>
          tree.productElement(0).toString -> tree.productElement(1)
        }.toMap
      }.toMap
    }.toMap
  }


  def methodAnnotations3(typ:Type): Unit = {
    val methods = typ.decls.collect { case m: MethodSymbol => m }.withFilter
    { _.annotations.length > 0 }

    for(method <- methods)
    {
      for(anno <- method.annotations)
      {
        println(anno.tree.tpe.typeSymbol.name.toString)

        for(child <- anno.tree.children)
        {
          println("child: " + child)
          println("prefix:" + child.productPrefix)
          if(child.productElement(0).isInstanceOf[ru.Ident]) {
            println(child.productElement(0))
            println(child.productElement(1))
          }
        }
      }
    }
  }
  */
}
