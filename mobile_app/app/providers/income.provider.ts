// providers/incomes.provider.ts
import { BaseProvider } from './base.provider';
import {
    CreateIncomeRequest,
    CreateIncomeResponse,
    GetIncomesResponse,
    DeleteIncomeResponse,
    Income
} from '../models/income.types';

export class IncomesProvider extends BaseProvider {
    async createIncome(data: CreateIncomeRequest): Promise<CreateIncomeResponse> {
        try {
            if (!data.amount || data.amount <= 0) {
                throw new Error('Сумма дохода должна быть больше 0');
            }

            if (!data.date) {
                throw new Error('Дата обязательна для заполнения');
            }

            const dateRegex = /^\d{2}\.\d{2}\.\d{4}$/;
            if (!dateRegex.test(data.date)) {
                throw new Error('Неверный формат даты. Используйте ДД.ММ.ГГГГ');
            }

            const response = await this.request<CreateIncomeResponse>(
                '/incomes',
                {
                    method: 'POST',
                    body: {
                        source: data.source,
                        amount: data.amount,
                        date: data.date
                    }
                }
            );

            return response;
        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('amount') && error.message.includes('positive')) {
                    throw new Error('Сумма должна быть положительным числом');
                }
                if (error.message.includes('date') && error.message.includes('format')) {
                    throw new Error('Неверный формат даты');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    async getIncomes(): Promise<GetIncomesResponse> {
        try {
            const response = await this.request<GetIncomesResponse>(
                '/incomes',
                { method: 'GET' }
            );

            return response;
        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('401')) {
                    throw new Error('Не удалось загрузить доходы. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    async deleteIncome(id: number): Promise<DeleteIncomeResponse> {
        try {
            if (!id) {
                throw new Error('ID дохода не указан');
            }

            const response = await this.request<DeleteIncomeResponse>(
                `/incomes/${id}`,
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
                    throw new Error('Доход не найден');
                }
                if (error.message.includes('403')) {
                    throw new Error('Нет прав для удаления этого дохода');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    getTotalAmount(incomes: Income[]): number {
        return incomes.reduce((sum, income) => sum + income.amount, 0);
    }

    groupBySource(incomes: Income[]): Record<string, number> {
        return incomes.reduce((acc, income) => {
            const source = income.source || 'Без источника';
            acc[source] = (acc[source] || 0) + income.amount;
            return acc;
        }, {} as Record<string, number>);
    }

    getIncomesInDateRange(incomes: Income[], startDate: Date, endDate: Date): Income[] {
        return incomes.filter(income => {
            const incomeDate = new Date(income.date);
            return incomeDate >= startDate && incomeDate <= endDate;
        });
    }

    formatDateForDisplay(isoDate: string): string {
        const date = new Date(isoDate);
        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();
        return `${day}.${month}.${year}`;
    }
}
