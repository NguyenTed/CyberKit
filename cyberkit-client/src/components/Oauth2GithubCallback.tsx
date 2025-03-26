import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { sendGithubCode } from '../services/AuthApiService';

const GitHubOAuthCallback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    

    if (code) {
      handleGitHubLogin(code);
    } else {
      console.error("GitHub login failed: No code received");
    }
  }, []);

  const handleGitHubLogin = async (code: string) => {
    try {
      console.log(code);
      const res = await sendGithubCode(code);
      if(res.statusCode === 200 && res.data) {
        localStorage.setItem("access_token", res.data);
        navigate("/");
      }
      else{
        console.error("GitHub login unsuccessfully!",);
        navigate('/login');
      }
      
    } catch (error) {
      console.error("GitHub login error:", error);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen">
      <div className="text-center">
        <p className="text-xl font-bold">Logging in with GitHub...</p>
      </div>
    </div>
  );
  
};

export default GitHubOAuthCallback;