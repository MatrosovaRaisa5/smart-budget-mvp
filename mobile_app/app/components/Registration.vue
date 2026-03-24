<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-register">
        <GridLayout rows="*" columns="*" backgroundColor="#13131A">

            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" justifyContent="center" alignItems="stretch"
                              paddingLeft="16" paddingRight="16" height="100%">

                    <GridLayout rows="auto" columns="*" backgroundColor="#1E1D2E" borderRadius="16"
                               paddingLeft="16" paddingRight="16" paddingTop="44" paddingBottom="44">

                        <StackLayout>
                            <!-- Заголовок -->
                            <Label text="Добро пожаловать!"
                                   class="title"
                                   horizontalAlignment="center" />

                            <!-- Подзаголовок с цветным словом Wallet -->
                            <StackLayout marginTop="24" horizontalAlignment="center">
                                <FlexboxLayout flexDirection="row" justifyContent="center" alignItems="center" flexWrap="wrap">
                                    <Label class="subtitle" text="Заполните данные о себе " />
                                    <Label class="subtitle" text="для создания аккаунта в " />
                                    <Label class="subtitle" text="Smart" color="white" />
                                    <Label class="subtitle" text="Wallet" color="#964BDC" />
                                </FlexboxLayout>
                            </StackLayout>

                            <!-- Поля ввода -->
                            <StackLayout marginTop="24">
                                <!-- ФИО поле (обязательное) -->
                                <GridLayout rows="auto, auto" columns="*">
                                    <GridLayout row="0" columns="16, *, auto"
                                               class="input-field"
                                               :class="{
                                                   'input-focused': focusedField === 'fullname',
                                                   'input-filled': form.fullname,
                                                   'input-error': errors.fullname
                                               }"
                                               paddingLeft="16" paddingRight="16">

                                        <Image col="0" src="~/assets/images/name.png"
                                               width="14" height="18" verticalAlignment="center" />

                                        <TextField col="1"
                                                  v-model="form.fullname"
                                                  hint="ФИО"
                                                  class="text-field"
                                                  :color="form.fullname ? 'white' : '#B4B4B4'"
                                                  @focus="focusedField = 'fullname'"
                                                  @blur="validateField('fullname')"
                                                  @textChange="validateField('fullname')"
                                                  autocorrect="false"
                                                  autocapitalizationType="words" />
                                    </GridLayout>

                                    <!-- Сообщение об ошибке для ФИО -->
                                    <Label row="1"
                                           v-if="errors.fullname"
                                           :text="errors.fullname"
                                           class="error-message"
                                           marginTop="12" />
                                </GridLayout>

                                <!-- Email поле -->
                                <GridLayout rows="auto, auto" columns="*" marginTop="20">
                                    <GridLayout row="0" columns="16, *, auto"
                                               class="input-field"
                                               :class="{
                                                   'input-focused': focusedField === 'email',
                                                   'input-filled': form.email,
                                                   'input-error': errors.email || showUserExistsModal
                                               }"
                                               paddingLeft="16" paddingRight="16">

                                        <Image col="0" src="~/assets/images/email.png"
                                               width="16" height="12" verticalAlignment="center" />

                                        <TextField col="1"
                                                  v-model="form.email"
                                                  hint="E-mail"
                                                  class="text-field"
                                                  :color="form.email ? 'white' : '#B4B4B4'"
                                                  @focus="focusedField = 'email'"
                                                  @blur="validateField('email')"
                                                  @textChange="validateField('email')"
                                                  autocorrect="false"
                                                  autocapitalizationType="none"
                                                  keyboardType="email" />
                                    </GridLayout>

                                    <!-- Сообщение об ошибке для Email -->
                                    <Label row="1"
                                           v-if="errors.email"
                                           :text="errors.email"
                                           class="error-message"
                                           marginTop="12" />
                                </GridLayout>

                                <!-- Пароль поле -->
                                <GridLayout rows="auto, auto" columns="*" marginTop="20">
                                    <GridLayout row="0" columns="16, *, 20"
                                               class="input-field"
                                               :class="{
                                                   'input-focused': focusedField === 'password',
                                                   'input-filled': form.password,
                                                   'input-error': errors.password
                                               }"
                                               paddingLeft="16" paddingRight="16">

                                        <Image col="0" src="~/assets/images/key.png"
                                               width="16" height="18" verticalAlignment="center" />

                                        <TextField col="1"
                                                  v-model="form.password"
                                                  :secure="!showPassword"
                                                  hint="Пароль"
                                                  class="text-field"
                                                  :color="form.password ? 'white' : '#B4B4B4'"
                                                  @focus="focusedField = 'password'"
                                                  @blur="validateField('password')"
                                                  @textChange="validateField('password')"
                                                  autocorrect="false"
                                                  autocapitalizationType="none" />

                                        <Image col="2"
                                              :src="showPassword ? '~/assets/images/openeyes.png' : '~/assets/images/closeeyes.png'"
                                              :width="showPassword ? 24 : 18"
                                              :height="showPassword ? 16 : 12"
                                              verticalAlignment="center"
                                              @tap="togglePasswordVisibility('password')" />
                                    </GridLayout>

                                    <!-- Сообщение об ошибке для пароля -->
                                    <Label row="1"
                                           v-if="errors.password"
                                           :text="errors.password"
                                           class="error-message"
                                           marginTop="12" />
                                </GridLayout>

                                <!-- Подтверждение пароля поле -->
                                <GridLayout rows="auto, auto" columns="*" marginTop="20">
                                    <GridLayout row="0" columns="16, *, 20"
                                               class="input-field"
                                               :class="{
                                                   'input-focused': focusedField === 'confirmPassword',
                                                   'input-filled': form.confirmPassword,
                                                   'input-error': errors.confirmPassword
                                               }"
                                               paddingLeft="16" paddingRight="16">

                                        <Image col="0" src="~/assets/images/key.png"
                                               width="16" height="18" verticalAlignment="center" />

                                        <TextField col="1"
                                                  v-model="form.confirmPassword"
                                                  :secure="!showConfirmPassword"
                                                  hint="Подтверждение пароля"
                                                  class="text-field"
                                                  :color="form.confirmPassword ? 'white' : '#B4B4B4'"
                                                  @focus="focusedField = 'confirmPassword'"
                                                  @blur="validateField('confirmPassword')"
                                                  @textChange="validateField('confirmPassword')"
                                                  autocorrect="false"
                                                  autocapitalizationType="none" />

                                        <Image col="2"
                                              :src="showConfirmPassword ? '~/assets/images/openeyes.png' : '~/assets/images/closeeyes.png'"
                                              :width="showConfirmPassword ? 24 : 18"
                                              :height="showConfirmPassword ? 16 : 12"
                                              verticalAlignment="center"
                                              @tap="togglePasswordVisibility('confirmPassword')" />
                                    </GridLayout>

                                    <!-- Сообщение об ошибке для подтверждения пароля -->
                                    <Label row="1"
                                           v-if="errors.confirmPassword"
                                           :text="errors.confirmPassword"
                                           class="error-message"
                                           marginTop="12" />
                                </GridLayout>

                                <!-- Чекбокс согласия с переносом текста -->
                                <GridLayout rows="auto" columns="auto, *" marginTop="24" marginBottom="24">
                                    <GridLayout row="0" col="0" width="18" height="18" marginLeft="14"
                                               :class="['checkbox', { 'checkbox-checked': isAgreed, 'checkbox-error': errors.agreement }]"
                                               borderRadius="6"
                                               @tap="toggleAgreement">
                                        <Label v-if="isAgreed" text="✓" color="white" fontSize="10"
                                               fontFamily="Inter" fontWeight="bold"
                                               horizontalAlignment="center" verticalAlignment="center" />
                                    </GridLayout>

                                    <StackLayout row="0" col="1" marginLeft="8">
                                        <FlexboxLayout flexDirection="row" flexWrap="wrap">
                                            <Label text="Я соглашаюсь с "
                                                   fontSize="12" fontFamily="Inter" fontWeight="600" color="white" />
                                            <Label text="политикой обработки"
                                                   fontSize="12" fontFamily="Inter" fontWeight="600" color="#964BDC"
                                                   @tap="openPrivacyPolicy" />
                                        </FlexboxLayout>
                                        <FlexboxLayout flexDirection="row" flexWrap="wrap">
                                            <Label text="персональных данных"
                                                   fontSize="12" fontFamily="Inter" fontWeight="600" color="#964BDC"
                                                   @tap="openPrivacyPolicy" />
                                        </FlexboxLayout>
                                    </StackLayout>
                                </GridLayout>

                                <!-- Кнопка регистрации -->
                                <Button :text="'Зарегистрироваться'"
                                       :class="['login-button', isFormValid ? 'active' : 'inactive']"
                                       :isEnabled="isFormValid"
                                       @tap="onRegister"/>

                                <!-- Вход для существующих пользователей -->
                                <FlexboxLayout flexDirection="row" justifyContent="center" alignItems="center"
                                               marginTop="24">
                                    <Label text="Уже есть аккаунт?"
                                           color="white" fontSize="14" fontFamily="Inter" fontWeight="600" />
                                    <Label text="Войти"
                                           color="#964BDC" fontSize="14" fontFamily="Inter" fontWeight="600"
                                           marginLeft="32"
                                           @tap="goToLogin" />
                                </FlexboxLayout>
                            </StackLayout>
                        </StackLayout>
                    </GridLayout>
                </FlexboxLayout>
            </ScrollView>

            <!-- Затемняющий фон (поверх всего) - ТОЛЬКО ДЛЯ ОШИБКИ -->
            <GridLayout v-if="showUserExistsModal || showRegisterErrorModal"
                        row="0" col="0" rows="*" columns="*"
                        backgroundColor="#818181" opacity="0.64"
                        @tap="closeAllModals"
                        zIndex="1000" />

            <!-- Модальное окно "Пользователь уже существует" (поверх фона) -->
            <GridLayout v-if="showUserExistsModal" row="0" col="0" rows="auto" columns="auto"
                       horizontalAlignment="center" verticalAlignment="center"
                       @tap="closeUserExistsModal"
                       zIndex="1001">
                <StackLayout backgroundColor="white" borderRadius="15"
                            paddingLeft="20" paddingRight="20"
                            paddingTop="20" paddingBottom="20">

                    <Image src="~/assets/images/wow.png"
                           width="36" height="36"
                           horizontalAlignment="center" />

                    <Label text="Пользователь уже существует"
                           fontSize="18" fontFamily="Inter" fontWeight="600"
                           color="#FF0000" lineHeight="27"
                           textWrap="true"
                           horizontalAlignment="center"
                           marginTop="12" />
                </StackLayout>
            </GridLayout>

            <!-- Модальное окно "Ошибка регистрации" -->
            <GridLayout v-if="showRegisterErrorModal" row="0" col="0" rows="auto" columns="auto"
                    horizontalAlignment="center" verticalAlignment="center"
                    @tap="closeRegisterErrorModal"
                    zIndex="1001">
                <StackLayout class="bg-white rounded-[32px] px-5 py-5">

                    <Image src="~/assets/images/sad.png"
                        width="36" height="36"
                        class="text-center" />

                    <Label text="Ошибка регистрации"
                        class="text-[#FF0000] font-inter font-semibold text-[18px] leading-[27px] text-center mt-3"
                        textWrap="true" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue'
import { $navigateTo } from 'nativescript-vue';
import { AuthProvider } from '../providers/auth.provider'
import MyBudget from './MyBudget.vue'
import Login from './Login.vue'
import {
    RegistrationFormData,
    RegistrationErrors,
    RegistrationTouched
} from '../models/form.types'
import { RegistrationValidator } from '../validators/registration.validator'

export default defineComponent({
    data() {
        return {
            auth: new AuthProvider(),

            form: {
                fullname: '',
                email: '',
                password: '',
                confirmPassword: ''
            } as RegistrationFormData,

            focusedField: null as string | null,

            showPassword: false,
            showConfirmPassword: false,

            isAgreed: false,
            showUserExistsModal: false,
            showRegisterErrorModal: false,

            isSuccess: false,
            isLoading: false,

            errors: {
                fullname: '',
                email: '',
                password: '',
                confirmPassword: '',
                agreement: ''
            } as RegistrationErrors,

            touched: {
                fullname: false,
                email: false,
                password: false,
                confirmPassword: false,
                agreement: false
            } as RegistrationTouched
        }
    },

    computed: {
        isFormValid(): boolean {
            const noErrors =
                !this.errors.fullname &&
                !this.errors.email &&
                !this.errors.password &&
                !this.errors.confirmPassword

            const allFieldsFilled =
                this.form.fullname.trim() !== '' &&
                this.form.email.trim() !== '' &&
                this.form.password.trim() !== '' &&
                this.form.confirmPassword.trim() !== '' &&
                this.isAgreed

            return noErrors && allFieldsFilled
        }
    },

    methods: {

        togglePasswordVisibility(field: string): void {
            if (field === 'password') {
                this.showPassword = !this.showPassword
            } else {
                this.showConfirmPassword = !this.showConfirmPassword
            }
        },

        toggleAgreement(): void {
            this.isAgreed = !this.isAgreed
            this.touched.agreement = true
            if (this.isAgreed) {
                this.errors.agreement = ''
            }
        },

        closeAllModals(): void {
            this.showUserExistsModal = false
            this.showRegisterErrorModal = false
        },

        closeUserExistsModal(): void {
            this.showUserExistsModal = false
        },

        closeRegisterErrorModal(): void {
            this.showRegisterErrorModal = false
        },

        validateField(field: keyof RegistrationFormData): void {
            if (this.isSuccess) return

            this.touched[field] = true
            switch (field) {
                case 'fullname':
                    this.errors.fullname =
                        RegistrationValidator.validateFullname(this.form.fullname)
                    break
                case 'email':
                    this.errors.email =
                        RegistrationValidator.validateEmail(this.form.email)
                    break
                case 'password':
                    this.errors.password =
                        RegistrationValidator.validatePassword(this.form.password)
                    if (this.form.confirmPassword) {
                        this.errors.confirmPassword =
                            RegistrationValidator.validateConfirmPassword(
                                this.form.password,
                                this.form.confirmPassword
                            )
                    }
                    break
                case 'confirmPassword':
                    this.errors.confirmPassword =
                        RegistrationValidator.validateConfirmPassword(
                            this.form.password,
                            this.form.confirmPassword
                        )
                    break
            }
        },

        validateAllFields(): boolean {
            Object.keys(this.touched).forEach(key => {
                this.touched[key as keyof RegistrationTouched] = true
            })
            this.errors = {
                ...RegistrationValidator.validateForm(this.form, this.isAgreed),
            }

            return (
                !this.errors.fullname &&
                !this.errors.email &&
                !this.errors.password &&
                !this.errors.confirmPassword &&
                !this.errors.agreement
            )
        },

        async onRegister(): Promise<void> {
            if (!this.validateAllFields()) return

            this.isLoading = true

            const email = this.form.email.trim()
            const password = this.form.password
            const name = this.form.fullname.trim()

            try {
                await this.auth.register({ email, password, name })

                const loginResponse = await this.auth.login({ email, password })

                this.isSuccess = true
                this.form = {
                    fullname: '',
                    email: '',
                    password: '',
                    confirmPassword: ''
                }
                this.isAgreed = false

                setTimeout(() => {
                    $navigateTo(MyBudget, {
                        transition: { name: 'slideLeft', duration: 300 },
                        clearHistory: true
                    })
                }, 2000)

            } catch (error) {
                this.isSuccess = false

                if (error instanceof Error) {
                    const errorMessage = error.message.toLowerCase()

                    if (errorMessage.includes('email already in use') ||
                        errorMessage.includes('already registered')) {
                        this.showUserExistsModal = true
                    }
                    else if (errorMessage.includes('invalid credentials') ||
                            errorMessage.includes('неверный email или пароль')) {
                        setTimeout(() => {
                            $navigateTo(Login, {
                                transition: { name: 'fade', duration: 300 }
                            })
                        }, 1500)
                    } else {
                        this.showRegisterErrorModal = true
                    }
                } else {
                    this.showRegisterErrorModal = true
                }
            } finally {
                this.isLoading = false
            }
        },

        openPrivacyPolicy(): void {
            console.log('Open privacy policy')
        },

        goToLogin(): void {
            $navigateTo(Login, { transition: { name: 'fade', duration: 300 } })
        }
    }
})
</script>

<style scoped>
.page-register {
    background-color: #13131A;
}

.input-field {
    background-color: #2F2D44;
    border-radius: 16;
    height: 56;
    align-items: center;
    border-width: 2;
    border-color: #2F2D44;
}

.input-field GridLayout {
    align-items: center;
}

.input-focused {
    border-color: #964BDC;
}

.input-filled {
    border-color: #964BDC;
}

.input-error {
    border-color: #FF0000;
}

.text-field {
    placeholder-color: #B4B4B4;
    background-color: transparent;
    font-size: 14;
    font-family: 'Inter';
    font-weight: 600;
    padding: 0;
    margin-left: 16;
    height: 56;
    text-align: left;
    vertical-align: middle;
    width: 100%;
    color: white;
}

.text-field TextField {
    font-size: 14;
    font-family: 'Inter';
    font-weight: 600;
}

.title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 24;
    line-height: 36;
    text-align: center;
}

.subtitle {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 18;
    line-height: 27;
    text-align: center;
}

.error-message {
    color: #FF0000;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-left: 16;
}

.checkbox {
    background-color: #2F2D44;
    border-width: 2;
    border-color: #964BDC;
}

.checkbox-checked {
    background-color: #964BDC;
    border-color: #964BDC;
}

.checkbox-error {
    border-color: #FF0000;
}

.login-button {
    height: 56;
    border-radius: 16;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
    color: white;
}

.login-button.inactive {
    background-color: #969696;
}

.login-button.active {
    background-color: #964BDC;
}
</style>
