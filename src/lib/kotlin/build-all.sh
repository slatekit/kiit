
cd slatekit-common      && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 12s' && sleep 12s     
cd slatekit-db          && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 12s' && sleep 12s 
cd slatekit-query       && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 12s' && sleep 12s    
cd slatekit-meta        && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 12s' && sleep 12s   
cd slatekit-app         && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 12s' && sleep 12s  
cd slatekit-cli         && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s  
cd slatekit-workers     && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 
cd slatekit-core        && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   
cd slatekit-apis        && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 
cd slatekit-cloud       && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s    
cd slatekit-entities    && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s       
cd slatekit-orm         && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s  
cd slatekit-integration && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s          
cd slatekit-providers   && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s        
cd slatekit-server      && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   
#cd slatekit-tools       && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s       
#cd slatekit-examples    && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s     
#cd slatekit-tests       && gradle clean build && cd ../ && sleep 5s 