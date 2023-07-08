import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../api/useUserApi';
import { useCookies } from 'react-cookie';
import { useMutation } from 'react-query';
import { logoutUser } from '../modules/userIfSlice';
import { OnOff, UlBox, DividingLine, MenuTitle, Dropdown } from '../components/Styles';
import { setAdminMode } from '../modules/adminMode';

const mainColor = `#D8B2D8`;

const HeaderStyles = {
  margin: `0`,
  width: '100%',
  background: mainColor,
  height: '60px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center', // 중앙 배치
  color: 'white',
  fontWeight: '600',
  fontSize: `22px`
};

const FooterStyles = {
  width: '100%',
  height: '50px',
  display: 'flex',
  background: '#D8B2D8',
  color: 'white',
  alignItems: 'center',
  justifyContent: 'center',
  fontSize: '12px',
};

const layoutStyles = {
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: '90vh',
}

const navigationLinksStyles  = {
  marginLeft: '20px',
  display: 'flex',
  alignItems: 'center',
}

function DropdownMenu() {
  const [isDropdown, setDropdown] = useState(false);
  const navigate = useNavigate();
  const [onOff, setOnOff] = useState(false);

  const handleMouseEnter = () => {
    setDropdown(true);
  };

  const handleMouseLeave = () => {
    setDropdown(false);
  };

  const adminMode = useSelector((state)=>state.adminMode.mode)
  useEffect(()=>{
  }, [adminMode])

  return (
    <div
      className="dropdown-menu"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={{ position: 'relative' }}
    >
      <div className="menu-trigger" style={{ cursor: 'pointer', marginLeft: "80px" }}>
        <span>Menu</span>
      </div>
      {isDropdown && (
      <div style={navigationLinksStyles}>
        {adminMode ? (
        <Dropdown id='dropdown-user'>
          <UlBox>
            <MenuTitle>회원 관리</MenuTitle>
            <DividingLine />
            <li>
              <Link to={"/my-page"}>마이페이지</Link>
            </li>
            <li>
              <Link to={"/"}>충전 요청</Link>
            </li>
            <li>
              <Link to={"/"}>충전 요청 확인</Link>
            </li>
            <li>
              <Link to={"/"}>월정액 신청</Link>
            </li>
            <li>
              <Link to={"/"}>결재 내역</Link>
            </li>
          </UlBox>
          <UlBox style={{ marginLeft: "10px", marginRight: "10px" }}>
            <MenuTitle>추천 번호</MenuTitle>
            <DividingLine />
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
          </UlBox>
          <UlBox>
            <MenuTitle>통계</MenuTitle>
            <DividingLine />
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
          </UlBox>
        </Dropdown>
        ) : (
        <Dropdown id='dropdown-admin' >
          <UlBox>
            <MenuTitle>관리자 설정</MenuTitle>
            <DividingLine />
            <li>
              <Link to={"/my-page"}>마이페이지</Link>
            </li>
            <li>
              <Link to={"/"}>충전 요청</Link>
            </li>
            <li>
              <Link to={"/"}>충전 요청 확인</Link>
            </li>
            <li>
              <Link to={"/"}>월정액 신청</Link>
            </li>
            <li>
              <Link to={"/"}>결재 내역</Link>
            </li>
          </UlBox>
          <UlBox style={{ marginLeft: "10px", marginRight: "10px" }}>
            <DividingLine />
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
          </UlBox>
        </Dropdown>
        )}
      </div>
      )}
    </div>
  );
}

function Header() {
    const [cookies, setCookie, removeCookie] = useCookies([]);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const mutation = useMutation(logout, {
      onSuccess: () => {
        dispatch(logoutUser());
        removeCookie('accessToken');
        removeCookie('refreshToken');
      }
    });

    const [cash, setCash] = useState(0);
    const [nickname, setNickname] = useState("");
    const [userRole, setUserRole] = useState("");
    const [tFMode, setTFMode] = useState(false);
    const [onOff, setOnOff] = useState({
      color: "",
      text: "",
    });
    const userIf = useSelector((state) => state.userIf);
    const logoutHandler = () => {
      mutation.mutate();
    }
    
    useEffect(()=>{
      if (userIf.cash !== cash) {
        setCash(userIf.cash);
      }
      if (userIf.nickname !== nickname) {
        setNickname(userIf.nickname);
      }
      if (userIf.role !== userRole) {
        setUserRole(userIf.role);
      }
    }, [userIf])

    useEffect(()=>{
      console.log("cash, nickname 이 랜더링 되었습니다")
      const signElement = document.getElementById("sign");
      const userIfDivElement = document.getElementById("userIfDiv");

      if(userIf.cash !== 0 || userIf.nickname !== "") {
        signElement.style.display = "none";
        userIfDivElement.style.display = "block";
      } else {
        signElement.style.display = "block";
        userIfDivElement.style.display = "none";
      }
    }, [cash, nickname])

    useEffect(()=>{
      const adminElement = document.getElementById("showOrHideforAdmin");

      if (userRole == "ROLE_ADMIN") {
        adminElement.style.display = "block";
      } else {
        adminElement.style.display = "none";
      }
    }, [userRole])
    
    const adminModeHandler = () => {
      setTFMode(!tFMode);
    }

    useEffect(()=>{
      dispatch(setAdminMode(tFMode));
      if  (tFMode === true) {
        setOnOff({
          color: "green",
          text: "ON",
        })
      } else {
        setOnOff({
          color: "red",
          text: "OFF",
        })
      }
    }, [tFMode])

  return (
    <div style={{ ...HeaderStyles }}>
        <div id='LogoTitle' onClick={()=>{navigate("/")}}>
            <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px", marginRight: "5px" }} />
            <span>포도 로또</span>
        </div>
          <div className='navigation-links' style={navigationLinksStyles}>
            <DropdownMenu/>
            <div id="showOrHideforAdmin" style={{ display: "none", marginLeft: "20px", marginRight: "10px" }}>
              <div style={{ display: "flex"}}>
                <div>
                  <button onClick={adminModeHandler} style={{ width: "2.5cm", height: "30px", marginRight: "15px" }} >관리자 모드</button>
                </div>
                <div>
                  <OnOff color={onOff.color}>{onOff.text}</OnOff>
                </div>
              </div>
            </div>
          </div>
          <div style={{ marginLeft: "auto", marginRight: "10px" }}>
            <div id='sign'>
              <Link to={"/signin"}>로그인 </Link>/
              <Link to={"/signup"}> 회원가입</Link>
            </div>
            <div id='userIfDiv' style={{ display: "flex", color: "black", fontSize: "16px"}}>
              <p>
                <Link to={"/set-charging"} style={{color:"#FF7F50"}}>{cash}</Link> 원  
                <Link to={"/my-page"} style={{color:"#BDFCC9", marginLeft: "10px"}}>{nickname}</Link> 님 반갑습니다
                <Link style={{marginLeft: "10px"}} onClick={logoutHandler}>로그아웃</Link>
              </p>
            </div>
          </div>
    </div>
  );
}

function Footer() {
  return (
    <div style={{ ...FooterStyles }}>
      <a href='https://github.com/hobakk/'>Github</a>
      <a style={{paddingLeft: `20px`}} href='https://holloweyed-snail.tistory.com/'>Blog</a>
    </div>
  );
}


function Layout({ children }) {
  return (
    <div>
      <Header/>
        <div style={{...layoutStyles}}>
            {children}
        </div>
      <Footer />
    </div>
  );
}

export default Layout;