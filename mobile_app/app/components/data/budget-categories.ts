// app/components/data/budget-categories.ts

export interface BudgetCategory {
    id: number;
    name: string;
    icon: string;
    percent: string;
    focused?: boolean;
}

// Категории для распределения бюджета (ТОЛЬКО ДЛЯ НАСТРОЕК)
let budgetCategories: BudgetCategory[] = [
    {
        id: 1,
        name: 'Продукты',
        icon: 'shop',
        percent: '30'
    },
    {
        id: 2,
        name: 'Транспорт',
        icon: 'transport',
        percent: '20'
    },
    {
        id: 3,
        name: 'Развлечения',
        icon: 'entertainment',
        percent: '15'
    }
];

// Функции для работы с категориями распределения (ТОЛЬКО ДЛЯ НАСТРОЕК)
export const getBudgetCategories = (): BudgetCategory[] => {
    return budgetCategories.map(cat => ({
        ...cat,
        focused: false
    }));
};

export const addBudgetCategory = (category: Omit<BudgetCategory, 'id'>): number => {
    const newId = Math.max(...budgetCategories.map(c => c.id), 0) + 1;
    const newCategory = { ...category, id: newId, focused: false };
    budgetCategories.push(newCategory);
    return newId;
};

export const removeBudgetCategory = (id: number): boolean => {
    const initialLength = budgetCategories.length;
    budgetCategories = budgetCategories.filter(c => c.id !== id);
    return budgetCategories.length !== initialLength;
};

export const updateBudgetCategoryPercent = (id: number, percent: string): boolean => {
    const index = budgetCategories.findIndex(c => c.id === id);
    if (index !== -1) {
        budgetCategories[index].percent = percent;
        return true;
    }
    return false;
};

export const getTotalPercent = (): number => {
    return budgetCategories.reduce((sum, cat) => sum + parseFloat(cat.percent), 0);
};

export const isDistributionValid = (): boolean => {
    return Math.abs(getTotalPercent() - 100) < 0.01;
};