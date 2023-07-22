import { styled } from "styled-components";
import { Link } from "react-router-dom";

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
  top: 100%;
  left: 0;
  display: flex;
  background-color: #9957F0;
  padding: 5px;
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

export const CommonLink = styled(Link)`
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

export const MenuDiv =styled.div`
  cursor: pointer;
  margin: auto;
  margin-left: 80px;
  margin-top: 8px;
  width: 3cm;
  height: 39px;
  background-color: ${(props)=>props.color};
  text-align: center;
`

export const MenuSpan =styled.span`
  color: ${(props)=>props.color};
  font-size: 28px;
`

export const WinNumberStyle =styled.div`
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
