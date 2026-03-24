export interface SetupBudgetRequest {
    plannedIncome: number;
    percentages: Record<string, number>;
}

export type SetupBudgetResponse = string;

export interface BudgetCategoryStatus {
    categoryId: number;
    categoryName: string;
    limit: number;
    spent: number;
    remaining: number;
    percent: number;
    spentPercent: number;
}

export interface BudgetStatusResponse {
    plannedIncome: number;
    actualIncome: number;
    categories: BudgetCategoryStatus[];
}

export type BudgetAlertsResponse = string[];
