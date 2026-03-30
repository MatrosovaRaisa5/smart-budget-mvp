// app/components/data/transaction-data.ts

export interface Transaction {
    id: number;
    name: string;
    amount: number;
    date: string;
    categoryId: number;
}

// Данные транзакций для разных категорий
export const transactionsData: Transaction[] = [
    // Продукты (categoryId: 1)
    { id: 1, name: 'Магнит', amount: 1500, date: '15.03.2026', categoryId: 1 },
    { id: 2, name: 'Пятерочка', amount: 2300, date: '14.03.2026', categoryId: 1 },
    { id: 3, name: 'Перекресток', amount: 4500, date: '10.03.2026', categoryId: 1 },
    { id: 4, name: 'Ашан', amount: 3200, date: '08.03.2026', categoryId: 1 },
    { id: 5, name: 'Лента', amount: 1800, date: '05.03.2026', categoryId: 1 },
];

// Функция для получения транзакций по категории
export const getTransactionsByCategory = (categoryId: number): Transaction[] => {
    return transactionsData.filter(t => t.categoryId === categoryId);
};

// Функция для добавления новой транзакции
export const addTransaction = (transaction: Omit<Transaction, 'id'>): Transaction => {
    const newId = Math.max(...transactionsData.map(t => t.id), 0) + 1;
    const newTransaction = { ...transaction, id: newId };
    transactionsData.push(newTransaction);
    return newTransaction;
};

// Функция для удаления транзакции
export const deleteTransaction = (id: number): boolean => {
    const index = transactionsData.findIndex(t => t.id === id);
    if (index !== -1) {
        transactionsData.splice(index, 1);
        return true;
    }
    return false;
};