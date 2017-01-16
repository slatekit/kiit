#  WEB API SERVER

The Sample App Server shows Slate Kit protocol independent APIs running in the Slate Kit Server
Refer to API docs for more info on protocol independent APIs. 
http://www.slatekit.com/apis-detail.html

## ABOUT

- The Web API Server runs on Akka-Http and hosts the APIs located in the SampleApp.Core project.
- The APIs are protocol independent and can also run the CLI ( above ).
- In order to test the server do the following:

## NOTES

1. run script : run-sample-server.bat
2. open postman ( chrome extension to send/test http requests )
3. ensure you have setup a header with **api-key** => **54B1817194C1450B886404C6BEA81673**
4. ensure for "post" requests you have set the body = raw ( json/application )
5. ensure for "post" requests you have at least an empty json object "{ }" for calling endpoints/methods that take 0 params
6. use any of the urls below for testing

## TESTS

verb | header | url | body
------------ | ------------- | ------------- | -------------
get    | see step 3. |  http://localhost:5000/api/sys/version/java               | { }      
get    | see step 3. |  http://localhost:5000/api/sys/version/scala              | { }          
post   | see step 3. |  http://localhost:5000/api/app/info/lang                  | { }                 
post   | see step 3. |  http://localhost:5000/api/app/info/host                  | { }          
post   | see step 3. |  http://localhost:5000/api/app/info/app                   | { }        
post   | see step 3. |  http://localhost:5000/api/sampleapp/users/total          | { }
post   | see step 3. |  http://localhost:5000/api/sampleapp/users/create         | { "email" : "batman@gotham.com", "first" : "bruce", "last" : "wayne", "isMale" : true, "age" : 32, "phone" : "123456789", "country" : "us" }
post   | see step 3. |  http://localhost:5000/api/sampleapp/users/create         | { "email" : "superman@metropolis.com", "first" : "clark", "last" : "kent", "isMale" : true, "age" : 32, "phone" : "987654321", "country" : "us" }
post   | see step 3. |  http://localhost:5000/api/sampleapp/users/create         | { "email" : "wonderwoman@metropolis.com", "first" : "diana", "last" : "price", "isMale" : false, "age" : 32, "phone" : "111111111", "country" : "us" }
put    | see step 3. |  http://localhost:5000/api/sampleapp/users/updatePhone    | { "id" : 1, "phone": "1112223334" }
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/getById?id=2   | 
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/getAll         | { }
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/first          | { }
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/last           | { }
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/recent?count=2 | 
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/oldest?count=2 | 
delete | see step 3. |  http://localhost:5000/api/sampleapp/users/deleteById     | { "id" : 2 }       
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/total          | { }
get    | see step 3. |  http://localhost:5000/api/sampleapp/users/getAll         | { }

