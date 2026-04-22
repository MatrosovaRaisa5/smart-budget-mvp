<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView row="0" col="0">
                <StackLayout flexDirection="column" justifyContent="flex-start" alignItems="stretch" class="px-4 pt-8 pb-10">

                    <Label text="Заполните необходимые поля"
                           class="text-white font-inter font-extrabold text-3xl text-left"
                           textWrap="true" />

                    <StackLayout class="mt-5">
                        <FlexboxLayout flexDirection="row" justifyContent="flex-start" alignItems="center" flexWrap="wrap">
                            <Label class="text-white font-inter font-semibold text-base" text="Чтобы зарегистрироваться в " />
                            <Label class="text-white font-inter font-semibold text-base" text="Smart" />
                            <Label class="text-[#964BDC] font-inter font-semibold text-base" text="Wallet" />
                        </FlexboxLayout>
                    </StackLayout>

                    <StackLayout class="mt-6">
                        <GridLayout rows="auto" columns="16, *"
                                   class="rounded-xl px-4 min-h-14 items-center pt-1"
                                   :class="[
                                       focusedField === 'fullname' ? 'border-[#964BDC] border-5' : 'border-[#121212] border-5',
                                       errors.fullname && touched.fullname ? 'border-[#3D2519] border-5 bg-[#3D2519]' : 'bg-[#252525]'
                                   ]">

                            <Image col="0" src="~/assets/images/name.png" width="14" height="18" class="self-center" />

                            <StackLayout col="1" class="ml-4 py-2">
                                <Label text="ФИО"
                                       class="font-inter font-semibold text-xs mb-1"
                                       :class="errors.fullname && touched.fullname ? 'text-[#DE6C35]' : 'text-[#8A8A8A]'" />
                                <TextField v-model="form.fullname"
                                          hint="Иванов Иван Иванович"
                                          hintColor="#BEBEBE"
                                          class="text-white font-inter font-medium text-sm bg-transparent p-0"
                                          color="white"
                                          @focus="focusedField = 'fullname'"
                                          @blur="onBlur('fullname')"
                                          @textChange="onFullnameChange"
                                          autocorrect="false"
                                          autocapitalizationType="words" />
                            </StackLayout>
                        </GridLayout>
                        <Label v-if="errors.fullname && touched.fullname"
                               :text="errors.fullname"
                               class="text-[#DE6C35] font-inter text-xs ml-4 mt-1" />

                        <GridLayout rows="auto" columns="16, *" class="mt-4 rounded-xl px-4 min-h-14 items-center pt-1"
                                   :class="[
                                       focusedField === 'email' ? 'border-[#964BDC] border-5' : 'border-[#121212] border-5',
                                       errors.email && touched.email ? 'border-[#3D2519] border-5 bg-[#3D2519]' : 'bg-[#252525]'
                                   ]">

                            <Image col="0" src="~/assets/images/email.png" width="16" height="12" class="self-center" />

                            <StackLayout col="1" class="ml-4 py-2">
                                <Label text="E-mail"
                                       class="font-inter font-semibold text-xs mb-1"
                                       :class="errors.email && touched.email ? 'text-[#DE6C35]' : 'text-[#8A8A8A]'" />
                                <TextField v-model="form.email"
                                          hint="test@mail.ru"
                                          hintColor="#BEBEBE"
                                          class="text-white font-inter font-medium text-sm bg-transparent p-0"
                                          color="white"
                                          @focus="focusedField = 'email'"
                                          @blur="onBlur('email')"
                                          @textChange="onEmailChange"
                                          autocorrect="false"
                                          autocapitalizationType="none"
                                          keyboardType="email" />
                            </StackLayout>
                        </GridLayout>
                        <Label v-if="errors.email && touched.email"
                               :text="errors.email"
                               class="text-[#DE6C35] font-inter text-xs ml-4 mt-1" />

                        <GridLayout rows="auto" columns="16, *, auto" class="mt-4 rounded-xl px-4 min-h-14 items-center pt-1"
                                   :class="[
                                       focusedField === 'password' ? 'border-[#964BDC] border-5' : 'border-[#121212] border-5',
                                       errors.password && touched.password ? 'border-[#3D2519] border-5 bg-[#3D2519]' : 'bg-[#252525]'
                                   ]">

                            <Image col="0" src="~/assets/images/key.png" width="16" height="18" class="self-center" />

                            <StackLayout col="1" class="ml-4 py-2">
                                <Label text="Пароль"
                                       class="font-inter font-semibold text-xs mb-1"
                                       :class="errors.password && touched.password ? 'text-[#DE6C35]' : 'text-[#8A8A8A]'" />
                                <TextField v-model="form.password"
                                          :secure="!showPassword"
                                          hint="123456"
                                          hintColor="#BEBEBE"
                                          class="text-white font-inter font-normal text-sm bg-transparent p-0"
                                          style="font-size: 14px; height: 18;"
                                          color="white"
                                          @focus="focusedField = 'password'"
                                          @blur="onBlur('password')"
                                          @textChange="onPasswordChange"
                                          autocorrect="false"
                                          autocapitalizationType="none" />
                            </StackLayout>

                            <Label v-if="form.password.length > 0"
                                col="2"
                                :text="showPassword ? 'Скрыть' : 'Показать'"
                                class="font-inter font-semibold text-xs text-right mr-0"
                                color="#964BDC"
                                @tap="togglePasswordVisibility" />
                        </GridLayout>
                        <Label v-if="errors.password && touched.password"
                               :text="errors.password"
                               class="text-[#DE6C35] font-inter text-xs ml-4 mt-1" />

                        <GridLayout rows="auto" columns="16, *, auto" class="mt-4 rounded-xl px-4 min-h-14 items-center pt-1"
                                   :class="[
                                       focusedField === 'confirmPassword' ? 'border-[#964BDC] border-5' : 'border-[#121212] border-5',
                                       errors.confirmPassword && touched.confirmPassword ? 'border-[#3D2519] border-5 bg-[#3D2519]' : 'bg-[#252525]'
                                   ]">

                            <Image col="0" src="~/assets/images/key.png" width="16" height="18" class="self-center" />

                            <StackLayout col="1" class="ml-4 py-2">
                                <Label text="Подтверждение пароля"
                                       class="font-inter font-semibold text-xs mb-1"
                                       :class="errors.confirmPassword && touched.confirmPassword ? 'text-[#DE6C35]' : 'text-[#8A8A8A]'" />
                                <TextField v-model="form.confirmPassword"
                                          :secure="!showConfirmPassword"
                                          hint="123456"
                                          hintColor="#BEBEBE"
                                          class="text-white font-inter font-normal text-sm bg-transparent p-0"
                                          style="font-size: 14px; height: 18;"
                                          color="white"
                                          @focus="focusedField = 'confirmPassword'"
                                          @blur="onBlur('confirmPassword')"
                                          @textChange="onConfirmPasswordChange"
                                          autocorrect="false"
                                          autocapitalizationType="none" />
                            </StackLayout>

                            <Label v-if="form.confirmPassword.length > 0"
                                col="2"
                                :text="showConfirmPassword ? 'Скрыть' : 'Показать'"
                                class="font-inter font-semibold text-xs text-right mr-0"
                                color="#964BDC"
                                @tap="toggleConfirmPasswordVisibility" />
                        </GridLayout>
                        <Label v-if="errors.confirmPassword && touched.confirmPassword"
                               :text="errors.confirmPassword"
                               class="text-[#DE6C35] font-inter text-xs ml-4 mt-1" />

                        <FlexboxLayout flexDirection="row" alignItems="center" class="mt-6">
                            <StackLayout width="24" height="24" flexShrink="0"
                                        class="mr-3 ml-4 rounded-md items-center justify-center"
                                        :class="[isAgreed ? 'bg-[#964BDC]' : 'bg-[#252525]']"
                                        borderWidth="1.5"
                                        :borderColor="isAgreed ? '#964BDC' : '#8A8A8A'"
                                        @tap="toggleAgreement">
                                <Label v-if="isAgreed" text="✓" color="white" fontSize="14" fontWeight="bold"
                                    horizontalAlignment="center" verticalAlignment="center" />
                            </StackLayout>

                            <FlexboxLayout flexDirection="row" flexWrap="wrap" class="flex-1">
                                <Label text="Я соглашаюсь "
                                    class="text-white font-inter font-semibold text-xs" />
                                <Label text="с политикой обработки персональных данных"
                                    class="text-[#964BDC] font-inter font-semibold text-xs"
                                    @tap="openPrivacyPolicy" />
                            </FlexboxLayout>
                        </FlexboxLayout>
                        <Label v-if="errors.agreement && touched.agreement"
                            :text="errors.agreement"
                            class="text-[#DE6C35] font-inter text-xs ml-4 mt-1" />
                    </StackLayout>
                </StackLayout>
            </ScrollView>

            <GridLayout row="1" col="0" rows="auto" columns="*" class="px-4 pb-4">
                <StackLayout>
                    <FlexboxLayout flexDirection="row" justifyContent="center" alignItems="center" class="mb-4">
                        <Label text="Уже есть аккаунт?"
                               class="text-white font-inter font-semibold text-sm" />
                        <Label text="Войти"
                               class="text-[#964BDC] font-inter font-semibold text-sm ml-8 py-2 px-2"
                               @tap="goToLogin" />
                    </FlexboxLayout>

                    <ActivityIndicator v-if="isLoading" :busy="true" color="#964BDC" class="mb-4" />

                    <Button v-else
                           :text="'Зарегистрироваться'"
                           :class="['h-14 rounded-xl font-inter font-semibold text-base text-white w-full',
                                    isFormValid && !isLoading ? 'bg-[#964BDC]' : 'bg-[#969696]']"
                           :isEnabled="isFormValid && !isLoading"
                           @tap="onRegister" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showUserExistsModal || showRegisterErrorModal"
                       row="0" col="0" rowSpan="2"
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAllModals"
                       zIndex="1000" />

            <GridLayout v-if="showUserExistsModal" row="0" col="0" rowSpan="2"
                       horizontalAlignment="stretch" verticalAlignment="center"
                       marginLeft="16" marginRight="16"
                       @tap="closeUserExistsModal"
                       zIndex="1001">
                <StackLayout class="bg-white rounded-xl py-5 px-4">
                    <Image src="~/assets/images/wow.png"
                           width="36" height="36"
                           class="text-center" />
                    <Label text="Пользователь уже существует"
                           class="text-[#DE6C35] font-inter font-semibold text-lg leading-7 text-center mt-3"
                           textWrap="true" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showRegisterErrorModal" row="0" col="0" rowSpan="2"
                       horizontalAlignment="stretch" verticalAlignment="center"
                       marginLeft="16" marginRight="16"
                       @tap="closeRegisterErrorModal"
                       zIndex="1001">
                <StackLayout class="bg-white rounded-xl py-5 px-4">
                    <Image src="~/assets/images/sad.png"
                           width="36" height="36"
                           class="text-center" />
                    <Label text="Ошибка регистрации"
                           class="text-[#DE6C35] font-inter font-semibold text-lg leading-7 text-center mt-3"
                           textWrap="true" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { AuthProvider } from '../providers/auth.provider';
import MyBudget from './MyBudget.vue';
import Login from './Login.vue';
import {
    RegistrationFormData,
    RegistrationErrors,
    RegistrationTouched
} from '../models/form.types';
import { RegistrationValidator } from '../validators/registration.validator';

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
        };
    },

    computed: {
        isFormValid(): boolean {
            const noErrors = !this.errors.fullname &&
                !this.errors.email &&
                !this.errors.password &&
                !this.errors.confirmPassword &&
                !this.errors.agreement;

            const allFieldsFilled = this.form.fullname.trim() !== '' &&
                this.form.email.trim() !== '' &&
                this.form.password.trim() !== '' &&
                this.form.confirmPassword.trim() !== '' &&
                this.isAgreed;

            return noErrors && allFieldsFilled;
        }
    },

    methods: {
        togglePasswordVisibility(): void {
            this.showPassword = !this.showPassword;
        },

        toggleConfirmPasswordVisibility(): void {
            this.showConfirmPassword = !this.showConfirmPassword;
        },

        toggleAgreement(): void {
            this.isAgreed = !this.isAgreed;
            this.touched.agreement = true;
            if (this.isAgreed) {
                this.errors.agreement = '';
            }
        },

        onFullnameChange(): void {
            this.touched.fullname = true;
            this.validateField('fullname');
        },

        onEmailChange(): void {
            this.touched.email = true;
            this.validateField('email');
        },

        onPasswordChange(): void {
            this.touched.password = true;
            this.validateField('password');
            if (this.form.confirmPassword) {
                this.validateField('confirmPassword');
            }
        },

        onConfirmPasswordChange(): void {
            this.touched.confirmPassword = true;
            this.validateField('confirmPassword');
        },

        onBlur(field: keyof RegistrationFormData): void {
            this.focusedField = null;
            this.touched[field] = true;
            this.validateField(field);
        },

        validateField(field: keyof RegistrationFormData): void {
            switch (field) {
                case 'fullname':
                    this.errors.fullname = RegistrationValidator.validateFullname(this.form.fullname);
                    break;
                case 'email':
                    this.errors.email = RegistrationValidator.validateEmail(this.form.email);
                    break;
                case 'password':
                    this.errors.password = RegistrationValidator.validatePassword(this.form.password);
                    if (this.form.confirmPassword) {
                        this.errors.confirmPassword = RegistrationValidator.validateConfirmPassword(
                            this.form.password,
                            this.form.confirmPassword
                        );
                    }
                    break;
                case 'confirmPassword':
                    this.errors.confirmPassword = RegistrationValidator.validateConfirmPassword(
                        this.form.password,
                        this.form.confirmPassword
                    );
                    break;
            }
        },

        validateAllFields(): boolean {
            Object.keys(this.touched).forEach(key => {
                this.touched[key as keyof RegistrationTouched] = true;
            });

            const validationErrors = RegistrationValidator.validateForm(this.form, this.isAgreed);
            this.errors = { ...validationErrors };

            return !this.errors.fullname &&
                !this.errors.email &&
                !this.errors.password &&
                !this.errors.confirmPassword &&
                !this.errors.agreement;
        },

        closeAllModals(): void {
            this.showUserExistsModal = false;
            this.showRegisterErrorModal = false;
        },

        closeUserExistsModal(): void {
            this.showUserExistsModal = false;
        },

        closeRegisterErrorModal(): void {
            this.showRegisterErrorModal = false;
        },

        async onRegister(): Promise<void> {
            if (!this.validateAllFields()) return;

            this.isLoading = true;

            try {
                const email = this.form.email.trim().toLowerCase();
                const password = this.form.password;
                const name = this.form.fullname.trim();

                await this.auth.register({ email, password, name });
                await this.auth.login({ email, password });

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
                    if (errorMessage.includes('email already in use') ||
                        errorMessage.includes('already registered')) {
                        this.showUserExistsModal = true;
                    } else {
                        this.showRegisterErrorModal = true;
                    }
                } else {
                    this.showRegisterErrorModal = true;
                }
            } finally {
                this.isLoading = false;
            }
        },

        openPrivacyPolicy(): void {
            console.log('Open privacy policy');
        },

        goToLogin(): void {
            this.$navigateTo(Login, {
                transition: {
                    name: 'fade',
                    duration: 300
                }
            });
        }
    }
});
</script>
