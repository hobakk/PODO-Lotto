import { styled } from "styled-components";

const mainColor = `#9957F0`;

export const CommonStyle = {
  display: 'flex',
  flexDirection: 'column',
  // justifyContent: 'center',
  alignItems: 'center',
  minHeight: '90vh',
}

export const SignBorder = {
  width: "45%",
  height: "22cm",
  // border: "3px solid black",
  backgroundColor: "white",
  margin: "3px",
}

export const InputBox =styled.input`
  width: 5cm;
  height: 25px;
`
export const OnOff =styled.span`
  color: ${(props) => props.color};
  font-size: 16px;
`
export const UlBox =styled.ul`
  width: ${(props) => props.width};
  height: ${(props) => props.height};
  /* border: 2px solid ${mainColor} ; */
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: center;
  background-color: white;
  font-size: 22px;
`

export const DividingLine =styled.div`
  border-bottom: 1px solid black;
  margin: 20px;
  margin-bottom: 10px;
`

export const MenuTitle =styled.p`
  color: black;
  text-align: center;
  font-size: 25px;
`

export const Dropdown =styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  display: flex;
  background-color: black;
  padding: 2px;
  z-index: 1;
`

export const LiBox =styled.li`
  margin-bottom: 10px;
`