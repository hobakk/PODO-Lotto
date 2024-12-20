type SubMenuItem = [string, string];

export type MenuType = {
    title: string,
    content: SubMenuItem[],
}

export const AdminMenuValue: MenuType = {
    title: "관리자",
    content: [
        ["전체 유저 조회", "/admin/users"], 
        ["충전 요청 조회", "/admin/charges"],
        ["충전 요청 검색", "/admin/search"], 
        ["메인 생성 / 초기화", "/admin/lotto"],
        ["당첨번호 등록", "/admin/winnumber"],
        ["모든 문의 확인", "/admin/boards"],
        ["월별 통계 생성", "/admin/monthlystats"]
    ]
}

export const BoardMenuValue: MenuType = {
    title: "고객센터",
    content: [
        ["문의 하기", "/board/set"], 
        ["내 문의 확인", "/boards/status"],
        
    ]
}

export const UserMenuValue: MenuType = {
    title: "내정보",
    content: [
        ["마이페이지", "/my-page"], 
        ["충전 요청", "/set-charging"],
        ["충전 요청 확인", "/get-charging"], 
        ["프리미엄", "/premium"],
        ["거래 내역", "/statement"],
        ["최근 구매 조회", "/sixnumber-list"],
        ["지난주 당첨 확인", "/check-lotto/last-week"],
    ]
}

export const StatsMenuValue: MenuType = {
    title: "통계",
    content: [
        ["통합", "/stats/main"], 
        ["월별", "/stats/month"],
    ]
}

export const LottoMenuValue: MenuType = {
    title: "추천번호",
    content: [
        ["랜덤 번호 발급", "/buynum"], 
        ["프리미엄 번호 발급", "/stats/num"],
        ["이전 발급 번호 조회", "/recent/num"], 
    ]
}