
# Foundational
cd ./common/
cd result        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd actors        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd common        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd utils         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd requests      && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd context       && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s     
cd http          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd ../../


# Support
cd ./infra/
cd telemetry     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd ../../
cd ./internal/
cd policy        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd meta          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd serialization && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd ../../

# Apps / Services
cd ./services/
cd app           && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s  
cd cli           && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s  
cd jobs          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd apis          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd server        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s
cd ../../

# Infrastructure
cd ./infra/
cd core          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd cache         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd db            && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s 
cd comms         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd ../../

# Data 
cd ./data/
cd query         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s    
cd data          && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s    
cd entities      && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s       
cd migrations    && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s  
cd ../../

# Providers
cd ./providers/
cd providers-aws         && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd providers-datadog     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd providers-logback     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd providers-kafka       && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd ../../

# Connectors
cd ./connectors/
cd connectors-cli        && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd connectors-entities   && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd connectors-jobs       && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd ../../

# Integration
cd ./support/
cd integration   && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s          
cd generator     && gradle clean build publish && cd ../ && echo 'sleeping 20s' && sleep 20s   
cd ../../
#cd slatekit-tools       && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s       
#cd slatekit-examples    && gradle clean build && cd ../ && echo 'sleeping 5s' && sleep 5s     
#cd slatekit-tests       && gradle clean build && cd ../ && sleep 5s 
