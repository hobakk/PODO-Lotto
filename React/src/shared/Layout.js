import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { CommonLink} from '../components/Styles';
import LogoutMutation from '../components/LogoutMutation';
import MenuContainer from '../components/MenuContainer';
import { AdminMenuValue, LottoMenuValue, StatsMenuValue, UserMenuValue } from './MenuValue';

const mainColor = `#9957F0`;

const HeaderStyles = {
  width: '100%',
  background: `linear-gradient(to bottom, white 90%, ${mainColor} 10%)`,
  height: '60px',
  display: 'flex',
  flexDirection: "row",
  alignItems: 'center',
  fontWeight: '550',
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
  minHeight: '100vh',
  backgroundColor: "white",
  overflow: "hidden", // 스크롤 기능
}

function Header() {
  const navigate = useNavigate();
  const logoutMutation = LogoutMutation();
  const userIf = useSelector((state) => state.userIf);
  const [isLogin, setIsLogin] = useState(false);

  const [cash, setCash] = useState(0);
  const [nickname, setNickname] = useState("");
  const [userRole, setUserRole] = useState("");
  
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
    if (userIf.nickname === "") {
      setIsLogin(false);
    } else {
      setIsLogin(true);
    }
  }, [userIf])

  return (
    <div style={ HeaderStyles }>
      <div id='LogoTitle' onClick={()=>{navigate("/")}} style={{ cursor: "pointer", marginLeft:"15px"}}>
        <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px"}} />
      </div>
      <div onClick={()=>{navigate("/")}} style={{ cursor: "pointer"}}>
        <span style={{ fontSize: "26px" }}>PODO Lotto</span>
      </div>
      <div style={{ display:"flex", margin:"auto" }}>
        <MenuContainer MenuValue={LottoMenuValue}/>
        <MenuContainer MenuValue={StatsMenuValue}/>
        <MenuContainer MenuValue={UserMenuValue}/>
        {userIf.role === "ROLE_ADMIN" &&(
          <MenuContainer MenuValue={AdminMenuValue}/>
        )}
      </div>
      <div style={{ marginRight:"15px" }}>
        {!isLogin ? (
          <div>
            <Link to={"/signin"}> 로그인 </Link> /
            <Link to={"/signup"}> 회원가입</Link>
          </div>
        ):(
          <div style={{ display:"flex", alignItems:'center', justifyContent: 'center', }}>
            <CommonLink to={"/set-charging"} color={"#3E1F80"}>{cash}</CommonLink> 원  
            <CommonLink to={"/my-page"} color={"#F29135"}>{nickname}</CommonLink> 님 반갑습니다
            <CommonLink color={"black"} onClick={()=>logoutMutation.mutate()}>로그아웃</CommonLink>
          </div>
        )}
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
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Header/>
        <div style={ layoutStyles }>
            {children}
        </div>
      <Footer/>
    </div>
  );
}

export default Layout;