import { useState, useEffect } from "react";
import { useSelector } from "react-redux";
import { UlBox, DividingLine, MenuTitle, Dropdown, LiBox, CustomLink, 
     MenuDiv, MenuSpan } from "./Styles";

function DropDownMenu() {
    const [isDropdown, setDropdown] = useState(false);
    const [backColor, setBackColor] = useState("white");
    const [spanColor, setSpanColor] = useState("black");
    const [tF, setTF] = useState(false);
  
    const handleMouseEnter = () => {
      setDropdown(true);
    };
  
    const handleMouseLeave = () => {
      setDropdown(false);
    };
  
    const userRole = useSelector((state)=>state.userIf.role);
    const adminMode = useSelector((state)=>state.adminMode.mode);
    
    useEffect(()=>{
      if  (userRole == "ROLE_ADMIN") {
        setTF(adminMode);
      } else if (adminMode === false) {
        setTF(false);
      }
    }, [adminMode])
  
    useEffect(()=>{
      if (isDropdown) {
        setBackColor("#9957F0");
        setSpanColor("white")
      } else {
        setBackColor("white");
        setSpanColor("black");
      }
    }, [isDropdown])

    const navigationLinksStyles  = {
        marginLeft: '30px',
        display: 'flex',
        alignItems: 'center',
    }
  
    return (
      <div
        className="dropdown-menu"
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        style={{ position: 'relative' }}
      >
        <MenuDiv color={backColor}>
          <MenuSpan color={spanColor}>Menu</MenuSpan>
        </MenuDiv>
        {isDropdown && (
        <div style={navigationLinksStyles}>
          {tF ? (
          <Dropdown id='dropdown-admin' >
            <UlBox width="12cm" height="9cm">
              <MenuTitle>관리자 설정</MenuTitle>
              <DividingLine />
              <div style={{ display: "flex", margin: "25px" }}>
                <div style={{marginRight: "20px"}} >
                  <LiBox>
                    <CustomLink to={"/"}>전체 유저 조회</CustomLink>
                  </LiBox>
                  <LiBox>
                    <CustomLink to={"/"}>충전 요청 조회</CustomLink>
                  </LiBox>
                  <LiBox>
                    <CustomLink to={"/"}>충전 요청 검색</CustomLink>
                  </LiBox>
                  <LiBox>
                    <CustomLink to={"/"}>관리자 권한 부여</CustomLink>
                  </LiBox>
                  <LiBox>
                    <CustomLink to={"/"}>포인트 지급</CustomLink>
                  </LiBox>
                </div>
                <div style={{ marginLeft: "40px" }}>
                  <LiBox>
                    <CustomLink to={"/"}>포인트 차감</CustomLink>
                  </LiBox>
                  <LiBox>
                    <CustomLink to={"/"}>메인 로또 생성</CustomLink>
                  </LiBox>
                  <LiBox>
                    <CustomLink to={"/"}>유저 상태 변경</CustomLink>
                  </LiBox>
                </div>
              </div>
            </UlBox>
          </Dropdown>
          ) : (
          <Dropdown id='dropdown-user'>
            <UlBox width="8cm" height="9cm">
              <MenuTitle>회원 관리</MenuTitle>
              <DividingLine />
              <LiBox>
                <CustomLink to={"/my-page"}>마이페이지</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/set-charging"}>충전 요청</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/get-charging"}>충전 요청 확인</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/premium"}>월정액 신청</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/statement"}>결재 내역</CustomLink>
              </LiBox>
            </UlBox>
            <UlBox width="8cm" height="9cm" >
              <MenuTitle>추천 번호</MenuTitle>
              <DividingLine />
              <LiBox>
                <CustomLink to={"/buynum"}>랜덤 번호 구매</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/stats/num"}>n회 반복 처리 된 번호 구매</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/recent/num"}>이전 구매번호 조회</CustomLink>
              </LiBox>
            </UlBox>
            <UlBox width="8cm" height="9cm">
              <MenuTitle>통계</MenuTitle>
              <DividingLine />
              <LiBox>
                <CustomLink to={"/stats/main"}>서버 통계</CustomLink>
              </LiBox>
              <LiBox>
                <CustomLink to={"/stats/month"}>월별 통계</CustomLink>
              </LiBox>
            </UlBox>
          </Dropdown>
          )}
        </div>
        )}
      </div>
    );
  }

export default DropDownMenu;