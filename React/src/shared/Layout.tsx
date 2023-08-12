import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { CommonLink} from '../components/Styles';
import LogoutMutation from '../components/LogoutMutation';
import MenuContainer from '../components/MenuContainer';
import { AdminMenuValue, LottoMenuValue, StatsMenuValue, UserMenuValue } from './MenuValue';
import useCheckLogin from '../hooks/useCheckLogin';
import { RootState } from '../config/configStore';

const mainColor = `#9957F0`;

const HeaderStyles: React.CSSProperties = {
  width: '100%',
  background: `linear-gradient(to bottom, white 90%, ${mainColor} 10%)`,
  height: '60px',
  display: 'flex',
  flexDirection: "row",
  alignItems: 'center',
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
  fontSize: '12px',
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
  cash: string;
  nickname: string;
  role: string;
}

function Header() {
  const navigate = useNavigate();
  const logoutMutation = LogoutMutation();
  const userIf = useSelector((state: RootState)=>state.userIf) as UserIf;
  const isLogin = useCheckLogin();
  const [cash, setCash] = useState<string | undefined>();
  const [nickname, setNickname] = useState<string | undefined>("");
  
  useEffect(()=>{
    setCash(userIf.cash);
    setNickname(userIf.nickname);
  }, [userIf]);

  return (
    <div style={ HeaderStyles }>
      <div id='LogoTitle' onClick={()=>{navigate("/")}} style={{ cursor: "pointer", marginLeft:"15px"}}>
        <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px"}} />
      </div>
      <div onClick={()=>{navigate("/")}} style={{ cursor: "pointer"}}>
        <span style={{ fontSize: "26px" }}>PODO Lotto</span>
      </div>
      <div style={{ display:"flex", margin:"auto" }}>
        <MenuContainer MenuValue={LottoMenuValue} AllowType='AllowLogin'/>
        <MenuContainer MenuValue={StatsMenuValue} AllowType='AllowNotRoleUser'/>
        <MenuContainer MenuValue={UserMenuValue} AllowType='AllowLogin'/>
        {userIf.role === "ROLE_ADMIN" &&(
          <MenuContainer MenuValue={AdminMenuValue} AllowType='AllowOnlyAdmin'/>
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
            <div color={"black"} onClick={()=>logoutMutation.mutate()}>로그아웃</div>
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