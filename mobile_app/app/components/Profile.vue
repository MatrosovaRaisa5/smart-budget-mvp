<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-profile">
        <GridLayout rows="*, auto" columns="*" backgroundColor="#13131A">
            
            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (СКРОЛЛИТСЯ) -->
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" paddingLeft="16" paddingRight="16" paddingBottom="140">
                    
                    <!-- Заголовок -->
                    <Label text="Профиль" 
                           class="page-title" 
                           marginTop="16"
                           marginBottom="24"
                           horizontalAlignment="left" />
                    
                    <!-- Фото профиля -->
                    <Image src="~/assets/images/photo.png" 
                           width="93" height="93"
                           borderRadius="93"
                           horizontalAlignment="center"
                           marginBottom="16" />
                    
                    <!-- Блок с именем и почтой (фон 2F2D44) -->
                    <!-- Блок с именем и почтой (фон 2F2D44) -->
                    <StackLayout class="profile-info-container" backgroundColor="#2F2D44" borderRadius="16" 
                                paddingTop="16" paddingBottom="16" paddingLeft="16" paddingRight="16"
                                alignItems="center">
                        
                        <!-- Имя пользователя с иконкой редактирования -->
                        <FlexboxLayout flexDirection="row" alignItems="center" justifyContent="center">
                            <Label :text="userName" 
                                class="user-name" />
                            <Image src="~/assets/images/edit.png" 
                                width="16" height="16"
                                marginLeft="8"
                                @tap="showEditNameModal" />
                        </FlexboxLayout>
                        
                        <!-- Email пользователя -->
                        <Label :text="userEmail" 
                            class="user-email"
                            marginTop="8" />
                    </StackLayout>
                    
                    <!-- Дополнительный отступ для контента -->
                    <StackLayout height="40" />
                    
                </FlexboxLayout>
            </ScrollView>
            
            <!-- СЛОЙ 2: БЛОК КНОПОК (ПРИБИТ К НИЗУ, НАД МЕНЮ) -->
            <StackLayout row="0" col="0" 
                        verticalAlignment="bottom"
                        backgroundColor="#2F2D44" 
                        borderRadius="16" 
                        marginLeft="16" 
                        marginRight="16" 
                        marginBottom="16"
                        paddingTop="8" 
                        zIndex="10">
                
                <!-- Кнопка Выйти из аккаунта -->
                <GridLayout rows="auto" columns="auto, *" class="action-item" padding="16" @tap="goToLogin">
                    <Image col="0" src="~/assets/images/exitp.png" 
                           width="22" height="22" />
                    <Label col="1" text="Выйти из аккаунта" 
                           class="action-text"
                           marginLeft="16" />
                </GridLayout>
                
                <!-- Разделительная линия -->
                <GridLayout height="2" backgroundColor="#969696" opacity="0.24" />
                
                <!-- Кнопка Удалить аккаунт -->
                <GridLayout rows="auto" columns="auto, *" class="action-item" padding="16" @tap="showDeleteAccountModal">
                    <Image col="0" src="~/assets/images/trash.png" 
                           width="22" height="22" />
                    <Label col="1" text="Удалить аккаунт" 
                           class="action-text"
                           marginLeft="16" />
                </GridLayout>
            </StackLayout>
            
            <!-- СЛОЙ 3: МЕНЮ (ПРИБИТО К НИЗУ) -->
            <Menu row="1" col="0" 
                  verticalAlignment="bottom"
                  :activeTab="activeTab" 
                  @update:activeTab="activeTab = $event"
                  zIndex="1" />
            
            <!-- МОДАЛЬНОЕ ОКНО РЕДАКТИРОВАНИЯ ИМЕНИ -->
            <GridLayout v-if="showEditModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeEditModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showEditModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="edit-modal" @tap="preventClose">
                    
                    <Label text="Редактировать" 
                           class="edit-modal-title"
                           marginBottom="16" />
                    
                    <!-- Поле ввода имени -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': editNameFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            <TextField v-model="editName"
                                      hint="Имя"
                                      class="input-text"
                                      @focus="editNameFocused = true"
                                      @blur="editNameFocused = false" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Кнопки -->
                    <Button text="Сохранить" 
                            :class="['edit-button', isNameChanged ? 'active' : 'inactive']"
                            :isEnabled="isNameChanged"
                            @tap="saveName"
                            marginTop="16" />
                    
                    <Button text="Отмена" 
                            class="cancel-button-modal"
                            @tap="closeEditModal"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>
            
            <!-- МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ АККАУНТА -->
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeDeleteModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="delete-modal" @tap="preventClose">
                    <Label text="Вы уверены, что хотите удалить аккаунт?" 
                           class="delete-modal-text"
                           textWrap="true"
                           marginBottom="24" />
                    
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end">
                        <Label text="Отмена" 
                               class="cancel-text"
                               marginRight="40"
                               @tap="closeDeleteModal" />
                        <Label text="Удалить" 
                               class="delete-text"
                               @tap="confirmDeleteAccount" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>
            
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
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
        
        preventClose(event: any) {
            event.cancelBubble = true;
        }
    }
});
</script>

<style scoped>
.page-profile {
    background-color: #13131A;
}

.page-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.profile-info-container {
    background-color: #2F2D44;
    border-radius: 16;
    width: 100%;
}

.user-name {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
    text-align: center;
}

.user-email {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: 500;
    font-size: 14;
    text-align: center;
}

.action-item {
    width: 100%;
}

.action-text {
    color: #FFFFFF;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.edit-modal {
    background-color: #1E1D2E;
    border-radius: 16;
    padding: 16;
    width: 90%;
    max-width: 400;
}

.edit-modal-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
    text-align: left;
}

.input-wrapper {
    width: 100%;
}

.input-field {
    background-color: #2F2D44;
    border-radius: 16;
    border-width: 2;
    border-color: #2F2D44;
    width: 100%;
    padding: 0 16;
    align-items: center;
}

.input-field GridLayout {
    align-items: center;
    height: 56;
}

.input-focused {
    border-color: #964BDC;
}

.input-text {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
    background-color: transparent;
    padding: 0;
    placeholder-color: #B4B4B4;
    vertical-align: middle;
    height: 56;
}

.edit-button {
    height: 56;
    border-radius: 16;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    color: white;
    width: 100%;
}

.edit-button.inactive {
    background-color: #969696;
}

.edit-button.active {
    background-color: #964BDC;
}

.edit-button.active:highlighted {
    background-color: #7B3CB0;
}

.cancel-button-modal {
    background-color: #FF0000;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 56;
    border-radius: 16;
    width: 100%;
}

.cancel-button-modal:highlighted {
    background-color: #CC0000;
}

.delete-modal {
    background-color: white;
    border-radius: 15;
    padding: 20;
    width: 90%;
    max-width: 400;
}

.delete-modal-text {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 18;
    text-align: left;
}

.cancel-text {
    color: #964BDC;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
    background-color: transparent;
}

.delete-text {
    color: #FF0000;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
    background-color: transparent;
}
</style>