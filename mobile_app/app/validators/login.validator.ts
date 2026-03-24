import { LoginErrors } from "~/models/form.types";

export class LoginValidator {
    static validateEmail(email: string): string {
        if (!email || email.trim() === '') {
            return 'Email обязателен';
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email.trim())) {
            return 'Введите корректный email';
        }

        return '';
    }

    static validatePassword(password: string): string {
        if (!password || password.trim() === '') {
            return 'Пароль обязателен';
        }

        if (password.length < 6) {
            return 'Пароль должен содержать минимум 6 символов';
        }

        return '';
    }

    static validateForm(email: string, password: string): LoginErrors {
        return {
            email: this.validateEmail(email),
            password: this.validatePassword(password),
        };
    }
}
