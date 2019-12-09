
# Foundational
cd slatekit-result        && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s     
cd slatekit-common        && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s     
cd slatekit-tracking      && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   
cd slatekit-functions     && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   
cd slatekit-meta          && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   
cd slatekit-core          && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   

# Apps / Services
cd slatekit-app           && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s  
cd slatekit-cli           && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s  
cd slatekit-jobs          && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 
cd slatekit-apis          && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 
cd slatekit-cache         && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 
cd slatekit-cmds          && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 

# Infrastructure
cd slatekit-db            && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s 
cd slatekit-cloud         && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s    
cd slatekit-notifications && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   

# Entities / ORM
cd slatekit-query         && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s    
cd slatekit-entities      && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s       
cd slatekit-orm           && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s  

# Integration
cd slatekit-integration   && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s          
cd slatekit-providers     && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s        
cd slatekit-server        && gradle clean build bintrayUpload && cd ../ && echo 'sleeping 15s' && sleep 15s   
#cd slatekit-tools       && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s       
#cd slatekit-examples    && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s     
#cd slatekit-tests       && gradle clean build && cd ../ && sleep 5s 