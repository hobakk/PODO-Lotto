import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useMutation } from 'react-query';
import { OnOff, CommonLink} from '../components/Styles';
import { setAdminMode } from '../modules/adminMode';
import LogoutMutation from '../components/LogoutMutation';
import DropdownMenu from "../components/DropDownMenu";

const mainColor = `#9957F0`;

const HeaderStyles = {
  margin: `0`,
  width: '100%',
  background: `linear-gradient(to bottom, white 90%, ${mainColor} 90%)`,
  height: '60px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center', // 중앙 배치
  color: 'black',
  fontWeight: '600',
  fontSize: `22px`
};

const FooterStyles = {
  width: '100%',
  height: '40px',
  display: 'flex',
  background: mainColor,
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
  minHeight: '90.1vh',
  backgroundColor: "white",
  overflow: "hidden", // 스크롤 기능x
}

function Header() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const logoutMutation = LogoutMutation();

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
    logoutMutation.mutate();
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
    tFMode ? (setOnOff({
      color: "green",
      text: "ON",
    })) : (setOnOff({
      color: "red",
      text: "OFF",
    }))
  }, [tFMode])

  const navigationLinksStyles  = {
    marginLeft: '30px',
    display: 'flex',
    alignItems: 'center',
  }

  return (
    <div style={{ ...HeaderStyles }}>
        <div id='LogoTitle' onClick={()=>{navigate("/")}} style={{ cursor: "pointer"}}>
          <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px", marginRight: "5px", marginLeft: "20px" }} />
        </div>
        <div onClick={()=>{navigate("/")}} style={{ cursor: "pointer"}}>
          <span style={{ fontSize: "26px" }}>PODO Lotto</span>
        </div>
          <div className='navigation-links' style={navigationLinksStyles}>
            <DropdownMenu/>
            <div id="showOrHideforAdmin" style={{ display: "none", marginLeft: "20px", marginRight: "10px" }}>
              <div style={{ display: "flex"}}>
                <div>
                  <button onClick={adminModeHandler} style={{ width: "2.3cm", height: "25px", marginRight: "15px",}} >관리자 모드</button>
                </div>
                <div>
                  <OnOff color={onOff.color}>{onOff.text}</OnOff>
                </div>
              </div>
            </div>
          </div>
          <div style={{ marginLeft: "auto", marginRight: "10px", fontSize: "20px" }}>
            <div id='sign'>
              <Link to={"/signin"}> 로그인 </Link> /
              <Link to={"/signup"}> 회원가입</Link>
            </div>
            <div id='userIfDiv' style={{ display: "flex", color: "black", fontSize: "16px"}}>
              <p style={{ marginRight: "30px" }}>
                <CommonLink to={"/set-charging"} color={"#3E1F80"}>{cash}</CommonLink> 원  
                <CommonLink to={"/my-page"} color={"#F29135"}>{nickname}</CommonLink> 님 반갑습니다
                <CommonLink color={"black"} onClick={logoutHandler}>로그아웃</CommonLink>
              </p>
            </div>
          </div>
    </div>
  );
}

function Footer() {
  return (
    <div style={{ ...FooterStyles }}>
      <a href='https://github.com/hobakk/' style={{ color: "white", fontSize: "14px" }}>Github</a>
      <a style={{paddingLeft: `20px`, color: "white", fontSize: "14px", }} href='https://holloweyed-snail.tistory.com/'>Blog</a>
    </div>
  );
}

function Layout({ children }) {
  return (
    <div>
      <Header/>
        <div style={{...layoutStyles, backdropFilter: "blur(20000px)"}}>
            {children}
        </div>
      <Footer />
    </div>
  );
}

export default Layout;