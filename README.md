<h1 align=center> Podo Lotto </h1><br/>

<h3> 사용기술 : </h3>
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

## User
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
- 닉네임,캐쉬 조회, 충전요청 조회, 거래내역 조회, 회원정보 조회
- 회원정보 수정, 비밀번호 재확인
## Admin
### 1. 모든 유저 조회, 모든 충전요청 조회
- FE: 조회된 정보들을 Nickname, cash 값으로 필터링
### 2. 기타
- 충전요청 검색
- 관리자 등록, 충전, 차감, 메인 로또 객체 생성, 상태 수정, 권한 수정
