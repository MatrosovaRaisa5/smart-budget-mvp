export interface RegisterRequest {
    email: string;
    password: string;
    name: string;
}

export type RegisterResponse = string;

export interface LoginRequest {
    email: string;
    password: string;
}
export interface LoginResponse {
    token: string;
    type: string;
    id: number;
    email: string;
    name: string;
}
