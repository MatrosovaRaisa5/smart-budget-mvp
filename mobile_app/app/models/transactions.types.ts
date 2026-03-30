export interface CategoryInfo {
    id: number;
    name: string;
}

export interface Transaction {
    id: number;
    amount: number;
    description: string;
    date: string;
    category: CategoryInfo;
    user?: {
        id: number;
        email: string;
        name: string;
    };
}

export interface CreateTransactionRequest {
    amount: number;
    description: string;
    categoryId: number;
    date: string;
}

export interface TransactionFilters {
    categoryId?: number;
    fromDate?: string;
    toDate?: string;
}

export type DeleteTransactionResponse = string;
