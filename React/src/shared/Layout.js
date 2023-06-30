import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from "styled-components"

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

const LiBox =styled.div`
  display: inline-block;
  margin-left: 10px;
`

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
            backgroundColor: '#f2f2f2',
            border: `1px solid red`,
            padding: '10px',
            zIndex: '1',
          }}
        >
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            <li>
              <a href="/my-page">My Page</a>
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
    const navigate = useNavigate();

  return (
    <div style={{ ...HeaderStyles }}>
        <div id='LogoTitle' onClick={()=>{navigate("/lotto")}}>
            <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px", }} />
            <span>포도 로또</span>
        </div>
          <div className='navigation-links' style={navigationLinksStyles}>
            <DropdownMenu />
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
      <Header />
        <div style={{...layoutStyles}}>
            {children}
        </div>
      <Footer />
    </div>
  );
}

export default Layout;