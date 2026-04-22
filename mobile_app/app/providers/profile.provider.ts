import { BaseProvider } from './base.provider';
import { TokenService } from '../services/token.service';

export interface UserProfile {
    email: string;
    name: string;
}

export interface UpdateProfileRequest {
    name: string;
}

export class ProfileProvider extends BaseProvider {
    async getProfile(): Promise<UserProfile> {
        try {
            const response = await this.request<UserProfile>(
                '/users/profile',
                { method: 'GET' }
            );
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('401')) {
                throw new Error('Не удалось загрузить профиль. Сессия истекла.');
            }
            this.handleError(error);
        }
    }

    async updateProfile(data: UpdateProfileRequest): Promise<string> {
        try {
            if (!data.name || data.name.trim() === '') {
                throw new Error('Имя не может быть пустым');
            }
            const response = await this.request<string>(
                '/users/profile',
                {
                    method: 'PUT',
                    body: { name: data.name.trim() }
                }
            );
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('400')) {
                throw new Error('Некорректное имя');
            }
            this.handleError(error);
        }
    }

    async deleteAccount(): Promise<string> {
        try {
            const response = await this.request<string>(
                '/users/account',
                { method: 'DELETE' }
            );
            TokenService.clear();
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('401')) {
                throw new Error('Не удалось удалить аккаунт. Сессия истекла.');
            }
            this.handleError(error);
        }
    }
}
