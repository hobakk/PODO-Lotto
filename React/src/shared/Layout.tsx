import React, { ReactElement, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { CommonLink, LogoutStyle} from '../components/Styles';
import LogoutMutation from '../hooks/useLogoutMutation';
import MenuContainer from '../components/MenuContainer';
import { AdminMenuValue, LottoMenuValue, StatsMenuValue, UserMenuValue } from './MenuValue';
import { RootState } from '../config/configStore';
import uesAxiosResponseInterceptor from '../hooks/useAxiosResponseInterceptor';

const mainColor = `#9957F0`;

const HeaderStyles: React.CSSProperties = {
  width: '100%',
  background: `linear-gradient(to bottom, white 90%, ${mainColor} 10%)`,
  height: '60px',
  display: 'flex',
  placeItems: "center",
  fontWeight: '550',
};

const FooterStyles: React.CSSProperties = {
  width: '100%',
  height: '40px',
  display: 'flex',
  background: mainColor,
  color: 'white',
  alignItems: 'center',
  justifyContent: 'center',
  marginTop: "10px",
};

const layoutStyles: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: '100vh',
  backgroundColor: "white",
  overflow: "hidden", // 스크롤 기능
}

type UserIf = {
  cash: number;
  nickname: string;
  role: string;
}

function Header() {
  uesAxiosResponseInterceptor();
  const navigate = useNavigate();
  const logoutMutation = LogoutMutation();
  const userIf = useSelector((state: RootState)=>state.userIf) as UserIf;
  const { nickname, role, cash } = userIf;
  const [isLogin, setIsLogin] = useState<boolean>(false);
  const [isHover, setIsHover] = useState<boolean>(false);

  const logoutHandler = () => {
    // setIsLogin(false);
    logoutMutation.mutate();
  }

  useEffect(()=>{ 
    if (nickname !== "" && role !== "") setIsLogin(true);
    else setIsLogin(false);
  }, [userIf]);

  return (
    <div style={ HeaderStyles }>
      <div style={{ display:"flex", placeItems: "center",  width: "30%" }}>
        <div onClick={()=>{navigate("/")}} style={{ cursor: "pointer", marginLeft:"15px"}}>
          <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px"}} />
        </div>
        <div onClick={()=>{navigate("/")}} style={{ marginLeft:"10px", cursor: "pointer"}}>
          <span style={{ fontSize: "24px" }}>PODO Lotto</span>
        </div>
      </div>
      <div style={{ display:"flex", width: "40%", justifyContent: "center", height: "1cm" }}>
        <MenuContainer MenuValue={LottoMenuValue} />
        <MenuContainer MenuValue={StatsMenuValue} />
        <MenuContainer MenuValue={UserMenuValue} />
        {userIf.role === "ROLE_ADMIN" && <MenuContainer MenuValue={AdminMenuValue}/> }
      </div>
      <div style={{ display: "flex", justifyContent: "flex-end", width: "30%", marginRight: "15px" }}>
        {!isLogin ? (
          <div style={{ fontSize:"18px"}}>
            <Link to={"/signin"} style={{ textDecoration: "none" }}>로그인</Link>
            <span style={{ margin: "5px" }}>/</span>
            <Link to={"/signup"} style={{ textDecoration: "none" }}>회원가입</Link>
          </div>
        ):(
          <div>
            <CommonLink to={"/set-charging"} color={"#3E1F80"}>{cash}</CommonLink> 원  
            <CommonLink to={"/my-page"} color={"#F29135"}>{nickname}</CommonLink> 님 반갑습니다
            <span 
              style={{ ...LogoutStyle, color: isHover ? "blue" : "black"}} 
              onClick={logoutHandler} 
              onMouseEnter={()=>setIsHover(true)}
              onMouseLeave={()=>setIsHover(false)}
            >
              로그아웃
            </span>
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

type LayoutProps = {
  children: React.ReactNode;
}

function Layout({ children }: LayoutProps) {
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