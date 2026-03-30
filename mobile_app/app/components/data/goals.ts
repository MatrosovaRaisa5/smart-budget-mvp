// app/components/data/goals.ts

export interface Goal {
    id: number;
    name: string;
    targetAmount: number;
    targetDate: string;
    currentAmount: number;
}

// Начальные данные целей
let goals: Goal[] = [
    {
        id: 1,
        name: 'Пылесос',
        targetAmount: 30000,
        targetDate: '30.06.2026',
        currentAmount: 5000
    },
    {
        id: 2,
        name: 'Путешествие',
        targetAmount: 100000,
        targetDate: '31.12.2026',
        currentAmount: 25000
    },
    {
        id: 3,
        name: 'Новый телефон',
        targetAmount: 50000,
        targetDate: '15.09.2026',
        currentAmount: 15000
    }
];

// Получить все цели
export const getGoals = (): Goal[] => {
    return [...goals];
};

// Получить цель по id
export const getGoalById = (id: number): Goal | undefined => {
    return goals.find(g => g.id === id);
};

// Добавить цель
export const addGoal = (goal: Omit<Goal, 'id'>): number => {
    const newId = Math.max(...goals.map(g => g.id), 0) + 1;
    const newGoal = { ...goal, id: newId };
    goals.push(newGoal);
    return newId;
};

// Обновить цель
export const updateGoal = (id: number, updates: Partial<Omit<Goal, 'id'>>): boolean => {
    const index = goals.findIndex(g => g.id === id);
    if (index !== -1) {
        goals[index] = { ...goals[index], ...updates };
        return true;
    }
    return false;
};

// Удалить цель
export const removeGoal = (id: number): boolean => {
    const initialLength = goals.length;
    goals = goals.filter(g => g.id !== id);
    return goals.length !== initialLength;
};

// Пополнить цель
export const depositToGoal = (id: number, amount: number): boolean => {
    const goal = goals.find(g => g.id === id);
    if (goal) {
        goal.currentAmount += amount;
        return true;
    }
    return false;
};