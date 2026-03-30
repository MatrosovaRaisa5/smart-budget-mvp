// app/components/data/budget.ts

export interface Income {
    id: number;
    amount: number;
    source: string;        // Название источника дохода (Зарплата, Фриланс и т.д.)
    date: string;          // Дата в формате DD.MM.YYYY
}

export interface Expense {
    id: number;
    category: string;
    transactionCount: number;
    spentPercent: number;
    spentAmount: number;
    remainingAmount: number;
    icon: string;
}

// Начальные данные доходов с новыми полями
let incomes: Income[] = [
    {
        id: 1,
        amount: 50000,
        source: 'Зарплата',
        date: '01.03.2026'
    },
    {
        id: 2,
        amount: 30000,
        source: 'Фриланс',
        date: '05.03.2026'
    },
    {
        id: 3,
        amount: 15000,
        source: 'Кэшбэк',
        date: '10.03.2026'
    }
];

// Начальные данные расходов с иконками
let expenses: Expense[] = [
    {
        id: 1,
        category: 'Продукты',
        transactionCount: 3,
        spentPercent: 83,
        spentAmount: 8300,
        remainingAmount: 2500,
        icon: 'shop'
    },
    {
        id: 2,
        category: 'Семья',
        transactionCount: 5,
        spentPercent: 45,
        spentAmount: 4500,
        remainingAmount: 5500,
        icon: 'family'
    },
    {
        id: 3,
        category: 'Одежда',
        transactionCount: 2,
        spentPercent: 53,
        spentAmount: 9000,
        remainingAmount: 1000,
        icon: 'clothes'
    }
];

// Функции для работы с доходами
export const getIncomes = (): Income[] => {
    return [...incomes];
};

export const getTotalIncome = (): number => {
    return incomes.reduce((total, income) => total + income.amount, 0);
};

export const getTotalIncomeSources = (): number => {
    return incomes.length;
};

export const getIncomeById = (id: number): Income | undefined => {
    return incomes.find(i => i.id === id);
};

export const addIncome = (income: Omit<Income, 'id'>): number => {
    const newId = Math.max(...incomes.map(i => i.id), 0) + 1;
    const newIncome = { ...income, id: newId };
    incomes.push(newIncome);
    return newId;
};

export const removeIncome = (id: number): boolean => {
    const initialLength = incomes.length;
    incomes = incomes.filter(i => i.id !== id);
    return incomes.length !== initialLength;
};

export const updateIncome = (id: number, updates: Partial<Income>): boolean => {
    const index = incomes.findIndex(i => i.id === id);
    if (index !== -1) {
        incomes[index] = { ...incomes[index], ...updates };
        return true;
    }
    return false;
};

// Функции для работы с расходами
export const getExpenses = (): Expense[] => {
    return [...expenses];
};

export const getTotalExpenses = (): number => {
    return expenses.reduce((total, expense) => total + expense.spentAmount, 0);
};

export const getExpenseById = (id: number): Expense | undefined => {
    return expenses.find(e => e.id === id);
};

export const addExpense = (expense: Omit<Expense, 'id'>): number => {
    const newId = Math.max(...expenses.map(e => e.id), 0) + 1;
    const newExpense = { ...expense, id: newId };
    expenses.push(newExpense);
    return newId;
};

export const removeExpense = (id: number): boolean => {
    const initialLength = expenses.length;
    expenses = expenses.filter(e => e.id !== id);
    return expenses.length !== initialLength;
};

export const updateExpense = (id: number, updates: Partial<Expense>): boolean => {
    const index = expenses.findIndex(e => e.id === id);
    if (index !== -1) {
        expenses[index] = { ...expenses[index], ...updates };
        return true;
    }
    return false;
};

// Функция для получения цвета статус-бара в зависимости от процента
export const getStatusBarColor = (percent: number): string => {
    if (percent < 50) return '#A2E809';
    if (percent <= 75) return '#FF5E00';
    return '#FF0000';
};

// Функция для проверки наличия данных
export const hasAnyData = (): boolean => {
    return incomes.length > 0 || expenses.length > 0;
};

export const hasIncomes = (): boolean => {
    return incomes.length > 0;
};

export const hasExpenses = (): boolean => {
    return expenses.length > 0;
};

// Функция для получения доступных иконок для расходов
export const getAvailableExpenseIcons = (): string[] => {
    return ['shop', 'family', 'clothes', 'transport', 'entertainment', 'food', 'health', 'education'];
};

// Функция для получения списка категорий расходов
export const getExpenseCategories = (): string[] => {
    return expenses.map(e => e.category);
};
