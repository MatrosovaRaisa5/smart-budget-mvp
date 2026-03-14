// app/components/data/categories-list.ts

export interface Category {
    id: number;
    name: string;
    icon: string;
    iconSize?: {
        width: number;
        height: number;
    };
}

// Полный список всех доступных категорий (ТОЛЬКО ДЛЯ ВЫБОРА)
let categoriesList: Category[] = [
    {
        id: 1,
        name: 'Продукты',
        icon: 'shop',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 2,
        name: 'Транспорт',
        icon: 'transport',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 3,
        name: 'Развлечения',
        icon: 'entertainment',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 4,
        name: 'Здоровье',
        icon: 'health',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 5,
        name: 'Образование',
        icon: 'education',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 6,
        name: 'Одежда',
        icon: 'clothes',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 7,
        name: 'Семья',
        icon: 'family',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 8,
        name: 'Кафе и рестораны',
        icon: 'restaurant',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 9,
        name: 'Спорт',
        icon: 'sport',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 10,
        name: 'Путешествия',
        icon: 'travel',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 11,
        name: 'Подарки',
        icon: 'gift',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 12,
        name: 'Животные',
        icon: 'pet',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 13,
        name: 'Красота',
        icon: 'beauty',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 14,
        name: 'Техника',
        icon: 'tech',
        iconSize: { width: 48, height: 48 }
    },
    {
        id: 15,
        name: 'Дом',
        icon: 'home',
        iconSize: { width: 48, height: 48 }
    }
];

// Функции для работы с категориями (ТОЛЬКО ДЛЯ ВЫБОРА)
export const getAllCategories = (): Category[] => {
    return [...categoriesList];
};

export const getCategoryById = (id: number): Category | undefined => {
    return categoriesList.find(c => c.id === id);
};

export const addCategory = (category: Omit<Category, 'id'>): number => {
    const newId = Math.max(...categoriesList.map(c => c.id), 0) + 1;
    const newCategory = { ...category, id: newId };
    categoriesList.push(newCategory);
    return newId;
};

export const searchCategories = (query: string): Category[] => {
    if (!query) return [...categoriesList];
    
    const searchLower = query.toLowerCase();
    return categoriesList.filter(cat => 
        cat.name.toLowerCase().includes(searchLower)
    );
};