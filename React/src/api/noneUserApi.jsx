import axios from "axios";
import { signApi } from "./config";

const signin = async (emailPassword) => {
    await signApi.post(`/signin`, emailPassword);
}

const signup = async (inputValue) => {
    return await axios.post(`${process.env.REACT_APP_SPRING_URL}/users/signup`, inputValue)
}

export { signin, signup }