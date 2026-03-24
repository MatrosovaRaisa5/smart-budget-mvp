export interface Category {
    id: number;
    name: string;
    global: boolean;
}

export type GetCategoriesResponse = Category[];

export interface CreateCategoryRequest {
    name: string;
}

export interface CreateCategoryResponse {
    id: number;
    name: string;
    global: boolean;
}

export type DeleteCategoryResponse = string;
