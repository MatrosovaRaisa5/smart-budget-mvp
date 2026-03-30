import { TokenService } from '../services/token.service';

export class BaseProvider {
    // Для деплоя нужно будет менять на:
    protected baseUrl: string = 'http://87.228.40.213:8080/api';
    //protected baseUrl: string = 'http://10.0.2.2:8080/api';

    protected async request<T>(
        endpoint: string,
        options: {
            method: 'GET' | 'POST' | 'PUT' | 'DELETE';
            body?: any;
            headers?: Record<string, string>;
        },
        skipAuth: boolean = false
    ): Promise<T> {
        const url = `${this.baseUrl}${endpoint}`;
        const headers = this.buildHeaders(options.headers, skipAuth);

        try {
            const response = await fetch(url, {
                method: options.method,
                headers,
                body: options.body ? JSON.stringify(options.body) : undefined
            });

            const responseData = await this.parseResponse(response);

            if (!response.ok) {
                this.handleHttpError(response.status, responseData, skipAuth);
            }

            return responseData as T;

        } catch (error) {
            this.handleNetworkError(error);
        }
    }

    private buildHeaders(customHeaders?: Record<string, string>, skipAuth: boolean = false): Record<string, string> {
        const headers: Record<string, string> = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...customHeaders
        };

        if (!skipAuth) {
            const token = TokenService.getToken();
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
        }

        return headers;
    }

    private async parseResponse(response: Response): Promise<any> {
        const responseText = await response.text();

        try {
            return JSON.parse(responseText);
        } catch {
            return responseText;
        }
    }

    private handleHttpError(status: number, data: any, skipAuth: boolean): never {
        if (status === 401 && !skipAuth) {
            TokenService.clear();
            setTimeout(() => TokenService.redirectToLogin(), 100);
            throw new Error('Сессия истекла. Пожалуйста, войдите снова.');
        }

        const errorMessage = this.getErrorMessage(status, data);
        throw new Error(errorMessage);
    }

    private getErrorMessage(status: number, data: any): string {
        if (typeof data === 'string' && data) {
            return data;
        }

        if (data?.message) {
            return data.message;
        }

        if (data?.error) {
            return data.error;
        }

        const statusMessages: Record<number, string> = {
            400: 'Неверные данные. Проверьте введенную информацию.',
            403: 'Доступ запрещен. Недостаточно прав.',
            404: 'Ресурс не найден.',
            409: 'Пользователь с таким email уже существует',
            422: 'Ошибка валидации данных.',
            500: 'Ошибка сервера. Попробуйте позже.'
        };

        return statusMessages[status] || `Произошла ошибка (${status})`;
    }

    private handleNetworkError(error: unknown): never {
        if (error instanceof Error) {
            if (this.isNetworkError(error.message)) {
                throw new Error('Нет подключения к интернету. Проверьте соединение.');
            }

            if (error.message.includes('timeout')) {
                throw new Error('Превышено время ожидания. Сервер не отвечает.');
            }

            throw error;
        }

        throw new Error('Неизвестная ошибка');
    }

    private isNetworkError(message: string): boolean {
        const networkErrors = [
            'Network request failed',
            'Could not connect',
            'network error',
            'Failed to fetch'
        ];
        return networkErrors.some(err => message.includes(err));
    }

    protected handleError(error: unknown): never {
        if (error instanceof Error) {
            throw error;
        }
        throw new Error('Произошла неизвестная ошибка');
    }
}
