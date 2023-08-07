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

export const UlBox =styled.ul`
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
  width: 3cm;
  height: 115%;
  margin-left: 80px;
  background-color: ${(props)=>props.color};
`

export const MenuSpan =styled.span`
  color: ${(props)=>props.color};
  display: flex;
  justify-content: center;
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

export const ChangingColor = (num, index) => {
  let color = "";
  if (num <= 10) {
    color = "#eab541";
  } else if (num <= 20) {
    color = "#4331b0";
  } else if (num <= 30) {
    color = "#e61940";
  } else if (num <= 40) {
    color = "#545f69";
  } else {
    color = "#17c23a";
  }
  return <WinNumberStyle key={`numbers-${index}`} color={color} style={{ marginRight: "7px"}}>{num}</WinNumberStyle>
}