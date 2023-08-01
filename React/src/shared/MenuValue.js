export const AdminMenuValue = {
    title: "관리자",
    content: [
        ["전체 유저 조회", "/admin/users"], 
        ["충전 요청 조회", "/admin/charges"],
        ["충전 요청 검색", "/admin/search"], 
        ["메인 로또 생성", "/admin/lotto"],
    ]
}

export const UserMenuValue = {
    title: "내정보",
    content: [
        ["마이페이지", "/my-page"], 
        ["충전 요청", "/set-charging"],
        ["충전 요청 확인", "/get-charging"], 
        ["월정액 신청", "/premium"],
        ["결재 내역", "/statement"],
    ]
}

export const LottoMenuValue = {
    title: "추천번호",
    content: [
        ["랜덤 번호 구매", "/buynum"], 
        ["프리미엄 번호 구매", "/stats/num"],
        ["이전 구매번호 조회", "/recent/num"], 
    ]
}