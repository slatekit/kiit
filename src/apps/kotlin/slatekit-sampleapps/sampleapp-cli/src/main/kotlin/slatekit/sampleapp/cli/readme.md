#  SAMPLE CLI

The Sample App Shell shows Slate Kit protocol independent APIs running in a command line shell.
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html

## NOTES
1. run script : run-sample-shell.bat
2. type ? to list all the api areas 
3. type sys ? to list all the apis in the "sys" area 
4. type sys.version? to list all the actions in the "sys.version" api 
5. you can test any of the api commands below. 
6. type "exit" to shutdown the sample app shell and return the command line 

## EXAMPLES

:> ?
:> sys.version.java
:> sys.version.scala 
:> app.info.lang
:> app.info.host
:> app.info.app
:> sampleapp.users ?
:> sampleapp.users.total
:> sampleapp.users.create  -email="batman@gotham.com" -first="bruce" -last="wayne" -isMale=true -age=32 -phone="123456789" -country="us" 
:> sampleapp.users.create  -email="superman@metropolis.com" -first="clark" -last="kent" -isMale=true -age=32 -phone="987654321" -country="us" 
:> sampleapp.users.create  -email="wonderwoman@metropolis.com" -first="diana" -last="price" -isMale=false -age=32 -phone="111111111" -country="br" 
:> sampleapp.users.getById -id=1
:> sampleapp.users.getAll
:> sampleapp.users.updatePhone -id=1 -phone=1112223334
:> sampleapp.users.first          
:> sampleapp.users.getById -id=2   
:> sampleapp.users.last           
:> sampleapp.users.recent -count=2
:> sampleapp.users.oldest -count=2
:> sampleapp.users.deleteById -id=2     
:> sampleapp.users.total
:> sampleapp.users.getAll          


:> exit