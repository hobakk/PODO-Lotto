import { styled } from "styled-components";
import { Link } from "react-router-dom";
import React from "react";

const mainColor = `#9957F0`;

export const CommonStyle: React.CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
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

export const MsgAndInput: React.CSSProperties = {
  fontSize: "23px",
  marginBottom: "25px",
  width: "14cm",
  display: "flex",
}
export const ButtonDiv: React.CSSProperties = {
  marginLeft: "auto",
  width: "6.7cm",
}
export const ButtonStyle: React.CSSProperties = {
  width: "100%",
  height: "30px",
}

export const TitleStyle: React.CSSProperties = {
  fontSize: "40px",
  marginBottom: "2cm",
}

export const BoardStyle: React.CSSProperties = {
  fontSize: "18px",
  width: "18cm",
  height:"1.8cm", 
  border:"2px solid black", 
  marginBottom:"15px",
  padding:"20px",
  backgroundColor:"#D4F0F0"
}

export const CommentStyle: React.CSSProperties = {
  borderRadius:"20px",
  marginTop:"2cm",
  width:"8cm",
  marginBottom:"15px",
  padding:"20px"
}

export const SelectStyle: React.CSSProperties = {
  width:"3cm",
  height:"0.7cm", 
  textAlign:"center", 
  fontSize:"16px"
}

type UlBoxProps = { 
  width?: string;
  height?: string;
}

export const InputBox =styled.input`
  width: 6.5cm;
  height: 25px;
  margin-left: auto;
  text-align: center;
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

export const TopDividingLine =styled.div<{width: string | undefined}>`
  border-top: 2px solid black;
  margin: auto;
  margin-bottom: 20px;
  width: ${(props) => props.width};
`

export const BottomDividingLine =styled.div<{width: string | undefined}>`
  border-bottom: 2px solid black;
  margin: auto;
  margin-top: 20px;
  width: ${(props) => props.width};
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
  top: 113%;
  left: 50%;
  transform: translateX(-50%);
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
  margin-left: 10px;
  margin-right: 2px;

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
  width: 4cm;
  height: 42.5px;
  background-color: ${(props)=>props.color};
`

export const MenuSpan =styled.span<ColorProps>`
  color: ${(props)=>props.color};
  display: flex;
  justify-content: center;
  font-size: 25px;
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