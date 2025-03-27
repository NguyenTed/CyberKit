import { Button, Result } from "antd";
import { useNavigate } from "react-router-dom";

const ForbiddenPage = () => {
const navigate = useNavigate();

    return (
        <div style={{ paddingTop: "100px", textAlign: "center" }}>
            <Result
                status="403"
                title="403 - Forbidden"
                subTitle="You are not allowed to access this page."
                extra={
                    <Button type="primary" onClick={() => navigate("/")}>
                        Back to Home
                    </Button>
                }
            />
        </div>
    
    );
};

export default ForbiddenPage;
