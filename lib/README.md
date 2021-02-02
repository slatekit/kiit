DEPENDENCIES:


# AWS
aws-java-sdk-core-1.11.100.jar
aws-java-sdk-dynamodb-1.11.100.jar
aws-java-sdk-kms-1.11.100.jar
aws-java-sdk-s3-1.11.100.jar
aws-java-sdk-sqs-1.11.100.jar


# MYSQL
- mysql-connector-java-5.1.38-bin.jar


# MISC
----------------------------------------------------------------------------------------
- threetenbp-1.3.8.jar
- json_simple-1.1.jar


# Kotlin
- see build files


# KTOR
- see slatekit.server


# LOGS
compile group: 'ch.qos.logback', name: 'logback-classic', version: '1.1.2'
compile group: 'ch.qos.logback', name: 'logback-core'   , version: '1.1.2'
compile group: 'org.logback-extensions', name: 'logback-ext-loggly'   , version: '0.1.2'


# METRICS
compile 'io.micrometer:micrometer-registry-datadog:latest.release'




