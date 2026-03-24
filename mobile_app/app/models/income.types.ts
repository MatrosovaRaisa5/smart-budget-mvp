export interface CreateIncomeRequest {
    source?: string;
    amount: number;
    date: string;
}

export interface Income {
    id: number;
    source: string;
    amount: number;
    date: string;
    user: {
        id: number;
        email: string;
        name: string;
    };
}

export type CreateIncomeResponse = Income;
export type GetIncomesResponse = Income[];
export type DeleteIncomeResponse = string;
