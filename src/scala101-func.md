---
layout: start_page
title: module Utils
permalink: /scala101_raw
---

# Scala 
Here is a brief introduction to scala. ( this doesn't list all the features language semantics )

# Resources
- [http://docs.scala-lang.org/cheatsheets/](http://docs.scala-lang.org/cheatsheets/)
- [https://mbonaci.github.io/scala/](https://mbonaci.github.io/scala/)
- [http://rea.tech/java-to-scala-cheatsheet/](http://rea.tech/java-to-scala-cheatsheet/)





## Classes: Deriving

```scala 

  // 1. You can declare class members in the constructor!
  // 2. You can declare set the access modifiers ( none = public, protected, private )
  // 3. You can declare whether they are mutable ( var ) or immutable ( val )
  class User( val name:String, val isActive:Boolean, val roles:String )
  {
  }


  // Just a simple example showing how to extend a class.
  class Admin() extends User("Administrator", true, "admin") 
  {
  }
}

```

{: .btn .btn-primary}
back to top
