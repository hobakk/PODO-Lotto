import axios from "axios";

const tokenMiddleware = (store) => (next) => (action) => {
    // const accessToken = store.getState().accessToken.accessToken;
    console.log(action.payload)
    console.log(action.endpoint)

    if (action.payload) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${action.payload}`;
    } 

    return next(action);
}

export default tokenMiddleware;