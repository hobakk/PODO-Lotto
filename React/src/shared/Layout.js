import React from 'react';
import { useNavigate } from 'react-router-dom';

const CommonStyle = {
    paddingLeft: `10px`,
}

const HeaderStyles = {
...CommonStyle,
  width: '100%',
  background: 'black',
  height: '50px',
  display: 'flex',
  alignItems: 'center',
  color: 'white',
  fontWeight: '600',
};

const FooterStyles = {
  width: '100%',
  height: '50px',
  display: 'flex',
  background: 'black',
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

function Header() {
    const navigate = useNavigate();

  return (
    <div style={{ ...HeaderStyles }}>
        <div id='LogoTitle' onClick={()=>{navigate("/lotto")}}>
            <img src={process.env.PUBLIC_URL + `/logo.png`} alt='Logo' style={{ width: "30px", height: "30px" }} />
            <span>포도 로또</span>
        </div>
    </div>
  );
}

function Footer() {
  return (
    <div style={{ ...FooterStyles }}>
      <span>copyright @SCC</span>
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