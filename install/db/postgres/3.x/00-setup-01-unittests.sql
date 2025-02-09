# --- !Ups
create schema if not exists unit_tests;


create table "unit_tests"."sample_entity" ( 
"id"                  BIGSERIAL      NOT NULL PRIMARY KEY,  
"test_string"         VARCHAR(30)    NOT NULL,  
"test_string_enc"     VARCHAR(100)   NOT NULL,  
"test_bool"           BOOLEAN        NOT NULL,  
"test_short"          SMALLINT       NOT NULL,  
"test_int"            INTEGER        NOT NULL,  
"test_long"           BIGINT         NOT NULL,  
"test_float"          FLOAT          NOT NULL,  
"test_double"         FLOAT8         NOT NULL,  
"test_enum"           INTEGER        NOT NULL,  
"test_localdate"      DATE           NOT NULL,  
"test_localtime"      TIME           NOT NULL,  
"test_localdatetime"  TIMESTAMP      NOT NULL,  
"test_zoneddatetime"  TIMESTAMPTZ    NOT NULL,  
"test_uuid"           VARCHAR(50)    NOT NULL,  
"test_uniqueid"       VARCHAR(50)    NOT NULL,  
"test_object_addr"    VARCHAR(40)    NOT NULL,  
"test_object_city"    VARCHAR(30)    NOT NULL,  
"test_object_state"   VARCHAR(20)    NOT NULL,  
"test_object_country" INTEGER        NOT NULL,  
"test_object_zip"     VARCHAR(5)     NOT NULL,  
"test_object_ispobox" BOOLEAN        NOT NULL 
);


create table IF NOT EXISTS "unit_tests"."user" (
"id"        BIGSERIAL     NOT NULL  PRIMARY KEY,
"userId"    VARCHAR(50)   NOT NULL,
"email"     VARCHAR(100)  NOT NULL,
"isActive"  BOOLEAN       NOT NULL,
"level"     INTEGER       NOT NULL,
"salary"    FLOAT8        NOT NULL,
"createdat" TIMESTAMPTZ   NOT NULL,
"createdby" BIGINT        NOT NULL,
"updatedat" TIMESTAMPTZ   NOT NULL,
"updatedby" BIGINT        NOT NULL
);

create table IF NOT EXISTS `Member` (
`id`         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
`groupid`    BIGINT     NOT NULL,
`userid`     BIGINT     NOT NULL
);

create table IF NOT EXISTS `Group` (
`id`      BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name`    NVARCHAR(30) NOT NULL
);