<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#13131A]">
        <GridLayout rows="*" columns="*" class="bg-[#13131A]">

            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" justifyContent="center" alignItems="stretch"
                              class="px-4 h-full">

                    <GridLayout rows="auto" columns="*" class="bg-[#1E1D2E] rounded-[32px] px-4 pt-11 pb-11">

                        <StackLayout>
                            <!-- Заголовок -->
                            <Label text="С возвращением!"
                                   class="text-white font-inter font-bold text-[24px] leading-[36px] text-center" />

                            <!-- Подзаголовок с цветным словом Wallet -->
                            <StackLayout class="mt-6 text-center">
                                <FlexboxLayout flexDirection="row" justifyContent="center" alignItems="center" flexWrap="wrap">
                                    <Label class="text-white font-inter font-semibold text-[18px] leading-[27px]" text="Войдите в свой аккаунт для работы с " />
                                    <Label class="text-white font-inter font-semibold text-[18px] leading-[27px]" text="Smart" />
                                    <Label class="text-[#964BDC] font-inter font-semibold text-[18px] leading-[27px]" text="Wallet" />
                                </FlexboxLayout>
                            </StackLayout>

                            <!-- Поля ввода -->
                            <StackLayout class="mt-6">
                                <!-- Email поле -->
                                <StackLayout>
                                    <GridLayout rows="auto" columns="16, *, auto"
                                               class="bg-[#2F2D44] rounded-[32px] h-14 items-center border-5 px-4"
                                               :class="[
                                                   focusedField === 'email' ? 'border-[#964BDC]' : 'border-[#2F2D44]',
                                                   errors.email && touched.email ? 'border-[#FF0000]' : ''
                                               ]">

                                        <Image col="0" src="~/assets/images/email.png"
                                               width="16" height="12" class="align-middle" />

                                        <TextField col="1"
                                                  v-model="form.email"
                                                  hint="E-mail"
                                                  class="text-white font-inter font-semibold text-[14px] bg-transparent p-0 ml-4 h-14 text-left align-middle placeholder-[#B4B4B4]"
                                                  :color="form.email ? 'white' : '#B4B4B4'"
                                                  @focus="focusedField = 'email'"
                                                  @blur="onBlur('email')"
                                                  @textChange="onEmailChange"
                                                  autocorrect="false"
                                                  autocapitalizationType="none" />
                                    </GridLayout>
                                    <Label v-if="errors.email && touched.email"
                                           :text="errors.email"
                                           class="text-[#FF0000] font-inter text-[12px] ml-4 mt-1" />
                                </StackLayout>

                                <!-- Пароль поле -->
                                <StackLayout class="mt-5">
                                    <GridLayout rows="auto" columns="16, *, 20"
                                               class="bg-[#2F2D44] rounded-[32px] h-14 items-center border-5 px-4"
                                               :class="[
                                                   focusedField === 'password' ? 'border-[#964BDC]' : 'border-[#2F2D44]',
                                                   errors.password && touched.password ? 'border-[#FF0000]' : ''
                                               ]">

                                        <Image col="0" src="~/assets/images/key.png"
                                               width="16" height="18" class="align-middle" />

                                        <TextField col="1"
                                                  v-model="form.password"
                                                  :secure="!showPassword"
                                                  hint="Пароль"
                                                  class="text-white font-inter font-semibold text-[14px] bg-transparent p-0 ml-4 h-14 text-left align-middle placeholder-[#B4B4B4]"
                                                  color="white"
                                                  @focus="focusedField = 'password'"
                                                  @blur="onBlur('password')"
                                                  @textChange="onPasswordChange"
                                                  autocorrect="false"
                                                  autocapitalizationType="none" />

                                        <Image col="2"
                                              :src="showPassword ? '~/assets/images/openeyes.png' : '~/assets/images/closeeyes.png'"
                                              :width="showPassword ? 24 : 18"
                                              :height="showPassword ? 16 : 12"
                                              class="align-middle"
                                              @tap="togglePasswordVisibility" />
                                    </GridLayout>
                                    <Label v-if="errors.password && touched.password"
                                           :text="errors.password"
                                           class="text-[#FF0000] font-inter text-[12px] ml-4 mt-1" />
                                </StackLayout>

                                <!-- Забыли пароль (как ссылка) -->
                                <Label text="Забыли пароль?"
                                       class="text-white font-inter font-semibold text-[14px] leading-[14px] text-right mt-4 mb-2"
                                       @tap="onForgotPassword"/>

                                <!-- Индикатор загрузки или кнопка входа -->
                                <StackLayout v-if="isLoading" class="mt-2">
                                    <ActivityIndicator :busy="true" color="#964BDC" />
                                </StackLayout>

                                <!-- Кнопка входа -->
                                <Button v-else
                                       :text="'Войти'"
                                       :class="['h-14 rounded-[32px] font-inter font-bold text-[18px] text-white w-full mt-2',
                                                isFormValid && !isLoading ? 'bg-[#964BDC]' : 'bg-[#969696]']"
                                       :isEnabled="isFormValid && !isLoading"
                                       @tap="onLogin"/>

                                <!-- Регистрация -->
                                <FlexboxLayout flexDirection="row" justifyContent="center" alignItems="center"
                                               class="mt-6">
                                    <Label text="Нет аккаунта?"
                                           class="text-white font-inter font-semibold text-[14px]" />
                                    <Label text="Зарегистрироваться"
                                           class="text-[#964BDC] font-inter font-semibold text-[14px] ml-8"
                                           @tap="goToRegistration" />
                                </FlexboxLayout>
                            </StackLayout>
                        </StackLayout>
                    </GridLayout>
                </FlexboxLayout>
            </ScrollView>

            <!-- Затемняющий фон (поверх всего) - общий для всех ошибок -->
            <GridLayout v-if="showInvalidCredentialsModal || showLoginErrorModal"
                       row="0" col="0" rows="*" columns="*"
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAllModals"
                       zIndex="1000" />

            <!-- Модальное окно "Неверный логин или пароль" -->
            <GridLayout v-if="showInvalidCredentialsModal" row="0" col="0" rows="auto" columns="auto"
                       horizontalAlignment="center" verticalAlignment="center"
                       @tap="closeInvalidCredentialsModal"
                       zIndex="1001">
                <StackLayout class="bg-white rounded-[32px] px-5 py-5">

                    <Image src="~/assets/images/sad.png"
                           width="36" height="36"
                           class="text-center" />

                    <Label text="Неверный логин или пароль"
                           class="text-[#FF0000] font-inter font-semibold text-[18px] leading-[27px] text-center mt-3"
                           textWrap="true" />
                </StackLayout>
            </GridLayout>

            <!-- Модальное окно "Ошибка входа" -->
            <GridLayout v-if="showLoginErrorModal" row="0" col="0" rows="auto" columns="auto"
                       horizontalAlignment="center" verticalAlignment="center"
                       @tap="closeLoginErrorModal"
                       zIndex="1001">
                <StackLayout class="bg-white rounded-[32px] px-5 py-5">

                    <Image src="~/assets/images/sad.png"
                           width="36" height="36"
                           class="text-center" />

                    <Label text="Ошибка входа"
                           class="text-[#FF0000] font-inter font-semibold text-[18px] leading-[27px] text-center mt-3"
                           textWrap="true" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { AuthProvider } from '../providers/auth.provider';
import { LoginValidator } from '../validators/login.validator';
import { LoginErrors, LoginTouched } from '~/models/form.types';
import MyBudget from './MyBudget.vue';
import Registration from './Registration.vue';

export default defineComponent({
    data() {
        return {
            auth: new AuthProvider(),
            form: {
                email: '',
                password: ''
            },
            focusedField: null as string | null,
            showPassword: false,
            showInvalidCredentialsModal: false,
            showLoginErrorModal: false,
            isLoading: false,

            errors: {
                email: '',
                password: ''
            } as LoginErrors,

            touched: {
                email: false,
                password: false
            } as LoginTouched
        };
    },

    computed: {
        isFormValid(): boolean {
            return !this.errors.email &&
                   !this.errors.password &&
                   this.form.email.trim() !== '' &&
                   this.form.password.trim() !== '';
        }
    },

    methods: {
        togglePasswordVisibility(): void {
            this.showPassword = !this.showPassword;
        },

        onEmailChange(): void {
            this.validateField('email');
        },

        onPasswordChange(): void {
            this.validateField('password');
        },

        onBlur(field: keyof LoginTouched): void {
            this.focusedField = null;
            this.touched[field] = true;
            this.validateField(field);
        },

        validateField(field: keyof LoginTouched): void {
            if (!this.touched[field]) return;

            switch (field) {
                case 'email':
                    this.errors.email = LoginValidator.validateEmail(this.form.email);
                    break;
                case 'password':
                    this.errors.password = LoginValidator.validatePassword(this.form.password);
                    break;
            }
        },

        validateAllFields(): boolean {
            Object.keys(this.touched).forEach(key => {
                this.touched[key as keyof LoginTouched] = true;
            });

            this.errors.email = LoginValidator.validateEmail(this.form.email);
            this.errors.password = LoginValidator.validatePassword(this.form.password);

            return !this.errors.email && !this.errors.password;
        },

        closeAllModals(): void {
            this.showInvalidCredentialsModal = false;
            this.showLoginErrorModal = false;
        },

        closeInvalidCredentialsModal(): void {
            this.showInvalidCredentialsModal = false;
        },

        closeLoginErrorModal(): void {
            this.showLoginErrorModal = false;
        },

        async onLogin(): Promise<void> {
            if (!this.validateAllFields()) {
                return;
            }

            this.isLoading = true;

            try {
                const email = this.form.email.trim().toLowerCase();
                const password = this.form.password;

                const response = await this.auth.login({ email, password });

                this.$navigateTo(MyBudget, {
                    transition: {
                        name: 'slideLeft',
                        duration: 300
                    },
                    clearHistory: true
                });

            } catch (error) {
                if (error instanceof Error) {
                    const errorMessage = error.message.toLowerCase();

                    if (errorMessage.includes('неверный email или пароль') ||
                        errorMessage.includes('invalid credentials') ||
                        errorMessage.includes('bad credentials')) {
                        this.showInvalidCredentialsModal = true;
                    } else {
                        this.showLoginErrorModal = true;
                    }
                } else {
                    this.showLoginErrorModal = true;
                }
            } finally {
                this.isLoading = false;
            }
        },

        onForgotPassword(): void {
            console.log('Forgot password clicked');
        },

        goToRegistration(): void {
            this.$navigateTo(Registration, {
                transition: {
                    name: 'fade',
                    duration: 300
                }
            });
        }
    }
});
</script>
