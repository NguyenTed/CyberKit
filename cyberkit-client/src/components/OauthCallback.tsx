import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';


const LoginSuccess: React.FC = () => {
    const navigate = useNavigate();
    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const params = new URLSearchParams(window.location.search);
                const token = params.get('access_token');
                if (token) {
                    localStorage.setItem('access_token', token);
                    navigate('/');
                }
            } catch (error) {
                console.error("Error fetching user info:", error);
                navigate('/login'); 
            }
        };

        fetchUserInfo();
    }, [navigate]);

    return (
        <div>Loading...</div>
    );
};

export default LoginSuccess;
