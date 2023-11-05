<h1 align=center><img src=React/public/logo.png width=33 height=33 /> PODO Lotto </h1><br/>
<div align=center>
  <p>참고: 소제목 옆 Service 클릭하시면 제목에 알맞은 코드로 이동됩니다. ( 예: User Service )</p></br>
  <img width="600cm" src="https://github.com/hobakk/PODO-Lotto/assets/117063625/9f2120df-a334-45fe-b0d1-c6d854cc22ce" />
</div><br/><br/>
<h2>사용기술</h2>
<div display=flex>

  <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white">
  <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
  <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white"><br/>
  <img width="83cm" src="https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=React&logoColor=white"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=TypeScript&logoColor=white">
  <img src="https://img.shields.io/badge/Redux-764ABC?style=for-the-badge&logo=Redux&logoColor=white">
  <img src="https://img.shields.io/badge/StyledComponents-DB7093?style=for-the-badge&logo=StyledComponents&logoColor=white"></br>
  <img src="https://img.shields.io/badge/amazon ec2-FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white">
  <img src="https://img.shields.io/badge/github actions-2088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/nginx-009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
</div><br/><br/>
<h2>ERD 구조도</h2>
https://github.com/hobakk/PODO-Lotto/blob/main/podo_erd.png

<br/><br/><h2>User [Service](https://github.com/hobakk/PODO-Lotto/blob/1d7719faf81ee4a3677f3fb16e8fe4676965d272/Java/src/main/java/com/example/sixnumber/user/service/UserService.java#L61)</h2>

### 1. 회원가입
sendAuthCodeToEmail, compareAuthCode, signUp
- email 양식 및 중복 확인 후 인증 메일 발송
- 발송된 인증번호 일치 확인
- 탈퇴 여부 확인 후 수정 또는 회원가입 수행
### 2. 로그인, 로그아웃
참고: Google, Naver, Kakao OAuth2 로그인 구현
- JWT Token(access, refresh)을 이용한 로그인, 로그아웃 구현
- Spring Security 를 통한 인증/인가
### 3. 회원 탈퇴
- 회원 탈퇴 당일로부터 1달간 데이터 유지
- 기간 만료 이전에 회원가입으로 email, password 가 일치하면 계정 활성화
- 기간 만료 시 스케줄러가 자동 삭제
### 4. 충전요청
- 입금 메시지와 금액을 입력받아 timeOut 30분 설정 및 Redis에 저장
- 요청들이 Admin에게 처리되지 않고 timeout 된다면 count를 증가
- count 가 4 이상일 경우 충전요청을 할 수 없으며, 스케줄러에 의해 계정을 정지
### 5. 프리미엄
- Req 값에 따라 권한을 수정
### 6. 기타
- 조회: 닉네임과 캐쉬, 충전요청, 거래명세, 회원 정보, 비밀번호 재확인, 최근 구매한 번호 리스트
- 수정: 회원정보, 비밀번호 찾기, 출석 체크

<br/><br/><h2>Admin [Service](https://github.com/hobakk/PODO-Lotto/blob/694e68dd9c749b2edfefc4114dab18e3fa30ef5c/Java/src/main/java/com/example/sixnumber/user/service/AdminService.java#L35)</h2>

### 1. 모든 유저 조회, 모든 충전요청 조회
- FE: 조회된 정보들을 Nickname, cash 값으로 필터링
### 2. 기타
- 생성: 메인 통계 (Lotto 객체)
- 조회: 충전요청 검색
- 수정: 관리자 등록, 충전, 차감, 상태 수정, 권한 수정

<br/><br/><h2>Lotto [Service](https://github.com/hobakk/PODO-Lotto/blob/bfb80e761d2bb514c7d2b88cc0a6abacd2aa53d5/Java/src/main/java/com/example/sixnumber/lotto/service/LottoService.java#L21)</h2>

Redis Cache 사용해서 33% 속도 개선 -> [Blog](https://holloweyed-snail.tistory.com/131)
### 1. 메인 로또 통계
- Cache 의 entryTtl 을 30분으로 제한하여 30분 단위로 갱신 (조회를 한다는 가정하에)
### 2. 월 통계, 저장된 월 통계 YearMonth(index)
- entryTtl 제한을 두지 않음

<br/><br/><h2>SixNumber [Service](https://github.com/hobakk/PODO-Lotto/blob/8c45bd1c971c3d5b876bb653ed916aa44af50ec2/Java/src/main/java/com/example/sixnumber/lotto/service/SixNumberService.java#L44)</h2>

### 1. 랜덤 번호 추천
### 2. 반복 연산된 번호 추천
- Thead 최종 출력 TopNumber 개수 만큼 생성하여 연산 
- MultiThread 를 사용하여 23.5% 속도 개선 -> [Blog](https://holloweyed-snail.tistory.com/127)
- 주어진 조건에 따라 무작위로 숫자를 생성하고, 그 중에서 가장 자주 등장하는 숫자를 찾아 리스트로 반환
### 3. 이전 구매번호 조회

<br/><br/><h2>WinNumber [Service](https://github.com/hobakk/PODO-Lotto/blob/58d702b93b4f904d1d58cabc3a74fdbaa785b30b/Java/src/main/java/com/example/sixnumber/lotto/service/WinNumberService.java#L25)</h2>

Redis Cache 사용해서 속도 개선
### 1. 당첨번호 조회
- @Cacheable 을 적용하고 entryTtl 제한을 두지않음
### 2. 당첨번호 등록
- @CachePut 을 적용하여 저장되어 있는 RedisCache 를 갱신

### 2. BlackList
accessToken 의 유효시간이 5분이라 로그아웃 이후 만료 전 탈취 당했을 상황에 대처할 목적
- accessToken 을 Redis 에서 BlackList 로 관리

<br/><br/><h2>Scheduler [Service](https://github.com/hobakk/PODO-Lotto/blob/694e68dd9c749b2edfefc4114dab18e3fa30ef5c/Java/src/main/java/com/example/sixnumber/global/scheduler/GlobalScheduler.java#L30)</h2>

### 1. 월 통계 생성
- 현재 기준 저번달 통계가 존재하지 않을 때 통계를 생성 및 저장
### 2. 프리미엄 등록 또는 해제
- 프리미엄 등록 이후 31일 경과시 5000원 차감
- 조건에 부합하지 않다면 프리미엄 해제
### 3. 탈퇴한 유저 정보 일괄 삭제
- 탈퇴 이후 1달이 경과된 유저 전부를 삭제
### 4. 이용정지
- 충전 요청 미처리 횟수 초과시 상태 변경

<br/><br/>
