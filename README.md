# URL 모음집

자기가 원하는 URL 저장소

# 기술스택
### Java 				                 – 11.0.21 version  
### MySql 				               - 8.0.32
### SpringBoot 			             - 2.7.9 version
### Mybatis 			               – 3.5.7 version
### Mybatis-spring 			         – 2.0.7 version
### Mybatis-spring-boot-starter – 2.3.0 version
### Lombok 			                – 1.18.26 version
### Commons-dbcp2 		          – 2.8.0 version
### Spring-boot-starter-jdbc 	  – 3.0.4 version
### Thymeleaf 			            - 3.0.11 RELEASE version 

# DB 설계
![포폴-8](https://github.com/sjk2224/SpringProject/assets/127715484/ecd202cc-d906-47fd-bdd0-a32f20fcac14)

### 뷰코드
```sql
VIEW `view_songboard` AS
    SELECT 
        `b`.`song_seq` AS `song_seq`,
        `b`.`mb_id` AS `mb_id`,
        `b`.`song_title` AS `song_title`,
        `b`.`song_singer` AS `song_singer`,
        `b`.`song_url` AS `song_url`,
        `b`.`song_create_at` AS `song_create_at`,
        `b`.`song_update_at` AS `song_update_at`,
        `f`.`up_original_file_name` AS `up_original_file_name`,
        `f`.`up_new_file_name` AS `up_new_file_name`,
        `f`.`up_file_path` AS `up_file_path`,
        `f`.`up_file_size` AS `up_file_size`
    FROM
        (`songboard` `b`
        JOIN `songfiles` `f`)
    WHERE
        ((`b`.`song_seq` = `f`.`song_seq`)
            AND (`b`.`mb_id` = `f`.`mb_id`))
```   




      
# 프로젝트 시연 화면

### 1. 로그인 화면
![포폴-1](https://github.com/sjk2224/SpringProject/assets/127715484/afc2fd38-a127-4c4b-b6ec-f23c7681e559)  
아이디와 비밀번호 입력후 로그인 버튼 클릭시 메인으로 이동  
회원 가입 버튼 클릭시 회원가입 페이지 이동  

### 2. 회원가입 화면
![포폴-2](https://github.com/sjk2224/SpringProject/assets/127715484/96afe0c2-bf5d-4e9c-a6d8-8fa98bd25d6c)  
아이디와 비밀번호 입력 후 회원 가입 버튼 클릭시 중복 검사 후 회원가입   

### 3. 메인 화면 설명
![포폴-3](https://github.com/sjk2224/SpringProject/assets/127715484/a3ea5d27-ab7e-471d-b6de-654c9f3053bf)  
#### 좌측메뉴바  
Dashboard: 클릭시 메인화면으로 이동  
Create: 클릭시 앨범 생성 창으로 이동  
Member Info: 클릭시 회원 정보화면으로 이동  
Logout: 클릭시 로그 아웃    

중앙 상단 검색창 : 검색어 입력 후 검색 버튼 클릭시 제목 또는 가수에 해당하는 목록이 DB에서 불러와짐  

앨범의 Edit 버튼: 클릭시 앨범 수정 페이지로 이동  
앨범의 Delete 버튼: 클릭시 앨범 삭제   

하단의 페이지 버튼: 클릭시 해당 되는 넘버의 페이지로 이동한다.

### 3-1. 검색 후 결과 반영된 메인화면
![포폴-4](https://github.com/sjk2224/SpringProject/assets/127715484/332870cb-ada4-4029-8a78-659f7516615c)  
검색 단어에 해당하는 앨범들이 메인에 띄어짐  

### 4. 앨범 추가 화면
![포폴-5](https://github.com/sjk2224/SpringProject/assets/127715484/c88959b9-efd0-4e98-8af2-88c067a681af)  
각 텍스트 박스에 텍스트를 입력하고 썸내일 파일용 이미지 파일을 선택 후 앨범 추가 버튼 클릭시 DB에 반영되고 앨범이 추가됨

### 4. 수정 화면 설명
![포폴-6](https://github.com/sjk2224/SpringProject/assets/127715484/3ea21d51-89bf-4b63-a2fa-a86ac74c2302)  
수정하고 싶은 부분을 수정 후에 수정하기 버튼 클릭시 앨범이 수정됨 (이미지 파일을 수정시 기존 이미지 파일은 삭제됨 )  

### 5. 회원 정보 화면 설명
![포폴-7](https://github.com/sjk2224/SpringProject/assets/127715484/ec92b231-9773-4b4c-9b35-966d5265b00b)  
회원의 아이디는 변경이 불가능하다.  
회원의 비밀번호를 변경 후 비밀번호 변경 버튼 클릭시 해당 회원의 비밀번호 변경 됨  
회원 탈퇴 버튼 클릭시 해당 회원의 모든 앨범이 DB에서 삭제됨  

