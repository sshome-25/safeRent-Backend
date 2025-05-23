--- analysis ---
```
analysis_id	bigint	NO	PRI		auto_increment
overall_assessment	text	YES			
risk_factor1	text	YES			
solution1	text	YES			
risk_factor2	text	YES			
solution2	text	YES			
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE
```
--- assessment_houses ---
```
assessment_house_id	bigint	NO	PRI		auto_increment
location	point	NO	MUL		
price	int unsigned	NO			
market_price	int unsigned	NO			
area	decimal(6,3) unsigned	NO			
floor	tinyint	NO			
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
address	varchar(255)	NO			
is_safe	tinyint(1)	NO			
```
---assessments---
```
assessment_id	bigint	NO	PRI		auto_increment
user_id	bigint	NO	MUL		
register_id	bigint	NO	MUL		
assessment_house_id	bigint	NO	MUL		
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
```
---comments---
```
comment_id	bigint	NO	PRI		auto_increment
parent_comment_id	bigint	YES	MUL		
user_id	bigint	NO	MUL		
post_id	bigint	NO	MUL		
traded_house_id	bigint	YES	MUL		
content	text	NO			
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
```
contract_file_paths
```
contract_path_id	bigint	NO	PRI		auto_increment
file_path	varchar(255)	NO			
contract_id	bigint	NO	MUL		
```
contracts
```
contract_id	bigint	NO	PRI		auto_increment
analysis_id	bigint	NO	MUL		
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
```
favorites
```
favorite_id	bigint	NO	PRI		auto_increment
user_id	bigint	NO	MUL		
traded_house_id	bigint	NO	MUL		
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
```
posts
```
post_id	bigint	NO	PRI		auto_increment
user_id	bigint	NO	MUL		
traded_house_id	bigint	YES	MUL		
title	varchar(255)	NO			
content	text	NO			
view_count	int unsigned	NO		0	
prefer_location	varchar(255)	NO			
prefer_room_num	tinyint unsigned	NO			
prefer_area	decimal(6,3) unsigned	NO			
is_park	tinyint(1)	NO			
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
```
register_file_paths
```
register_path_id	bigint	NO	PRI		auto_increment
file_path	varchar(255)	NO			
register_id	bigint	NO	MUL		
```
registers
```
register_id	bigint	NO	PRI		auto_increment
analysis_id	bigint	NO	MUL		
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
```
roles
```
role_id	tinyint	NO	PRI		
name	varchar(255)	NO			
```
traded_houses
```
traded_house_id	bigint	NO	PRI		auto_increment
location	point	NO	MUL		
area	decimal(6,3) unsigned	NO			
floor	tinyint	NO			
built_year	smallint unsigned	NO			
transaction_date	date	NO			
price	int unsigned	NO			
sggCd	varchar(255)	NO			
umdNm	varchar(255)	NO			
jibun	varchar(255)	NO			
cityNm	varchar(255)	NO			
aptNm	varchar(255)	NO			
aptDong	varchar(255)	NO			
```
users
```
user_id	bigint	NO	PRI		auto_increment
email	varchar(255)	NO	UNI		
password	varchar(255)	NO			
created_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
updated_at	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED on update CURRENT_TIMESTAMP
status	enum('ACTIVE','INACTIVE')	NO		ACTIVE	
nickname	varchar(255)	NO	UNI		
role_id	tinyint	NO	MUL	1	
```
