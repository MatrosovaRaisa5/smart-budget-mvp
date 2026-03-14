// app/components/data/warnings.ts

export interface Warning {
    id: number;
    text: string;
    category: string;
}

// Начальные данные предупреждений
let warnings: Warning[] = [
    {
        id: 1,
        text: 'Вы потратили 90% бюджета на',
        category: 'развлечения'
    },
    {
        id: 2,
        text: 'Вы потратили 75% бюджета на',
        category: 'продукты'
    },
    {
        id: 3,
        text: 'Вы потратили 50% бюджета на',
        category: 'транспорт'
    }
];

// Получить все предупреждения
export const getWarnings = (): Warning[] => {
    return [...warnings];
};

// Получить количество предупреждений
export const getWarningsCount = (): number => {
    return warnings.length;
};

// Удалить предупреждение по id
export const removeWarning = (id: number): boolean => {
    const initialLength = warnings.length;
    warnings = warnings.filter(w => w.id !== id);
    return warnings.length !== initialLength;
};

// Проверить, есть ли предупреждения
export const hasWarnings = (): boolean => {
    return warnings.length > 0;
};