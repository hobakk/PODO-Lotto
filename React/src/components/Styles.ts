import { styled } from "styled-components";
import { Link } from "react-router-dom";

const mainColor = `#9957F0`;

export const CommonStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  // justifyContent: 'center',
  alignItems: 'center',
  minHeight: '90vh',
}

export const SignBorder: React.CSSProperties = {
  width: "45%",
  height: "22cm",
  margin: "3px",
}

export const LogoutStyle: React.CSSProperties = {
  cursor: "pointer",
  marginLeft: "10px",
}

type UlBoxProps = { 
  width?: string;
  height?: string;
}

export const InputBox =styled.input`
  width: 5cm;
  height: 25px;
`

export const UlBox =styled.ul<UlBoxProps>`
  width: ${(props) => props.width};
  height: ${(props) => props.height};
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: center;
  background-color: white;
  font-size: 20px;
`

export const DividingLine =styled.div`
  border-bottom: 1px solid gray;
  margin: auto;
  margin-bottom: 20px;
  width: 4cm;
`

export const CommonP =styled.p`
  color: black;
  font-size: 30px;
  margin-bottom: "10px";
`

export const MenuTitle =styled.p`
  color: black;
  text-align: center;
  font-size: 25px;
`

export const Dropdown =styled.div`
  position: absolute;
  top: 115%;
  background-color: white;
  border: 6px solid #9957F0;
  padding: 10px 5px 10px 5px;
  z-index: 1;
`

export const LiBox =styled.li`
  margin-bottom: 10px;
`

export const CustomLink =styled(Link)`
  color: blue;
  text-decoration: none;
  font-size: 21px;
`

type ColorProps = { 
  color: string;
}

export const CommonLink = styled(Link)<ColorProps>`
  color: ${(props)=>props.color};
  text-decoration: none;
  margin-left: 20px;

  &:hover {
    color: blue;
    cursor: pointer;
  }

  &:active {
    color: red;
    cursor: grabbing;
  }
`

export const MenuDiv =styled.div<ColorProps>`
  cursor: pointer;
  width: 3cm;
  height: 115%;
  margin-left: 80px;
  background-color: ${(props)=>props.color};
`

export const MenuSpan =styled.span<ColorProps>`
  color: ${(props)=>props.color};
  display: flex;
  justify-content: center;
  font-size: 28px;
`

export const WinNumberStyle =styled.div<ColorProps>`
  background-color: ${(props)=>props.color};
  width: 1.1cm;
  height: 1.1cm;
  border-radius: 30px;
  text-align: center;
  color: white;
  font-weight: bold;
  display: flex;
  flex-direction: column;
  justify-content: center;
`