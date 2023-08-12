import React, { useState, useEffect } from 'react'
import { MenuDiv, MenuSpan, Dropdown, UlBox, LiBox, CustomLink } from './Styles';
import { MenuType } from '../shared/MenuValue';
import { useSelector } from 'react-redux';
import { RootState } from '../config/configStore';
import { useNavigate } from 'react-router-dom';

type MenuContainerProps = {
    MenuValue: MenuType; 
    AllowType: string;
}

function MenuContainer({ MenuValue, AllowType }: MenuContainerProps) {

    const [backColor, setBackColor] = useState<string>("white");
    const [isDropdown, setDropdown] = useState<boolean>(false);
    const [spanColor, setSpanColor] = useState<string>("black");
    const userRole = useSelector((state: RootState)=>state.userIf.role) as string;
    const navigate = useNavigate();

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

    useEffect(()=>{
        if (AllowType === "AllowLogin") {
            if (userRole === "") {
                alert("로그인 이후 이용해주세요");
                navigate("/signin");
            }
        } else if (AllowType === "AllowNotRoleUser") {
            if (userRole === "ROLE_USER") {
                alert("프리미엄 등록 이후 이용해주시기 바랍니다");
                navigate("/premium");
            } else if (userRole === "") {
                alert("로그인 이후 이용해주세요");
                navigate("/signin");
            }
        } else if (AllowType === "AllowOnlyAdmin") {
            if (userRole !== "ROLE_ADMIN") {
                alert("접근 권한이 없습니다");
                navigate("/");
            }
        }
    }, [userRole])
    
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