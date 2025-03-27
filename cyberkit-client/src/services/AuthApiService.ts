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
  return axios.post(URL_BACKEND,{},
  {
    withCredentials: true,
  });
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
  return axios.post<IBackendRes<IUserLogin>>(URL_BACKEND, data,
  {
    withCredentials: true,
  });
};

const getGithubAuth = () => {
  const URL_BACKEND = "/api/v1/auth/github-login";
  return axios.get(URL_BACKEND);
};
const sendGithubCode = (data: string) => {
  const URL_BACKEND = "/api/v1/auth/github-code/" + data;
  return axios.post<IBackendRes<string>>(URL_BACKEND,{},
  {
    withCredentials: true,
  });
};
const getRefreshToken = () => {
  const URL_BACKEND = "/api/v1/auth/refresh";
  return axios.get<IBackendRes<string>>(URL_BACKEND, {
    withCredentials: true, 
  });
};
export {
  getAccountAPI,
  logoutAPI,
  signupAPI,
  loginAPI,
  getDelayAccountAPI,
  getGithubAuth,
  sendGithubCode,
  getRefreshToken
};
