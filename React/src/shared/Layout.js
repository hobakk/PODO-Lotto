import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useMutation } from 'react-query';
import { OnOff, CommonLink} from '../components/Styles';
import { setAdminMode } from '../modules/adminMode';
import LogoutMutation from '../components/LogoutMutation';
import DropdownMenu from "../components/DropDownMenu";
import MenuContainer from '../components/MenuContainer';
import { AdminMenuValue, LottoMenuValue, UserMenuValue } from './MenuValue';

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
    setIsLogin(!isLogin);
  }, [userIf])

  const navigationLinksStyles  = {
    margin: 'auto',
    display: 'flex',
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
            <MenuContainer MenuValue={LottoMenuValue}/>
            <MenuContainer MenuValue={UserMenuValue}/>
            {userIf.role === "ROLE_ADMIN" &&(
              <MenuContainer MenuValue={AdminMenuValue}/>
            )}
          </div>
          <div style={{ marginLeft: "auto", marginRight: "10px", fontSize: "20px" }}>
            <div>
              {!isLogin ? (
                <div>
                  <Link to={"/signin"}> 로그인 </Link> /
                  <Link to={"/signup"}> 회원가입</Link>
                </div>
              ):(
                <div style={{ display: "flex", color: "black", fontSize: "16px"}}>
                  <p>
                    <CommonLink to={"/set-charging"} color={"#3E1F80"}>{cash}</CommonLink> 원  
                    <CommonLink to={"/my-page"} color={"#F29135"}>{nickname}</CommonLink> 님 반갑습니다
                    <CommonLink color={"black"} onClick={()=>logoutMutation.mutate()}>로그아웃</CommonLink>
                  </p>
                </div>
              )}
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