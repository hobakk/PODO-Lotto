import React, { useState, useEffect } from 'react'
import { MenuDiv, MenuSpan, Dropdown, UlBox, LiBox, CustomLink } from '../shared/Styles';
import { MenuType } from '../shared/MenuValue';

function MenuContainer({ MenuValue }: {MenuValue: MenuType}) {
    const [backColor, setBackColor] = useState<string>("white");
    const [isDropdown, setDropdown] = useState<boolean>(false);
    const [spanColor, setSpanColor] = useState<string>("black");

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
                    <Dropdown>
                        <UlBox width="6cm">
                        <div style={{ padding:"10px", justifyContent:"center" }}>
                            {MenuValue.content.map((item)=>{
                                return (
                                    <LiBox key={item[1]}>
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