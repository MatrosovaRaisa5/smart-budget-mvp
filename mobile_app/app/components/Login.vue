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
                                <GridLayout rows="auto" columns="16, *, auto" 
                                           class="bg-[#2F2D44] rounded-[32px] h-14 items-center border-5 px-4"
                                           :class="[
                                               focusedField === 'email' ? 'border-[#964BDC]' : 'border-[#2F2D44]',
                                               showErrorModal ? 'border-[#FF0000]' : ''
                                           ]">
                                    
                                    <Image col="0" src="~/assets/images/email.png" 
                                           width="16" height="12" class="align-middle" />
                                    
                                    <TextField col="1" 
                                              v-model="form.email"
                                              hint="E-mail"
                                              class="text-white font-inter font-semibold text-[14px] bg-transparent p-0 ml-4 h-14 text-left align-middle placeholder-[#B4B4B4]"
                                              :color="form.email ? 'white' : '#B4B4B4'"
                                              @focus="focusedField = 'email'"
                                              @blur="focusedField = null"
                                              @textChange="clearError"
                                              autocorrect="false"
                                              autocapitalizationType="none" />
                                </GridLayout>
                                
                                <!-- Пароль поле -->
                                <GridLayout rows="auto" columns="16, *, 20" 
                                           class="bg-[#2F2D44] rounded-[32px] h-14 items-center border-5 px-4 mt-5"
                                           :class="[
                                               focusedField === 'password' ? 'border-[#964BDC]' : 'border-[#2F2D44]',
                                               showErrorModal ? 'border-[#FF0000]' : ''
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
                                              @blur="focusedField = null"
                                              @textChange="clearError"
                                              autocorrect="false"
                                              autocapitalizationType="none" />
                                    
                                    <Image col="2" 
                                          :src="showPassword ? '~/assets/images/openeyes.png' : '~/assets/images/closeeyes.png'"
                                          :width="showPassword ? 24 : 18" 
                                          :height="showPassword ? 16 : 12" 
                                          class="align-middle"
                                          @tap="togglePasswordVisibility" />
                                </GridLayout>
                                
                                <!-- Забыли пароль (как ссылка) -->
                                <Label text="Забыли пароль?" 
                                       class="text-white font-inter font-semibold text-[14px] leading-[14px] text-right mt-4 mb-2"
                                       @tap="onForgotPassword"/>
                                
                                <!-- Кнопка входа -->
                                <Button :text="'Войти'"
                                       :class="['h-14 rounded-[32px] font-inter font-bold text-[18px] text-white w-full mt-2', 
                                                isFormValid ? 'bg-[#964BDC]' : 'bg-[#969696]']"
                                       :isEnabled="isFormValid"
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
            
            <!-- Затемняющий фон (поверх всего) -->
            <GridLayout v-if="showErrorModal" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeErrorModal"
                       zIndex="1000" />
            
            <!-- Модальное окно ошибки (поверх фона) -->
            <GridLayout v-if="showErrorModal" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
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
            
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { userExists, checkPassword } from './data/users';

export default defineComponent({
    data() {
        return {
            form: {
                email: '',
                password: ''
            },
            focusedField: null as string | null,
            showPassword: false,
            showErrorModal: false
        };
    },
    computed: {
        isFormValid(): boolean {
            return this.form.email.trim() !== '' && this.form.password.trim() !== '';
        }
    },
    methods: {
        togglePasswordVisibility(): void {
            this.showPassword = !this.showPassword;
        },
        
        clearError(): void {
            // Ничего не делаем
        },
        
        closeErrorModal(): void {
            this.showErrorModal = false;
        },
        
        onLogin(): void {
            if (!this.isFormValid) return;
            
            const email = this.form.email.trim();
            const password = this.form.password;
            
            if (userExists(email)) {
                if (checkPassword(email, password)) {
                    console.log('Login successful for:', email);
                    
                    this.$navigateTo(require('./Main.vue').default, {
                        transition: { 
                            name: 'slideLeft', 
                            duration: 300 
                        },
                        clearHistory: true
                    });
                } else {
                    this.showErrorModal = true;
                }
            } else {
                this.showErrorModal = true;
            }
        },
        
        onForgotPassword(): void {
            console.log('Forgot password clicked');
        },
        
        goToRegistration(): void {
            this.$navigateTo(require('./Registration.vue').default, {
                transition: { 
                    name: 'fade', 
                    duration: 300 
                }
            });
        }
    }
});
</script>