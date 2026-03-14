// app/components/data/users.ts

interface Users {
    [key: string]: string;
}

// Начальная база пользователей
let users: Users = {
    'user@example.com': 'password123',
    'test@test.com': '123456',
    'admin@smartwallet.com': 'admin123',
    'john.doe@email.com': 'john2024',
    'jane.smith@email.com': 'jane2024'
};

// Функция для получения всех пользователей
export const getUsers = (): Users => {
    return { ...users };
};

// Функция для проверки существования пользователя
export const userExists = (email: string): boolean => {
    return users.hasOwnProperty(email.toLowerCase());
};

// Функция для проверки пароля
export const checkPassword = (email: string, password: string): boolean => {
    const normalizedEmail = email.toLowerCase();
    return users[normalizedEmail] === password;
};

// Функция для добавления нового пользователя
export const addUser = (email: string, password: string): boolean => {
    const normalizedEmail = email.toLowerCase();
    if (!users[normalizedEmail]) {
        users[normalizedEmail] = password;
        return true;
    }
    return false;
};

// Функция для удаления пользователя
export const removeUser = (email: string): boolean => {
    const normalizedEmail = email.toLowerCase();
    if (users[normalizedEmail]) {
        delete users[normalizedEmail];
        return true;
    }
    return false;
};

// Функция для получения количества пользователей
export const getUserCount = (): number => {
    return Object.keys(users).length;
};