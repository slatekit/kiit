
# Foundational
cd slatekit-result        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd slatekit-actors        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd slatekit-common        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-utils         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-requests      && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd slatekit-context       && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     

# Support
cd slatekit-tracking      && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-policy        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-meta          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-serialization && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-http          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-core          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   

# Apps / Services
cd slatekit-app           && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s  
cd slatekit-cli           && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s  
cd slatekit-jobs          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd slatekit-apis          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 

# Infrastructure
cd slatekit-cache         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd slatekit-db            && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd slatekit-notifications && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   

# Data 
cd slatekit-query         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s    
cd slatekit-data          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s    
cd slatekit-entities      && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s       
cd slatekit-migrations    && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s  

# Providers
cd ../../ext/kotlin/
cd slatekit-providers-aws         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd slatekit-providers-datadog     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd slatekit-providers-logback     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd slatekit-providers-kafka       && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          

# Connectors
cd slatekit-connectors-cli        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd slatekit-connectors-entities   && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd slatekit-connectors-jobs       && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd ../../lib/kotlin

# Integration
cd slatekit-integration   && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd slatekit-server        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd slatekit-generator     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
#cd slatekit-tools       && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s       
#cd slatekit-examples    && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s     
#cd slatekit-tests       && gradle clean build && cd ../ && sleep 5s 