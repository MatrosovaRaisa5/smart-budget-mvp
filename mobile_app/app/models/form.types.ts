export interface RegistrationFormData {
    fullname: string
    email: string
    password: string
    confirmPassword: string
}

export interface RegistrationErrors {
    fullname: string
    email: string
    password: string
    confirmPassword: string
    agreement: string
}

export interface RegistrationTouched {
    fullname: boolean
    email: boolean
    password: boolean
    confirmPassword: boolean
    agreement: boolean
}

export interface LoginErrors {
    email: string;
    password: string;
}

export interface LoginTouched {
    email: boolean;
    password: boolean;
}
