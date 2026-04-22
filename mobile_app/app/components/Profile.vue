<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView v-if="!isLoading" row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">

                    <FlexboxLayout  alignItems="center" class="mb-6">
                        
                        <Label text="Профиль" class="text-white font-inter font-semibold text-2xl ml-1" />
                    </FlexboxLayout>

                    <Image src="~/assets/images/photo.png" width="93" height="93" borderRadius="93" class="self-center mb-4" />

                    <StackLayout class="bg-[#1E1E1E] rounded-xl px-4 pt-4 pb-4 mb-6">
                        <FlexboxLayout flexDirection="row" alignItems="center" justifyContent="center" class="mb-2">
                            <Label :text="userName" class="text-white font-inter font-semibold text-lg text-center" />
                            <Image src="~/assets/images/edit.png" width="16" height="16" class="ml-2" @tap="showEditNameModal" />
                        </FlexboxLayout>
                        <Label :text="userEmail" class="text-[#8A8A8A] font-inter font-normal text-sm text-center" />
                    </StackLayout>

                </FlexboxLayout>
            </ScrollView>

            <ActivityIndicator v-if="isLoading" row="0" col="0" :busy="true" color="#964BDC" class="my-auto" />

            <GridLayout row="1" col="0" rows="auto, auto" columns="*" class="bg-[#121212]">
                <StackLayout row="0" class="mx-4 mb-4 bg-[#1E1E1E] rounded-xl">
                    <GridLayout rows="auto" columns="auto, *" class="p-4" @tap="goToLogin">
                        <Image col="0" src="~/assets/images/exitp.png" width="22" height="22"/>
                        <Label col="1" text="Выйти из аккаунта" class="text-white font-inter font-semibold text-sm ml-4" />
                    </GridLayout>
                    <GridLayout height="1" backgroundColor="#8A8A8A" opacity="0.24" />
                    <GridLayout rows="auto" columns="auto, *" class="p-4" @tap="showDeleteAccountModal">
                        <Image col="0" src="~/assets/images/trash.png" width="24" height="24" />
                        <Label col="1" text="Удалить аккаунт" class="text-white font-inter font-semibold text-sm ml-4" />
                    </GridLayout>
                </StackLayout>

                <Menu row="1" col="0" verticalAlignment="bottom" :activeTab="activeTab" @update:activeTab="activeTab = $event" />
            </GridLayout>

            <GridLayout v-if="showEditModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeEditModal" zIndex="1000" />

            <GridLayout v-if="showEditModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-xl p-5 w-80" @tap="preventClose">
                    <Label text="Редактировать" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-xl px-4 min-h-14 items-center pt-1 mb-4" :class="editNameFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusEditName">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Имя" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="editNameField" v-model="editName" hint="Имя" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="editNameFocused = true" @blur="editNameFocused = false" />
                        </StackLayout>
                    </GridLayout>

                    <Button text="Сохранить" :class="['bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-xl w-full mt-2', !isNameChanged ? 'bg-[#969696]' : '']" :isEnabled="isNameChanged" @tap="saveName" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-xl w-full mt-3" @tap="closeEditModal" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeDeleteModal" zIndex="1000" />

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Вы уверены, что хотите удалить аккаунт?" class="text-[#8A8A8A] font-inter font-semibold text-base text-left" textWrap="true" />
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end" class="mt-4">
                        <Label text="Отмена" class="text-[#964BDC] font-inter font-semibold text-sm py-2 px-4 mr-4" @tap="closeDeleteModal" />
                        <Label text="Удалить" class="text-[#DE6C35] font-inter font-semibold text-sm py-2 px-4" @tap="confirmDeleteAccount" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { Frame } from '@nativescript/core';
import { $navigateTo } from 'nativescript-vue';
import Menu from './Menu.vue';
import Login from './Login.vue';
import Registration from './Registration.vue';

export default defineComponent({
    components: {
        Menu
    },
    data() {
        return {
            activeTab: 'profile',
            isLoading: false,
            userName: 'Иванов Иван Иванович',
            userEmail: 'ivanov@mail.ru',
            editName: '',
            editNameFocused: false,
            showEditModalFlag: false,
            showDeleteModalFlag: false
        };
    },
    computed: {
        isNameChanged(): boolean {
            return this.editName.trim() !== '' && this.editName !== this.userName;
        }
    },
    methods: {
        focusEditName() {
            this.editNameFocused = true;
            setTimeout(() => {
                const field = this.$refs.editNameField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        
        showEditNameModal() {
            this.editName = this.userName;
            this.showEditModalFlag = true;
        },
        
        closeEditModal() {
            this.showEditModalFlag = false;
            this.editName = '';
        },
        
        saveName() {
            if (this.isNameChanged) {
                this.userName = this.editName;
                console.log('Name updated to:', this.userName);
                this.closeEditModal();
            }
        },
        
        showDeleteAccountModal() {
            this.showDeleteModalFlag = true;
        },
        
        closeDeleteModal() {
            this.showDeleteModalFlag = false;
        },
        
        confirmDeleteAccount() {
            console.log('Account deleted');
            this.closeDeleteModal();
            $navigateTo(Registration, {
                transition: {
                    name: 'fade',
                    duration: 300
                },
                clearHistory: true
            });
        },
        
        goToLogin() {
            $navigateTo(Login, {
                transition: {
                    name: 'fade',
                    duration: 300
                },
                clearHistory: true
            });
        },
        
        goBack() {
            const frame = Frame.topmost();
            if (frame) {
                frame.goBack();
            }
        },
        
        preventClose(event: any) {
            event.cancelBubble = true;
        }
    }
});
</script>