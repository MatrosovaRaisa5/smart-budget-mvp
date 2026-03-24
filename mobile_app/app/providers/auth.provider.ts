import { BaseProvider } from './base.provider';
import { TokenService } from '../services/token.service';
import {
    RegisterRequest,
    RegisterResponse,
    LoginRequest,
    LoginResponse
} from '../models/auth.types';

export class AuthProvider extends BaseProvider {
    async register(data: RegisterRequest): Promise<LoginResponse> {
        try {
            if (!data.email || !data.password || !data.name) {
                throw new Error('Все поля обязательны для заполнения');
            }

            if (data.password.length < 6) {
                throw new Error('Пароль должен содержать минимум 6 символов');
            }

            if (!this.isValidEmail(data.email)) {
                throw new Error('Введите корректный email адрес');
            }

            await this.request<RegisterResponse>(
                '/auth/register',
                {
                    method: 'POST',
                    body: {
                        email: data.email.trim().toLowerCase(),
                        password: data.password,
                        name: data.name.trim()
                    }
                },
                true
            );

            const loginResponse = await this.login({
                email: data.email,
                password: data.password
            });

            return loginResponse;

        } catch (error) {
            if (error instanceof Error) {
                const errorMessage = error.message.toLowerCase();

                if (errorMessage.includes('уже зарегистрирован') ||
                    errorMessage.includes('already exists') ||
                    error.message.includes('Email already exists')) {
                    throw new Error('Пользователь с таким email уже зарегистрирован');
                }

                if (error.message.includes('Invalid email')) {
                    throw new Error('Некорректный формат email');
                }

                if (error.message.includes('Password too weak')) {
                    throw new Error('Пароль слишком простой. Используйте минимум 6 символов');
                }

                throw error;
            }
            this.handleError(error);
        }
    }

    async login(data: LoginRequest): Promise<LoginResponse> {
        try {
            if (!data.email || !data.password) {
                throw new Error('Email и пароль обязательны');
            }
            const response = await this.request<LoginResponse>(
                '/auth/login',
                {
                    method: 'POST',
                    body: {
                        email: data.email.trim().toLowerCase(),
                        password: data.password
                    }
                },
                true
            );

            if (response.token) {
                TokenService.setToken(response.token);
            }

            return response;

        } catch (error) {
            if (error instanceof Error) {
                const errorMessage = error.message.toLowerCase();

                if (errorMessage.includes('неверный email или пароль') ||
                    errorMessage.includes('invalid credentials') ||
                    error.message.includes('Invalid credentials')) {
                    throw new Error('Неверный email или пароль');
                }

                throw error;
            }

            this.handleError(error);
        }
    }
    async checkAuth(): Promise<boolean> {
        const token = TokenService.getToken();
        if (!token) {
            return false;
        }

        // Запрос для проверки валидности сохранённого токена
        try {
            await this.request('/categories', { method: 'GET' });
            return true;
        } catch (error) {
            TokenService.clear();
            return false;
        }
    }

    async logout(): Promise<void> {
        TokenService.clear();
        TokenService.redirectToLogin();
    }

    isAuthenticated(): boolean {
        return TokenService.hasToken();
    }

    private isValidEmail(email: string): boolean {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
}
