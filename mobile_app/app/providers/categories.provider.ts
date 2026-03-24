// providers/categories.provider.ts
import { BaseProvider } from './base.provider';
import {
    Category,
    GetCategoriesResponse,
    CreateCategoryRequest,
    CreateCategoryResponse,
    DeleteCategoryResponse
} from '../models/categories.types';

export class CategoriesProvider extends BaseProvider {
    async getCategories(): Promise<GetCategoriesResponse> {
        try {
            const response = await this.request<GetCategoriesResponse>(
                '/categories',
                { method: 'GET' }
            );

            return response;

        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('401')) {
                    throw new Error('Не удалось загрузить категории. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    async createCategory(data: CreateCategoryRequest): Promise<CreateCategoryResponse> {
        try {
            if (!data.name || data.name.trim() === '') {
                throw new Error('Название категории обязательно');
            }

            if (data.name.length < 2) {
                throw new Error('Название категории должно быть не менее 2 символов');
            }

            const response = await this.request<CreateCategoryResponse>(
                `/categories?name=${encodeURIComponent(data.name.trim())}`,
                { method: 'POST' }
            );

            return response;

        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('400')) {
                    throw new Error('Категория с таким названием уже существует');
                }
                if (error.message.includes('401')) {
                    throw new Error('Не удалось создать категорию. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    async deleteCategory(id: number): Promise<DeleteCategoryResponse> {
        try {
            if (!id) {
                throw new Error('ID категории не указан');
            }

            const response = await this.request<DeleteCategoryResponse>(
                `/categories/${id}`,
                { method: 'DELETE' }
            );

            if (typeof response === 'string') {
                return response;
            }

            if (response && typeof response === 'object' && 'message' in response) {
                return (response as any).message;
            }

            return response;

        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('404')) {
                    throw new Error('Категория не найдена');
                }
                if (error.message.includes('403')) {
                    throw new Error('Нельзя удалить глобальную категорию или категорию другого пользователя');
                }
                if (error.message.includes('401')) {
                    throw new Error('Не удалось удалить категорию. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    splitCategories(categories: Category[]): {
        global: Category[];
        user: Category[];
    } {
        return {
            global: categories.filter(c => c.global),
            user: categories.filter(c => !c.global)
        };
    }

    findCategoryById(categories: Category[], id: number): Category | undefined {
        return categories.find(c => c.id === id);
    }

    findCategoryByName(categories: Category[], name: string): Category | undefined {
        return categories.find(c => c.name.toLowerCase() === name.toLowerCase());
    }

    categoryExists(categories: Category[], name: string): boolean {
        return categories.some(c => c.name.toLowerCase() === name.toLowerCase());
    }
}
