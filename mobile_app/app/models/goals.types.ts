export interface SavingsGoal {
    id: number;
    name: string;
    targetAmount: number;
    savedAmount: number;
    deadline: string; // format dd.MM.yyyy
    user?: {
        id: number;
        email: string;
        name: string;
    };
}

export interface CreateGoalRequest {
    name: string;
    targetAmount: number;
    savedAmount?: number;
    deadline: string;
}

export interface UpdateGoalRequest {
    name: string;
    targetAmount: number;
    savedAmount?: number;
    deadline: string;
}

export interface ContributionRequest {
    amount: number;
}

export type GetGoalsResponse = SavingsGoal[];
export type CreateGoalResponse = SavingsGoal;
export type UpdateGoalResponse = SavingsGoal;
export type ContributionResponse = SavingsGoal;
export type DeleteGoalResponse = string;
