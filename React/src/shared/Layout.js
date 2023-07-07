import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../api/useUserApi';
import { useCookies } from 'react-cookie';
import { useMutation } from 'react-query';
import { logoutUser } from '../modules/userIfSlice';

const HeaderStyles = {
  margin: `0`,
  width: '100%',
  background: `#D8B2D8`,
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

  const handleMouseEnter = () => {
    setDropdown(true);
  };

  const handleMouseLeave = () => {
    setDropdown(false);
  };

  return (
    <div
      className="dropdown-menu"
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      style={{ position: 'relative' }}
    >
      <div className="menu-trigger" style={{ cursor: 'pointer' }}>
        <span>Menu</span>
      </div>
      {isDropdown && (
        <div className="dropdown-content" style={{
            position: 'absolute',
            top: '100%',
            left: '0',
            backgroundColor: 'white',
            border: `3px solid #FFB6C1`,
            padding: '10px',
            zIndex: '1',
          }}
        >
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            <li>
              <Link to={"/my-page"}>My Page</Link>
            </li>
            <li>
              <a href="/recommendation">Recommendation Number</a>
            </li>
            <li>
              <a href="/statistics">Statistics</a>
            </li>
          </ul>
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

  return (
    <div style={{ ...HeaderStyles }}>
        <div id='LogoTitle' onClick={()=>{navigate("/")}}>
            <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px", marginRight: "5px" }} />
            <span>포도 로또</span>
        </div>
          <div className='navigation-links' style={navigationLinksStyles}>
            <DropdownMenu />
          </div>
          <div style={{ marginLeft: "auto", marginRight: "10px" }}>
            <div id='sign'>
              <Link to={"/signin"}>로그인 </Link>/
              <Link to={"/signup"}> 회원가입</Link>
            </div>
            <div id='userIfDiv' style={{ display: "flex", color: "black", fontSize: "16px"}}>
              <p>
                <span style={{color:"#FF7F50"}}>{cash}</span> 원  
                <Link to={"/mypage"} style={{color:"#BDFCC9", marginLeft: "10px"}}>{nickname}</Link> 님 반갑습니다
                <Link to={"/"} style={{marginLeft: "10px"}} onClick={logoutHandler}>로그아웃</Link>
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