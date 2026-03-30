// providers/transactions.provider.ts
import { BaseProvider } from './base.provider';
import {
    Transaction,
    CreateTransactionRequest,
    TransactionFilters,
    DeleteTransactionResponse
} from '../models/transactions.types';

export class TransactionsProvider extends BaseProvider {

    async addTransaction(data: CreateTransactionRequest): Promise<Transaction> {
        try {
            if (!data.amount || data.amount <= 0) {
                throw new Error('Сумма траты должна быть больше 0');
            }
            if (!data.description || data.description.trim().length === 0) {
                throw new Error('Описание траты обязательно');
            }
            if (!data.categoryId) {
                throw new Error('Не выбрана категория траты');
            }
            if (!data.date || !this.isValidDate(data.date)) {
                throw new Error('Некорректная дата (ожидается формат ДД.ММ.ГГГГ)');
            }

            const response = await this.request<Transaction>(
                '/transactions',
                {
                    method: 'POST',
                    body: data
                }
            );

            return response;
        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('400')) {
                    throw new Error('Некорректные данные для добавления траты');
                }
                if (error.message.includes('401')) {
                    throw new Error('Не удалось добавить трату. Сессия истекла.');
                }
                if (error.message.includes('404')) {
                    throw new Error('Категория не найдена');
                }
                throw error;
            }
            this.handleError(error);
        }
    }

    async getTransactions(filters?: TransactionFilters): Promise<Transaction[]> {
        try {
            const queryParams = new URLSearchParams();

            if (filters?.categoryId) {
                queryParams.append('categoryId', filters.categoryId.toString());
            }
            if (filters?.fromDate && this.isValidDate(filters.fromDate)) {
                queryParams.append('fromDate', filters.fromDate);
            }
            if (filters?.toDate && this.isValidDate(filters.toDate)) {
                queryParams.append('toDate', filters.toDate);
            }

            const url = `/transactions${queryParams.toString() ? `?${queryParams.toString()}` : ''}`;
            const response = await this.request<Transaction[]>(
                url,
                { method: 'GET' }
            );

            return response || [];
        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('401')) {
                    throw new Error('Не удалось загрузить транзакции. Сессия истекла.');
                }
                throw error;
            }
            this.handleError(error);
        }
    }

    async deleteTransaction(id: number): Promise<DeleteTransactionResponse> {
        try {
            if (!id || id <= 0) {
                throw new Error('Некорректный ID транзакции');
            }

            const response = await this.request<DeleteTransactionResponse>(
                `/transactions/${id}`,
                { method: 'DELETE' }
            );

            return response;
        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('404')) {
                    throw new Error('Транзакция не найдена');
                }
                if (error.message.includes('401')) {
                    throw new Error('Не удалось удалить транзакцию. Сессия истекла.');
                }
                throw error;
            }
            this.handleError(error);
        }
    }

    private isValidDate(dateString: string): boolean {
        const regex = /^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$/;
        if (!regex.test(dateString)) return false;

        const [day, month, year] = dateString.split('.').map(Number);
        const date = new Date(year, month - 1, day);
        return date.getDate() === day && date.getMonth() === month - 1 && date.getFullYear() === year;
    }
}
