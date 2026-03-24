// providers/budget.provider.ts
import { BaseProvider } from './base.provider';
import {
    SetupBudgetRequest,
    SetupBudgetResponse,
    BudgetStatusResponse,
    BudgetAlertsResponse,
    BudgetCategoryStatus
} from '../models/budget.types';

export class BudgetProvider extends BaseProvider {
    async setupBudget(data: SetupBudgetRequest): Promise<SetupBudgetResponse> {
        try {
            if (!data.plannedIncome || data.plannedIncome <= 0) {
                throw new Error('Плановый доход должен быть больше 0');
            }

            if (!data.percentages || Object.keys(data.percentages).length === 0) {
                throw new Error('Не указаны проценты для категорий');
            }

            const totalPercent = Object.values(data.percentages).reduce((sum, p) => sum + p, 0);
            if (Math.abs(totalPercent - 100) > 0.01) {
                throw new Error(`Сумма процентов должна быть 100, а не ${totalPercent}`);
            }

            for (const [categoryId, percent] of Object.entries(data.percentages)) {
                if (percent < 0 || percent > 100) {
                    throw new Error(`Процент для категории ${categoryId} должен быть от 0 до 100`);
                }
            }

            const response = await this.request<SetupBudgetResponse>(
                '/budget/setup',
                {
                    method: 'POST',
                    body: {
                        plannedIncome: data.plannedIncome,
                        percentages: data.percentages
                    }
                }
            );
            return response;

        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('400')) {
                    throw new Error('Неверные данные бюджета. Проверьте проценты.');
                }
                if (error.message.includes('401')) {
                    throw new Error('Не удалось сохранить бюджет. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    async getBudgetStatus(): Promise<BudgetStatusResponse> {
        try {
            const response = await this.request<BudgetStatusResponse>(
                '/budget/status',
                { method: 'GET' }
            );
            return response;

        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('401')) {
                    throw new Error('Не удалось загрузить статус бюджета. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    async getAlerts(): Promise<BudgetAlertsResponse> {
        try {
            const response = await this.request<BudgetAlertsResponse>(
                '/budget/alerts',
                { method: 'GET' }
            );
            return response;

        } catch (error) {
            if (error instanceof Error) {
                if (error.message.includes('401')) {
                    throw new Error('Не удалось загрузить уведомления. Сессия истекла.');
                }
                throw error;
            }

            this.handleError(error);
        }
    }

    getRemainingForCategory(category: BudgetCategoryStatus): number {
        return category.remaining;
    }

    isCategoryOverLimit(category: BudgetCategoryStatus): boolean {
        return category.spent > category.limit;
    }

    getCategoryUsagePercent(category: BudgetCategoryStatus): number {
        return category.spentPercent;
    }

    getTotalUsagePercent(status: BudgetStatusResponse): number {
        if (status.categories.length === 0) return 0;

        const totalSpent = status.categories.reduce((sum, c) => sum + c.spent, 0);
        const totalLimit = status.categories.reduce((sum, c) => sum + c.limit, 0);

        return totalLimit > 0 ? (totalSpent / totalLimit) * 100 : 0;
    }

    sortCategoriesBySpentPercent(
        categories: BudgetCategoryStatus[],
        descending: boolean = true
    ): BudgetCategoryStatus[] {
        return [...categories].sort((a, b) =>
            descending
                ? b.spentPercent - a.spentPercent
                : a.spentPercent - b.spentPercent
        );
    }

    getHighRiskCategories(
        categories: BudgetCategoryStatus[],
        threshold: number = 80
    ): BudgetCategoryStatus[] {
        return categories.filter(c => c.spentPercent >= threshold);
    }

    getExhaustedCategories(categories: BudgetCategoryStatus[]): BudgetCategoryStatus[] {
        return categories.filter(c => c.remaining <= 0);
    }
}
