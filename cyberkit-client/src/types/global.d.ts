export {};

declare global {
    interface IBackendRes<T> {
        error?: string | string[];
        message: string;
        statusCode: number | string;
        data?: T;
    }
    interface IModelPaginate<T> {
        meta: {
            current: number;
            pageSize: number;
            pages: number;
            total: number;
        },
        results: T[]
    }
    type TUserInfo = {
        email: string;
        name: string;
        role: string;
        premium: boolean;
        gender: string;
        dateOfBirth: string;
    }
    interface IUserLogin{
        accessToken: string;
        user : TUserInfo;
    }
    type TRegisterReq ={
        name: string;
        email: string;
        password: string;
        dateOfBirth: string;
        gender: string;
    }
    
    
}
 
