<h1 align=center> Podo Lotto </h1><br/>

<h2>사용기술</h2>
<div display=flex>
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=black">
  <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=black">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=black">
  <img src="https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=Oracle&logoColor=black"><br/>
  <img width="83cm" src="https://img.shields.io/badge/React-61DAFB?style=flat-square&logo=React&logoColor=white"/>
  <img width="119cm" src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=JavaScript&logoColor=white"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=TypeScript&logoColor=white">
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=black">
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=black">
  <img src="https://img.shields.io/badge/Redux-764ABC?style=for-the-badge&logo=Redux&logoColor=black">
  <img src="https://img.shields.io/badge/StyledComponents-DB7093?style=for-the-badge&logo=StyledComponents&logoColor=black">
</div><br/>

<br/><h2>User [Code](https://github.com/hobakk/Lotto/blob/6daf87ae8f743fd3f639894d6304bb6795b45ca2/Java/src/main/java/com/example/sixnumber/user/service/UserService.java#L53)</h2>

### 1. 회원가입, 회원탈퇴
- 회원탈퇴 당일로 부터 1달간 데이터유지
- 기간 만료 이전에 재가입 시 계정 활성화
- 기간이 만료되면 스케줄러를 이용하여 자동 삭제처리
### 2. 로그인, 로그아웃
- JWT Token(access, refresh)을 이용한 로그인, 로그아웃 구현
- Spring Security 를 통한 인증/인가
### 3. 충전요청
- 입금메세지와 금액을 입력받아 Redis에 저장
- 삭제가 빈번하여 Redis로 timeout을 넣어 자동 삭제되게 구현
- 유저가 고의로 많은 충전요청을 할 수 있기에 3개의 제한을 둠
- 요청들이 Admin에게 처리되지 않고 timeout 된다면 count를 증가, 4이상일 때 충전요청을 할 수 없게됌
- 매일 6, 18시 스케줄러가 실행되고 count가 4인 유저 계정을 정지
### 4. 프리미엄
- 스케줄러를 이용한 프리미엄 해제 및 결제 자동화
### 5. 기타
- 조회: 닉네임,캐쉬, 충전요청, 거래내역, 회원정보, 비밀번호 재확인, 최근 구매한 번호 리스트
- 수정: 회원정보

<br/><br/><h2>Admin [Code](https://github.com/hobakk/Lotto/blob/331722f5f4e2f3da9b55a35fa9b411b69fda7c57/Java/src/main/java/com/example/sixnumber/user/service/AdminService.java#L35)</h2>

### 1. 모든 유저 조회, 모든 충전요청 조회
- FE: 조회된 정보들을 Nickname, cash 값으로 필터링
### 2. 기타
- 생성: 메인 통계 (Lotto 객체)
- 조회: 충전요청 검색
- 수정: 관리자 등록, 충전, 차감, 상태 수정, 권한 수정

<br/><br/><h2>Lotto [Code](https://github.com/hobakk/Lotto/blob/331722f5f4e2f3da9b55a35fa9b411b69fda7c57/Java/src/main/java/com/example/sixnumber/lotto/service/LottoService.java#L24)</h2>

Redis Cache 사용해서 33% 속도 개선 -> [Blog](https://holloweyed-snail.tistory.com/131)
### 1. 메인 로또 통계
- Cache 의 entryTtl 을 30분으로 제한하여 30분 단위로 갱신 (조회를 한다는 가정하에)
### 2. 월 통계, 저장된 월 통계 YearMonth(index)
- entryTtl 제한을 두지 않음

<br/><br/><h2>SixNumber [Code](https://github.com/hobakk/Lotto/blob/331722f5f4e2f3da9b55a35fa9b411b69fda7c57/Java/src/main/java/com/example/sixnumber/lotto/service/SixNumberService.java#L43)</h2>

### 1. 랜덤 번호 추천
### 2. 반복 연산된 번호 추천 [code](https://github.com/hobakk/Lotto/blob/main/Java/src/main/java/com/example/sixnumber/lotto/service/SixNumberService.java#L75-L131)
- Thead 최종 출력 TopNumber 개수 만큼 생성하여 연산 
- MultiThread 를 사용하여 23.5% 속도 개선 -> [Blog](https://holloweyed-snail.tistory.com/127)
- 주어진 조건에 따라 무작위로 숫자를 생성하고, 그 중에서 가장 자주 등장하는 숫자를 찾아 리스트로 반환

<br/><br/><h2>WinNumber [Code](https://github.com/hobakk/Lotto/blob/331722f5f4e2f3da9b55a35fa9b411b69fda7c57/Java/src/main/java/com/example/sixnumber/lotto/service/WinNumberService.java#L25)</h2>

Redis Cache 사용해서 속도 개선
### 1. 당첨번호 조회
- @Cacheable 을 적용하고 entryTtl 제한을 두지않음
### 2. 당첨번호 등록
- @CachePut 을 적용하여 저장되어 있는 RedisCache 를 갱신

<br/><br/><h2>JwtSecurityFilter [Code](https://github.com/hobakk/Lotto/blob/6daf87ae8f743fd3f639894d6304bb6795b45ca2/Java/src/main/java/com/example/sixnumber/global/scurity/JwtSecurityFilter.java#L31)</h2>

- refreshToken 가 유효하다면 accessToken 을 갱신

### 1. BlackList
- accessToken 을 Redis 에서 BlackList 로 관리

<br/><br/><h2>Scheduler [Code](https://github.com/hobakk/Lotto/blob/331722f5f4e2f3da9b55a35fa9b411b69fda7c57/Java/src/main/java/com/example/sixnumber/global/scheduler/GlobalScheduler.java#L29)</h2>

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
