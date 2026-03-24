import { ApplicationSettings } from '@nativescript/core';
import { $navigateTo } from 'nativescript-vue';

export class TokenService {
    private static readonly TOKEN_KEY = 'auth_token';

    static setToken(token: string): void {
        ApplicationSettings.setString(this.TOKEN_KEY, token);
    }

    static getToken(): string | null {
        const value = ApplicationSettings.getString(this.TOKEN_KEY);
        return value ? value : null;
    }

    static clear(): void {
        ApplicationSettings.remove(this.TOKEN_KEY);
    }

    static hasToken(): boolean {
        return !!this.getToken();
    }

    static redirectToLogin(): void {
        setTimeout(() => {
            const Login = require('../components/Login.vue').default;
            $navigateTo(Login, {
                transition: { name: 'fade', duration: 300 },
                clearHistory: true
            });
        }, 100);
    }
}
