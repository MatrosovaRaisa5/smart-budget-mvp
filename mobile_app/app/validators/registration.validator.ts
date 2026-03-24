import { RegistrationFormData, RegistrationErrors } from '../models/form.types'

export class RegistrationValidator {

    static validateFullname(name: string): string {
        if (!name.trim()) {
            return 'Заполните обязательное поле'
        }
        return ''
    }

    static validateEmail(email: string): string {
        if (!email.trim()) {
            return 'Заполните обязательное поле'
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!emailRegex.test(email)) {
            return 'Некорректный E-mail'
        }
        return ''
    }

    static validatePassword(password: string): string {
        if (!password.trim()) {
            return 'Заполните обязательное поле'
        }
        if (password.length < 6) {
            return 'Пароль должен быть не менее 6 символов'
        }
        return ''
    }

    static validateConfirmPassword(password: string, confirmPassword: string): string {
        if (!confirmPassword.trim()) {
            return 'Заполните обязательное поле'
        }
        if (password !== confirmPassword) {
            return 'Пароли не совпадают'
        }
        return ''
    }

    static validateForm(form: RegistrationFormData, isAgreed: boolean): RegistrationErrors {
        return {
            fullname: this.validateFullname(form.fullname),
            email: this.validateEmail(form.email),
            password: this.validatePassword(form.password),
            confirmPassword: this.validateConfirmPassword(form.password, form.confirmPassword),
            agreement: isAgreed ? '' : 'error',
        }
    }
}
