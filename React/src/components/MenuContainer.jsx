import React, { useState, useEffect } from 'react'
import { MenuDiv, MenuSpan, Dropdown, UlBox, LiBox, CustomLink } from './Styles';

function MenuContainer({ MenuValue }) {
    const [backColor, setBackColor] = useState("white");
    const [isDropdown, setDropdown] = useState(false);
    const [spanColor, setSpanColor] = useState("black");

    const handleMouseEnter = () => {
        setDropdown(true);
    };
    
    const handleMouseLeave = () => {
        setDropdown(false);
    };

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
        {MenuValue !== null && (
            <>
                <MenuDiv color={backColor}>
                    <MenuSpan color={spanColor}>{MenuValue.title}</MenuSpan>
                </MenuDiv>
                {isDropdown && (
                    <div style={navigationLinksStyles}>
                    <Dropdown id='dropdown-admin' >
                        <UlBox width="8cm">
                        <div style={{ margin: "20px", justifyContent:"center" }}>
                            {MenuValue.content.map((item)=>{
                                return (
                                    <LiBox>
                                        <CustomLink to={`${item[1]}`}>{item[0]}</CustomLink>
                                    </LiBox>
                                )
                                })}
                        </div>
                        </UlBox>
                    </Dropdown>  
                    </div>
                )}
            </>
        )}
        
        </div>
    )
}

export default MenuContainer;