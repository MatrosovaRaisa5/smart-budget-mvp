import { BaseProvider } from './base.provider';
import {
    SavingsGoal,
    CreateGoalRequest,
    UpdateGoalRequest,
    ContributionRequest,
    GetGoalsResponse,
    CreateGoalResponse,
    UpdateGoalResponse,
    ContributionResponse,
    DeleteGoalResponse
} from '../models/goals.types';

export class GoalsProvider extends BaseProvider {
    async getGoals(): Promise<GetGoalsResponse> {
        try {
            const response = await this.request<GetGoalsResponse>(
                '/goals',
                { method: 'GET' }
            );
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('401')) {
                throw new Error('Не удалось загрузить цели. Сессия истекла.');
            }
            this.handleError(error);
        }
    }

    async createGoal(data: CreateGoalRequest): Promise<CreateGoalResponse> {
        try {
            if (!data.name || data.name.trim() === '') {
                throw new Error('Название цели обязательно');
            }
            if (!data.targetAmount || data.targetAmount <= 0) {
                throw new Error('Сумма цели должна быть больше 0');
            }
            if (!data.deadline || !this.isValidDate(data.deadline)) {
                throw new Error('Некорректная дата (формат ДД.ММ.ГГГГ)');
            }

            const response = await this.request<CreateGoalResponse>(
                '/goals',
                {
                    method: 'POST',
                    body: {
                        name: data.name.trim(),
                        targetAmount: data.targetAmount,
                        savedAmount: data.savedAmount || 0,
                        deadline: data.deadline
                    }
                }
            );
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('400')) {
                throw new Error('Некорректные данные цели');
            }
            this.handleError(error);
        }
    }

    async updateGoal(goalId: number, data: UpdateGoalRequest): Promise<UpdateGoalResponse> {
        try {
            if (!goalId) throw new Error('ID цели не указан');
            if (!data.name || data.name.trim() === '') throw new Error('Название цели обязательно');
            if (!data.targetAmount || data.targetAmount <= 0) throw new Error('Сумма цели должна быть больше 0');
            if (!data.deadline || !this.isValidDate(data.deadline)) throw new Error('Некорректная дата');

            const response = await this.request<UpdateGoalResponse>(
                `/goals/${goalId}`,
                {
                    method: 'PUT',
                    body: {
                        name: data.name.trim(),
                        targetAmount: data.targetAmount,
                        savedAmount: data.savedAmount,
                        deadline: data.deadline
                    }
                }
            );
            return response;
        } catch (error) {
            this.handleError(error);
        }
    }

    async deleteGoal(goalId: number): Promise<DeleteGoalResponse> {
        try {
            if (!goalId) throw new Error('ID цели не указан');
            const response = await this.request<DeleteGoalResponse>(
                `/goals/${goalId}`,
                { method: 'DELETE' }
            );
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                throw new Error('Цель не найдена');
            }
            this.handleError(error);
        }
    }

    async addContribution(goalId: number, amount: number): Promise<ContributionResponse> {
        try {
            if (!goalId) throw new Error('ID цели не указан');
            if (!amount || amount <= 0) throw new Error('Сумма пополнения должна быть больше 0');

            const response = await this.request<ContributionResponse>(
                `/goals/${goalId}/contributions`,
                {
                    method: 'POST',
                    body: { amount }
                }
            );
            return response;
        } catch (error) {
            if (error instanceof Error && error.message.includes('404')) {
                throw new Error('Цель не найдена');
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
