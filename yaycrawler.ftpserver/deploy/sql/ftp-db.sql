-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
-- 
--  http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

CREATE TABLE FTP_USER (      
   userid VARCHAR(64) NOT NULL PRIMARY KEY,       
   userpassword VARCHAR(64),      
   homedirectory VARCHAR(128) NOT NULL,             
   enableflag BOOLEAN DEFAULT TRUE,    
   writepermission BOOLEAN DEFAULT FALSE,       
   idletime INT DEFAULT 0,             
   uploadrate INT DEFAULT 0,             
   downloadrate INT DEFAULT 0,
   maxloginnumber INT DEFAULT 0,
   maxloginperip INT DEFAULT 0
);

insert into FTP_USER values("admin","admin:CE639E895E75B2C8A47B68C3FAF582A6","d:/1235",1,1,0,0,0,0,0);
insert into FTP_USER values("anonymous","","d:/1235",1,0,300,4800,4800,20,2);