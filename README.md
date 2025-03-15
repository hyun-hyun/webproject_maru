# 1. 개요
## 1) 구성
![image](https://github.com/user-attachments/assets/c182ac6b-0e86-4abe-98e4-8cef1533187f)


1. local_deploy.bat을 활용해 build 및 로컬 내 Docker 배포

2. git develop branch에 commit, push

3. main에 pull request 후 merge

4. main에 push되면 GitHub Actions 동작

5. SSH를 통해 JAR파일 복사, 도커 재실행하여 배포 완료

6. 해당 commit으로 tag 생성(버전관리)

## 2) 사용기술

+ 백엔드: Java, Spring Boot, Spring Security, Gradle

+ 프론트엔드 : mustache, html, javascript

+ 데이터베이스: MySQL, Redis

+ 서버 및 배포: Docker, GitHub Actions, Linux (Synology NAS)

# 2. ERD
![real_erd](https://github.com/user-attachments/assets/028fbf25-ff2e-44e9-9ecf-c69ed7761e86)

# 3. 구현기능
## 1) 회원가입 및 회원정보 수정
- 닉네임 중복 확인
- 이메일 중복 확인
- 비밀번호 암호화
- 임시비밀번호로 변경
- 계정 삭제(비밀번호 확인 후)
- 이메일로 인증코드 발송
- 이메일로 보낸 인증코드 확인
- 비밀번호 찾기 시 임시비밀번호 생성 및 메일 전송

![image](https://github.com/user-attachments/assets/a540f3ed-21d3-4486-b1ce-73c02f70fded)


## 2) 회원별 조회 및 추천기능(마이페이지)
- 회원별 키워드 워드 클라우드(외부 API인 anychart 활용)
- 회원별 찜목록 5개(전체보기)
- 회원별 리뷰목록 5개(전체보기)
- 회원별 추천목록
- Pageable을 활용한 무한스크롤 구현

![image](https://github.com/user-attachments/assets/5699e80c-f0f4-464d-9b40-d5f11f9fe4b7)


## 3) 메인페이지
- 회원별 추천 5개(로그인 시)
- 최근 1개월간 높은 평점 받은 작품 15개 조회
- 최근 3개월간 높은 평점 받은 작품 50개 조회
- 최근 등록한 작품 15개 조회
- 전체 작품 조회(등록일자, 방영일자, 평점, 리뷰수 정렬 기능)

![image](https://github.com/user-attachments/assets/94c9ba9f-ddcb-466d-bb10-e9458375e5de)

## 4) 작품페이지
- 작품 등록, 수정, 삭제(관리자)
- 평점, 키워드, 한줄평으로 리뷰 등록, 수정, 삭제(회원)
- 댓글 등록, 수정, 삭제(회원)

  ![image](https://github.com/user-attachments/assets/583d743c-36d0-42f5-8d47-99fd5d79c65b)

## 5) QnA 페이지
- QnA 등록, 수정, 삭제, 답글, 공지여부 구현
- 권한별 공지, 답글, 수정, 삭제 제한
- Pageable을 활용한 페이지 구현

![image](https://github.com/user-attachments/assets/015b491e-65be-4349-902c-1d20b3fb7080)

