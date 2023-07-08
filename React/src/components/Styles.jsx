import { styled } from "styled-components";

const mainColor = `#D8B2D8`;

export const CommonStyle = {
  display: 'flex',
  flexDirection: 'column',
  // justifyContent: 'center',
  alignItems: 'center',
  minHeight: '90vh',
}

export const SignBorder = {
  width: "20cm",
  height: "20cm",
  border: "3px solid black",
  margin: "3px"
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
  width: 6cm;
  height: 6cm;
  border: 2px solid ${mainColor} ;
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: center;
  background-color: white;
  font-size: 18px;
`

export const DividingLine =styled.div`
  border-bottom: 1px solid black;
  margin: 15px;
`

export const MenuTitle =styled.p`
  color: black;
  text-align: center;
`
export const Dropdown =styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  display: flex;
  background-color: #D8B2D8;
  padding: 10px;
  z-index: 1;
`