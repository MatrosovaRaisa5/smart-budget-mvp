<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">
            
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" justifyContent="flex-start" alignItems="stretch" class="px-4 pt-8 pb-4">
                    
                    <Label text="Введите почту и&nbsp;пароль"
                           class="text-white font-inter font-extrabold text-3xl text-left"
                           textWrap="true" />
                    
                    <StackLayout class="mt-5">
                        <FlexboxLayout flexDirection="row" justifyContent="flex-start" alignItems="center" flexWrap="wrap">
                            <Label class="text-white font-inter font-semibold text-base" text="Чтобы войти в " />
                            <Label class="text-white font-inter font-semibold text-base" text="Smart" />
                            <Label class="text-[#964BDC] font-inter font-semibold text-base" text="Wallet" />
                        </FlexboxLayout>
                    </StackLayout>

                    <StackLayout class="mt-6">
                        <GridLayout rows="auto" columns="16, *" 
                                   class="rounded-xl px-4 min-h-14 items-center pt-1"
                                   :class="[
                                       focusedField === 'email' ? 'border-[#964BDC] border-5' : 'border-[#121212] border-5',
                                       errors.email && touched.email ? 'border-[#3D2519] border-5 bg-[#3D2519]' : 'bg-[#252525]'
                                   ]">
                            
                            <Image col="0" src="~/assets/images/email.png" width="16" height="12" class="self-center" />
                            
                            <StackLayout col="1" class="ml-4 py-2" >
                                <Label text="E-mail" 
                                       class="font-inter font-semibold text-xs mb-1"
                                       :class="errors.email && touched.email ? 'text-[#DE6C35]' : 'text-[#8A8A8A]'" />
                                <TextField v-model="form.email"
                                          hint="test@mail.ru"
                                          class="text-white font-inter font-medium text-sm bg-transparent p-0"
                                          color="white"
                                          @focus="focusedField = 'email'"
                                          @blur="onBlur('email')"
                                          @textChange="onEmailChange"
                                          autocorrect="false"
                                          autocapitalizationType="none" />
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
                                fontSize="12"
                                color="#964BDC"
                                class="font-inter font-semibold text-right mr-0"
                                @tap="togglePasswordVisibility" />
                        </GridLayout>
                        <Label v-if="errors.password && touched.password"
                               :text="errors.password"
                               class="text-[#DE6C35] font-inter text-xs ml-4 mt-1" />

                        <GridLayout class="mt-4" @tap="onForgotPassword">
                            <Label text="Забыли пароль?"
                                   class="text-white font-inter font-semibold text-sm leading-5 text-right py-2 px-2" />
                        </GridLayout>
                    </StackLayout>
                </FlexboxLayout>
            </ScrollView>

            <GridLayout row="1" col="0" rows="auto" columns="*" class="px-4 pb-4">
                <StackLayout>
                    <FlexboxLayout flexDirection="row" justifyContent="center" alignItems="center" class="mb-4">
                        <Label text="Нет аккаунта?"
                               class="text-white font-inter font-semibold text-sm leading-5" 
                               textWrap="true"/>
                        
                        <Label text="Зарегистрироваться"
                               class="text-[#964BDC] font-inter font-semibold text-sm leading-5 ml-8 py-2 px-2"
                               @tap="goToRegistration" />
                    </FlexboxLayout>
                    
                    <ActivityIndicator v-if="isLoading" :busy="true" color="#964BDC" class="mb-4" />
                    
                    <Button v-else
                           :text="'Войти'"
                           :class="['h-14 rounded-xl font-inter font-semibold text-base text-white w-full',
                                    isFormValid && !isLoading ? 'bg-[#964BDC]' : 'bg-[#969696]']"
                           :isEnabled="isFormValid && !isLoading"
                           @tap="onLogin" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showInvalidCredentialsModal || showLoginErrorModal"
                       row="0" col="0" rowSpan="2"
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAllModals"
                       zIndex="1000" />

           
            <GridLayout v-if="showInvalidCredentialsModal" row="0" col="0" rowSpan="2"
                    horizontalAlignment="stretch" verticalAlignment="center"
                    marginLeft="16" marginRight="16"
                    @tap="closeInvalidCredentialsModal"
                    zIndex="1001">
                <StackLayout class="bg-white rounded-xl py-5 px-4">
                    <Image src="~/assets/images/sad.png"
                        width="36" height="36"
                        class="text-center" />
                    <Label text="Неверный логин или пароль"
                        class="text-[#DE6C35] font-inter font-semibold text-lg leading-7 text-center mt-3"
                        textWrap="true" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showLoginErrorModal" row="0" col="0" rowSpan="2"
                    horizontalAlignment="stretch" verticalAlignment="center"
                    marginLeft="16" marginRight="16"
                    @tap="closeLoginErrorModal"
                    zIndex="1001">
                <StackLayout class="bg-white rounded-xl py-5 px-4">
                    <Image src="~/assets/images/sad.png"
                        width="36" height="36"
                        class="text-center" />
                    <Label text="Ошибка входа"
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
            this.touched.email = true;
            this.validateField('email');
        },

        onPasswordChange(): void {
            this.touched.password = true;
            this.validateField('password');
        },

        onBlur(field: keyof LoginTouched): void {
            this.focusedField = null;
            this.touched[field] = true;
            this.validateField(field);
        },

        validateField(field: keyof LoginTouched): void {
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
            this.touched.email = true;
            this.touched.password = true;
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
            alert('Восстановление пароля будет доступно позже');
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