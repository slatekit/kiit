insert into `sample_entity` ( 
                    `test_string`,`test_string_enc`,`test_bool`,
                    `test_short`,`test_int`,`test_long`,`test_float`,`test_double`,`test_enum`,
                    `test_localdate`,`test_localtime`,`test_localdatetime`,`test_zoneddatetime`,
                    `test_uuid`,`test_uniqueId`,
                    `test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_ispobox`
            )  VALUES (?, ?, ?,
                    ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?,
                    ?, ?,
                    ?, ?, ?, ?, ?, ?
            );

            
create table `sample_entity` ( 
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,  
`test_string` NVARCHAR(30) NOT NULL,  
`test_string_enc` NVARCHAR(100) NOT NULL,  
`test_bool` BIT NOT NULL,  
`test_short` SMALLINT NOT NULL,  
`test_int` INTEGER NOT NULL,  
`test_long` BIGINT NOT NULL,  
`test_float` FLOAT NOT NULL,  
`test_double` DOUBLE NOT NULL,  
`test_enum` INTEGER NOT NULL,  
`test_localdate` DATE NOT NULL,  
`test_localtime` TIME NOT NULL,  
`test_localdatetime` DATETIME NOT NULL,  
`test_zoneddatetime` DATETIME NOT NULL,  
`test_uuid` NVARCHAR(50) NOT NULL,  
`test_uniqueid` NVARCHAR(50) NOT NULL,  
`test_object_addr` NVARCHAR(40) NOT NULL,  
`test_object_city` NVARCHAR(30) NOT NULL,  
`test_object_state` NVARCHAR(20) NOT NULL,  
`test_object_country` INTEGER NOT NULL,  
`test_object_zip` NVARCHAR(5) NOT NULL,  
`test_object_ispobox` BIT NOT NULL );

create table IF NOT EXISTS `User5` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`email` NVARCHAR(100) NOT NULL,
`isActive` BIT NOT NULL,
`level` INTEGER NOT NULL,
`salary` DOUBLE NOT NULL,
`createdat` DATETIME NOT NULL,
`createdby` BIGINT NOT NULL,
`updatedat` DATETIME NOT NULL,
`updatedby` BIGINT NOT NULL,
`uniqueid` NVARCHAR(50) NOT NULL
);

create table IF NOT EXISTS `Member` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`groupid` BIGINT NOT NULL,
`userid` BIGINT NOT NULL
);

create table IF NOT EXISTS `Group` (
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` NVARCHAR(30) NOT NULL
);