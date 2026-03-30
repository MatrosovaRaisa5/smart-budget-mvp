// app/components/data/transactions.ts

export interface Transaction {
    id: number;
    name: string;
    amount: number;
    date: string;
    categoryId: number;
    categoryName?: string;
}

// Данные транзакций
export let transactionsData: Transaction[] = [
    // Сегодняшние (30.03.2026)
    { id: 1, name: 'Магнит', amount: 1500, date: '30.03.2026', categoryId: 1, categoryName: 'Продукты' },
    { id: 2, name: 'Кофе', amount: 350, date: '30.03.2026', categoryId: 2, categoryName: 'Кафе' },
    { id: 3, name: 'Такси', amount: 500, date: '30.03.2026', categoryId: 3, categoryName: 'Транспорт' },
    
    // Вчерашние (29.03.2026)
    { id: 4, name: 'Пятерочка', amount: 2300, date: '29.03.2026', categoryId: 1, categoryName: 'Продукты' },
    { id: 5, name: 'Кино', amount: 800, date: '29.03.2026', categoryId: 4, categoryName: 'Развлечения' },
    { id: 6, name: 'Аптека', amount: 1200, date: '29.03.2026', categoryId: 5, categoryName: 'Здоровье' },
    
    // Ранее (более старые)
    { id: 7, name: 'Перекресток', amount: 4500, date: '25.03.2026', categoryId: 1, categoryName: 'Продукты' },
    { id: 8, name: 'Ашан', amount: 3200, date: '22.03.2026', categoryId: 1, categoryName: 'Продукты' },
    { id: 9, name: 'Лента', amount: 1800, date: '20.03.2026', categoryId: 1, categoryName: 'Продукты' },
    { id: 10, name: 'Ozon', amount: 5000, date: '18.03.2026', categoryId: 6, categoryName: 'Покупки' },
    { id: 11, name: 'Wildberries', amount: 3200, date: '15.03.2026', categoryId: 6, categoryName: 'Покупки' },
    { id: 12, name: 'Яндекс.Еда', amount: 1200, date: '12.03.2026', categoryId: 2, categoryName: 'Кафе' }
];

// Получить все транзакции
export const getAllTransactions = (): Transaction[] => {
    return [...transactionsData];
};

// Добавить новую транзакцию
export const addTransaction = (transaction: Omit<Transaction, 'id'>): Transaction => {
    const newId = Math.max(...transactionsData.map(t => t.id), 0) + 1;
    const newTransaction = { ...transaction, id: newId };
    transactionsData.push(newTransaction);
    return newTransaction;
};

// Удалить транзакцию
export const deleteTransaction = (id: number): boolean => {
    const index = transactionsData.findIndex(t => t.id === id);
    if (index !== -1) {
        transactionsData.splice(index, 1);
        return true;
    }
    return false;
};

// Поиск транзакций по названию
export const searchTransactions = (query: string): Transaction[] => {
    if (!query) return [...transactionsData];
    const searchLower = query.toLowerCase();
    return transactionsData.filter(t => 
        t.name.toLowerCase().includes(searchLower) ||
        (t.categoryName && t.categoryName.toLowerCase().includes(searchLower))
    );
};

// Группировка транзакций по датам
export const groupTransactionsByDate = (transactions: Transaction[]) => {
    const today = new Date();
    const todayStr = `${today.getDate().toString().padStart(2, '0')}.${(today.getMonth() + 1).toString().padStart(2, '0')}.${today.getFullYear()}`;
    
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayStr = `${yesterday.getDate().toString().padStart(2, '0')}.${(yesterday.getMonth() + 1).toString().padStart(2, '0')}.${yesterday.getFullYear()}`;
    
    const todayTransactions: Transaction[] = [];
    const yesterdayTransactions: Transaction[] = [];
    const earlierTransactions: Transaction[] = [];
    
    transactions.forEach(t => {
        if (t.date === todayStr) {
            todayTransactions.push(t);
        } else if (t.date === yesterdayStr) {
            yesterdayTransactions.push(t);
        } else {
            earlierTransactions.push(t);
        }
    });
    
    return {
        today: todayTransactions,
        yesterday: yesterdayTransactions,
        earlier: earlierTransactions
    };
};