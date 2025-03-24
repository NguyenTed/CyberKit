import axios from "./AxiosCustomize";

const getAccountAPI = () => {
  const URL_BACKEND = "/api/v1/auth/account";
  return axios.get<IBackendRes<TUserInfo>>(URL_BACKEND);
};
const getDelayAccountAPI = () => {
  const URL_BACKEND = "/api/v1/auth/account/waiting";
  return axios.get<IBackendRes<TUserInfo>>(URL_BACKEND);
};
const logoutAPI = () => {
  const URL_BACKEND = "/api/v1/auth/logout";
  return axios.post(URL_BACKEND);
};
const signupAPI = (datas: TRegisterReq) => {
  const URL_BACKEND = "/api/v1/auth/signup";
  const data = {
    name: datas.name,
    email: datas.email,
    password: datas.password,
    dateOfBirth: datas.dateOfBirth,
    gender: datas.gender,
  };
  return axios.post(URL_BACKEND, data);
};
const loginAPI = (email: string, password: string) => {
  const URL_BACKEND = "/api/v1/auth/login";
  const data = {
    email: email,
    password: password,
  };
  return axios.post<IBackendRes<IUserLogin>>(URL_BACKEND, data);
};
const loginByGithub = () => {
  const URL_BACKEND = "/api/v1/oauth2";
  return axios.get(URL_BACKEND);
};

export {
  getAccountAPI,
  logoutAPI,
  signupAPI,
  loginAPI,
  loginByGithub,
  getDelayAccountAPI,
};
