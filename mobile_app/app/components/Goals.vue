<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-goals">
        <GridLayout rows="*" columns="*" backgroundColor="#13131A">
            
            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (САМЫЙ НИЗ) -->
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" paddingLeft="16" paddingRight="16">
                    
                    <!-- Верхняя панель с заголовком и кнопкой добавить цель -->
                    <GridLayout rows="auto" columns="*, auto" marginTop="32" alignItems="center">
                        <Label col="0" text="Мои цели" 
                            class="goals-title" 
                            horizontalAlignment="left" />
                        
                        <Button col="1" text="Добавить цель" 
                                class="add-goal-button"
                                verticalAlignment="center"
                                @tap="showAddModal" />
                    </GridLayout>
                    
                    <!-- БЛОК ЦЕЛЕЙ -->
                    <StackLayout v-if="goals.length > 0" marginTop="16">
                        <StackLayout v-for="goal in goals" :key="goal.id"
                                   class="goal-item"
                                   marginBottom="16">
                            
                            <!-- Верхняя часть с названием и кнопками -->
                            <GridLayout rows="auto" columns="auto, *, auto, auto, auto" marginBottom="12">
                                <Image col="0" src="~/assets/images/goals.png" 
                                       width="48" height="48" 
                                       horizontalAlignment="left" />
                                
                                <Label col="1" :text="goal.name" 
                                       class="goal-name"
                                       marginLeft="16"
                                       verticalAlignment="center" />
                                
                                <Image col="2" src="~/assets/images/deposit.png" 
                                       width="24" height="24" 
                                       marginRight="16"
                                       verticalAlignment="center"
                                       @tap="showDepositModal(goal)" />
                                
                                <Image col="3" src="~/assets/images/edit.png" 
                                       width="17" height="17" 
                                       marginRight="16"
                                       verticalAlignment="center"
                                       @tap="showEditModal(goal)" />
                                
                                <Image col="4" src="~/assets/images/trash.png" 
                                       width="18" height="18" 
                                       verticalAlignment="center"
                                       @tap="showDeleteModal(goal)" />
                            </GridLayout>
                            
                            <!-- Рекомендуемый ежемесячный взнос -->
                            <Label :text="'Рекомендуемый ежемесячный взнос ' + formatAmount(calculateMonthly(goal)) + ' ₽'"
                                   class="goal-monthly"
                                   marginBottom="8" />
                            
                            <!-- Статус бар -->
                            <GridLayout rows="auto" columns="*" height="3" marginBottom="8">
                                <GridLayout col="0" backgroundColor="#969696" height="3" width="100%" />
                                <GridLayout col="0" :backgroundColor="getProgressColor(goal)"
                                           :width="getProgressPercent(goal) + '%'" height="3" 
                                           horizontalAlignment="left" />
                            </GridLayout>
                            
                            <!-- Осталось и общая цель -->
                            <StackLayout horizontalAlignment="right">
                                <Label :text="'Осталось ' + formatAmount(goal.targetAmount - goal.currentAmount) + ' ₽'"
                                       class="goal-remaining" />
                                <Label :text="'Цель ' + formatAmount(goal.targetAmount) + ' ₽ до ' + formatDate(goal.targetDate)"
                                       class="goal-total" />
                            </StackLayout>
                        </StackLayout>
                    </StackLayout>
                    
                    <!-- Пустое состояние и всплывающее окно -->
                    <FlexboxLayout v-if="goals.length === 0 && !isLoading" 
                                   flexDirection="column" justifyContent="center" alignItems="center" 
                                   height="100%" minHeight="500">
                        <!-- Здесь будет всплывающее окно -->
                    </FlexboxLayout>
                    
                    <!-- Дополнительный отступ снизу для удобства скролла -->
                    <StackLayout height="40" />
                    
                </FlexboxLayout>
            </ScrollView>
            
            <!-- СЛОЙ 2: МЕНЮ (ПОВЕРХ ОСНОВНОГО КОНТЕНТА) -->
            <Menu row="0" col="0" 
                  verticalAlignment="bottom"
                  :activeTab="activeTab" 
                  @update:activeTab="activeTab = $event"
                  zIndex="1" />
            
            <!-- СЛОЙ 3: ЗАТЕМНЯЮЩИЙ ФОН (ПОВЕРХ МЕНЮ) - показываем только если нет данных -->
            <GridLayout v-if="showPopup && goals.length === 0 && !isLoading" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closePopup"
                       zIndex="1000" />
            
            <!-- СЛОЙ 4: МОДАЛЬНОЕ ОКНО (САМЫЙ ВЕРХ) - показываем только если нет данных -->
            <GridLayout v-if="showPopup && goals.length === 0 && !isLoading" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       @tap="closePopup"
                       zIndex="1001">
                <StackLayout backgroundColor="white" borderRadius="15" 
                            paddingLeft="20" paddingRight="20" 
                            paddingTop="20" paddingBottom="20"
                            width="90%"
                            maxWidth="400"
                            @tap="preventClose">
                    
                    <Image src="~/assets/images/smile.png" 
                           width="60" height="60" 
                           horizontalAlignment="center" />
                    
                    <Label text="Пора ставить цели! Нажмите кнопку ниже" 
                           class="popup-text"
                           textWrap="true"
                           horizontalAlignment="center"
                           marginTop="12" />
                    
                    <Button text="Добавить цель" 
                           class="budget-button"
                           @tap="showAddModalFromPopup" />
                </StackLayout>
            </GridLayout>
            
            <!-- СЛОЙ 5: МОДАЛЬНОЕ ОКНО ДОБАВЛЕНИЯ ЦЕЛИ -->
            <GridLayout v-if="showAddModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAddModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showAddModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="add-modal" @tap="preventClose">
                    
                    <!-- Заголовок -->
                    <Label text="Новая цель" 
                           class="add-modal-title"
                           marginBottom="16" />
                    
                    <!-- Поле На что копите -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': nameFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            
                            <TextField v-model="newGoal.name"
                                      :hint="!newGoal.name ? 'На что копите' : ''"
                                      class="input-text"
                                      @focus="nameFocused = true"
                                      @blur="nameFocused = false"
                                      autocorrect="false"
                                      autocapitalizationType="sentences" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Сколько хотите накопить -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': targetAmountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            
                            <TextField col="0" 
                                      v-model="displayTargetAmount"
                                      :hint="!newGoal.targetAmount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
                                      @focus="targetAmountFocused = true"
                                      @blur="targetAmountFocused = false" />
                            
                            <Label col="1" text="₽" 
                                   class="currency-symbol-no-margin"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Дата -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': dateFocused, 'input-error': dateError }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            
                            <TextField v-model="newGoal.targetDate"
                                      :hint="!newGoal.targetDate ? 'ДД.ММ.ГГГГ' : ''"
                                      class="input-text"
                                      @focus="dateFocused = true"
                                      @blur="validateDate" />
                        </GridLayout>
                        
                        <Label v-if="dateError" 
                               :text="dateError"
                               class="error-message"
                               marginTop="4" />
                    </StackLayout>
                    
                    <!-- Поле Текущая накопленная сумма -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': currentAmountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            
                            <TextField col="0" 
                                      v-model="displayCurrentAmount"
                                      :hint="!newGoal.currentAmount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
                                      @focus="currentAmountFocused = true"
                                      @blur="currentAmountFocused = false" />
                            
                            <Label col="1" text="₽" 
                                   class="currency-symbol-no-margin"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Рекомендуемый ежемесячный взнос -->
                    <Label :text="'Рекомендуемый ежемесячный взнос ' + formatAmount(calculatedMonthly) + ' ₽'"
                           class="recommended-text"
                           marginBottom="16" />
                    
                    <!-- Кнопки -->
                    <Button text="Добавить цель" 
                            :class="['add-button', isAddGoalValid ? 'active' : 'inactive']"
                            :isEnabled="isAddGoalValid"
                            @tap="addGoal"
                            marginTop="16" />

                    <Button text="Отмена" 
                            class="cancel-button-modal"
                            @tap="closeAddModal"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>
            
            <!-- СЛОЙ 6: МОДАЛЬНОЕ ОКНО РЕДАКТИРОВАНИЯ ЦЕЛИ -->
            <GridLayout v-if="showEditModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeEditModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showEditModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="add-modal" @tap="preventClose">
                    
                    <!-- Заголовок -->
                    <Label text="Настройка цели" 
                           class="add-modal-title"
                           marginBottom="16" />
                    
                    <!-- Поле На что копите -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': editNameFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            
                            <TextField v-model="editingGoal.name"
                                      :hint="!editingGoal.name ? 'На что копите' : ''"
                                      class="input-text"
                                      @focus="editNameFocused = true"
                                      @blur="editNameFocused = false"
                                      autocorrect="false"
                                      autocapitalizationType="sentences" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Сколько хотите накопить -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': editTargetAmountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            
                            <TextField col="0" 
                                      v-model="displayEditTargetAmount"
                                      :hint="!editingGoal.targetAmount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
                                      @focus="editTargetAmountFocused = true"
                                      @blur="editTargetAmountFocused = false" />
                            
                            <Label col="1" text="₽" 
                                   class="currency-symbol-no-margin"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Дата -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': editDateFocused, 'input-error': editDateError }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            
                            <TextField v-model="editingGoal.targetDate"
                                      :hint="!editingGoal.targetDate ? 'ДД.ММ.ГГГГ' : ''"
                                      class="input-text"
                                      @focus="editDateFocused = true"
                                      @blur="validateEditDate" />
                        </GridLayout>
                        
                        <Label v-if="editDateError" 
                               :text="editDateError"
                               class="error-message"
                               marginTop="4" />
                    </StackLayout>
                    
                    <!-- Поле Текущая накопленная сумма -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': editCurrentAmountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            
                            <TextField col="0" 
                                      v-model="displayEditCurrentAmount"
                                      :hint="!editingGoal.currentAmount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
                                      @focus="editCurrentAmountFocused = true"
                                      @blur="editCurrentAmountFocused = false" />
                            
                            <Label col="1" text="₽" 
                                   class="currency-symbol-no-margin"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Рекомендуемый ежемесячный взнос -->
                    <Label :text="'Рекомендуемый ежемесячный взнос ' + formatAmount(calculateMonthly(editingGoal)) + ' ₽'"
                           class="recommended-text"
                           marginBottom="16" />
                    
                    <!-- Кнопки -->
                    <Button text="Сохранить" 
                            :class="['add-button', isEditGoalValid ? 'active' : 'inactive']"
                            :isEnabled="isEditGoalValid"
                            @tap="updateGoal"
                            marginTop="16" />

                    <Button text="Отмена" 
                            class="cancel-button-modal"
                            @tap="closeEditModal"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>
            
            <!-- СЛОЙ 7: МОДАЛЬНОЕ ОКНО ПОПОЛНЕНИЯ -->
            <GridLayout v-if="showDepositModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeDepositModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showDepositModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="add-modal" @tap="preventClose">
                    
                    <!-- Заголовок -->
                    <Label text="Пополнение" 
                           class="add-modal-title"
                           marginBottom="16" />
                    
                    <!-- Поле На сколько хотите пополнить -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': depositAmountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            
                            <TextField col="0" 
                                      v-model="displayDepositAmount"
                                      :hint="!depositAmount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
                                      @focus="depositAmountFocused = true"
                                      @blur="depositAmountFocused = false" />
                            
                            <Label col="1" text="₽" 
                                   class="currency-symbol-no-margin"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Кнопки -->
                    <Button text="Пополнить" 
                            :class="['add-button', depositAmount && parseFloat(depositAmount) > 0 ? 'active' : 'inactive']"
                            :isEnabled="depositAmount && parseFloat(depositAmount) > 0"
                            @tap="addDeposit"
                            marginTop="16" />

                    <Button text="Отмена" 
                            class="cancel-button-modal"
                            @tap="closeDepositModal"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>
            
            <!-- СЛОЙ 8: МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ -->
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeDeleteModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="delete-modal" @tap="preventClose">
                    <Label text="Удалить цель без возможности восстановления?" 
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
                               @tap="confirmDelete" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>
            <GridLayout height="100px">
                <StackLayout>

                </StackLayout>
            </GridLayout>
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import Menu from './Menu.vue';
import { getGoals, addGoal, updateGoal, removeGoal, depositToGoal, Goal } from './data/goals';

export default defineComponent({
    components: {
        Menu
    },
    data() {
        return {
            activeTab: 'goals',
            goals: [] as Goal[],
            isLoading: true,
            showPopup: true,
            
            showAddModalFlag: false,
            showEditModalFlag: false,
            showDepositModalFlag: false,
            showDeleteModalFlag: false,
            
            nameFocused: false,
            targetAmountFocused: false,
            dateFocused: false,
            currentAmountFocused: false,
            dateError: '',
            
            newGoal: {
                name: '',
                targetAmount: '',
                targetDate: this.getTodayDate(),
                currentAmount: ''
            },
            
            editingGoal: {} as Goal,
            editNameFocused: false,
            editTargetAmountFocused: false,
            editDateFocused: false,
            editCurrentAmountFocused: false,
            editDateError: '',
            
            depositAmount: '',
            depositAmountFocused: false,
            selectedGoalId: null as number | null,
            deleteGoalId: null as number | null
        };
    },
    computed: {
        displayTargetAmount: {
            get(): string { return this.newGoal.targetAmount; },
            set(value: string) { this.newGoal.targetAmount = value.replace(/[^\d]/g, ''); }
        },
        displayCurrentAmount: {
            get(): string { return this.newGoal.currentAmount; },
            set(value: string) { this.newGoal.currentAmount = value.replace(/[^\d]/g, ''); }
        },
        displayEditTargetAmount: {
            get(): string { return this.editingGoal.targetAmount?.toString() || ''; },
            set(value: string) { if (this.editingGoal) this.editingGoal.targetAmount = parseFloat(value.replace(/[^\d]/g, '')); }
        },
        displayEditCurrentAmount: {
            get(): string { return this.editingGoal.currentAmount?.toString() || ''; },
            set(value: string) { if (this.editingGoal) this.editingGoal.currentAmount = parseFloat(value.replace(/[^\d]/g, '')); }
        },
        displayDepositAmount: {
            get(): string { return this.depositAmount; },
            set(value: string) { this.depositAmount = value.replace(/[^\d]/g, ''); }
        },
        isAddGoalValid(): boolean {
            return this.newGoal.name.trim() !== '' &&
                   parseFloat(this.newGoal.targetAmount) > 0 &&
                   !this.dateError &&
                   this.isValidDate(this.newGoal.targetDate);
        },
        isEditGoalValid(): boolean {
            return this.editingGoal.name?.trim() !== '' &&
                   (this.editingGoal.targetAmount || 0) > 0 &&
                   !this.editDateError &&
                   this.isValidDate(this.editingGoal.targetDate);
        },
        calculatedMonthly(): number {
            const target = parseFloat(this.newGoal.targetAmount) || 0;
            const current = parseFloat(this.newGoal.currentAmount) || 0;
            const months = this.getMonthsDifference(this.newGoal.targetDate);
            if (months <= 0) return 0;
            return Math.ceil((target - current) / months);
        }
    },
    async mounted() {
        await this.loadData();
    },
    methods: {
        async loadData() {
            this.isLoading = true;
            try {
                this.goals = getGoals();
                if (this.goals.length > 0) {
                    this.showPopup = false;
                }
            } finally {
                this.isLoading = false;
            }
        },
        
        formatAmount(amount: number): string {
            return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },
        
        formatDate(dateString: string): string {
            if (!dateString) return '';
            const parts = dateString.split('.');
            if (parts.length === 3) return `${parts[0]}.${parts[1]}.${parts[2]}`;
            return dateString;
        },
        
        getTodayDate(): string {
            const today = new Date();
            return `${today.getDate().toString().padStart(2, '0')}.${(today.getMonth() + 1).toString().padStart(2, '0')}.${today.getFullYear()}`;
        },
        
        isValidDate(dateString: string): boolean {
            const regex = /^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$/;
            if (!regex.test(dateString)) return false;
            const [day, month, year] = dateString.split('.').map(Number);
            const date = new Date(year, month - 1, day);
            return date.getDate() === day && date.getMonth() === month - 1 && date.getFullYear() === year;
        },
        
        getMonthsDifference(dateString: string): number {
            if (!this.isValidDate(dateString)) return 0;
            const [day, month, year] = dateString.split('.').map(Number);
            const target = new Date(year, month - 1, day);
            const today = new Date();
            const months = (target.getFullYear() - today.getFullYear()) * 12 + (target.getMonth() - today.getMonth());
            return months > 0 ? months : 0;
        },
        
        calculateMonthly(goal: Goal): number {
            const months = this.getMonthsDifference(goal.targetDate);
            if (months <= 0) return 0;
            return Math.ceil((goal.targetAmount - goal.currentAmount) / months);
        },
        
        getProgressPercent(goal: Goal): number {
            if (goal.targetAmount === 0) return 0;
            return Math.min((goal.currentAmount / goal.targetAmount) * 100, 100);
        },
        
        getProgressColor(goal: Goal): string {
            const percent = this.getProgressPercent(goal);
            if (percent >= 100) return '#A2E809';
            if (percent >= 75) return '#A2E809';
            if (percent >= 50) return '#FF5E00';
            return '#FF0000';
        },
        
        validateDate() {
            this.dateFocused = false;
            if (!this.isValidDate(this.newGoal.targetDate)) {
                this.dateError = 'Неверный формат даты. Используйте ДД.ММ.ГГГГ';
            } else {
                this.dateError = '';
            }
        },
        
        validateEditDate() {
            this.editDateFocused = false;
            if (!this.isValidDate(this.editingGoal.targetDate)) {
                this.editDateError = 'Неверный формат даты. Используйте ДД.ММ.ГГГГ';
            } else {
                this.editDateError = '';
            }
        },
        
        showAddModalFromPopup() {
            this.showPopup = false;
            this.showAddModal();
        },
        
        showAddModal() {
            this.newGoal = { name: '', targetAmount: '', targetDate: this.getTodayDate(), currentAmount: '' };
            this.dateError = '';
            this.showAddModalFlag = true;
        },
        
        closeAddModal() {
            this.showAddModalFlag = false;
        },
        
        addGoal() {
            if (!this.isAddGoalValid) return;
            addGoal({
                name: this.newGoal.name,
                targetAmount: parseFloat(this.newGoal.targetAmount),
                targetDate: this.newGoal.targetDate,
                currentAmount: parseFloat(this.newGoal.currentAmount) || 0
            });
            this.loadData();
            this.closeAddModal();
        },
        
        showEditModal(goal: Goal) {
            this.editingGoal = { ...goal };
            this.showEditModalFlag = true;
        },
        
        closeEditModal() {
            this.showEditModalFlag = false;
        },
        
        updateGoal() {
            if (!this.isEditGoalValid) return;
            updateGoal(this.editingGoal.id, {
                name: this.editingGoal.name,
                targetAmount: this.editingGoal.targetAmount,
                targetDate: this.editingGoal.targetDate,
                currentAmount: this.editingGoal.currentAmount
            });
            this.loadData();
            this.closeEditModal();
        },
        
        showDepositModal(goal: Goal) {
            this.selectedGoalId = goal.id;
            this.depositAmount = '';
            this.showDepositModalFlag = true;
        },
        
        closeDepositModal() {
            this.showDepositModalFlag = false;
            this.selectedGoalId = null;
            this.depositAmount = '';
        },
        
        addDeposit() {
            if (!this.selectedGoalId || !this.depositAmount || parseFloat(this.depositAmount) <= 0) return;
            depositToGoal(this.selectedGoalId, parseFloat(this.depositAmount));
            this.loadData();
            this.closeDepositModal();
        },
        
        showDeleteModal(goal: Goal) {
            this.deleteGoalId = goal.id;
            this.showDeleteModalFlag = true;
        },
        
        closeDeleteModal() {
            this.showDeleteModalFlag = false;
            this.deleteGoalId = null;
        },
        
        confirmDelete() {
            if (this.deleteGoalId) {
                removeGoal(this.deleteGoalId);
                this.loadData();
                this.closeDeleteModal();
            }
        },
        
        closePopup() {
            this.showPopup = false;
        },
        
        preventClose(event: any) {
            event.cancelBubble = true;
        },
        
        goBack() {
            this.$navigateBack();
        }
    }
});
</script>

<style scoped>
.page-goals {
    background-color: #13131A;
}

.goals-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 24;
}

.add-goal-button {
    background-color: #A2E809;
    color: #13131A;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 12;
    border-radius: 50;
    padding: 8 16;
    height: auto;
    min-height: 0;
}

.add-goal-button:highlighted {
    background-color: #8BCA00;
}

.goal-item {
    background-color: #3F3D5B;
    border-radius: 15;
    padding: 16;
    width: 100%;
}

.goal-name {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
}

.goal-monthly {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.goal-remaining {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    margin-bottom: 4;
    text-align: right;
}

.goal-total {
    color: #969696;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 10;
    text-align: right;
}

.popup-text {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 16;
    text-align: center;
    width: 100%;
}

.budget-button {
    background-color: #964BDC;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 48;
    border-radius: 16;
    margin-top: 12;
    width: 100%;
}

.budget-button:highlighted {
    background-color: #7B3CB0;
}

.add-modal {
    background-color: #1E1D2E;
    border-radius: 16;
    padding: 16;
    width: 90%;
    max-width: 400;
}

.add-modal-title {
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

.input-error {
    border-color: #FF0000;
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

.input-text TextField {
    font-size: 14;
    font-family: 'Inter';
    font-weight: 600;
    color: white;
    background-color: transparent;
    placeholder-color: #B4B4B4;
    vertical-align: middle;
    height: 56;
}

.currency-symbol-no-margin {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
    margin-left: 4;
}

.error-message {
    color: #FF0000;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-left: 0;
}

.recommended-text {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    text-align: left;
}

.add-button {
    height: 56;
    border-radius: 16;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    color: white;
    width: 100%;
}

.add-button.inactive {
    background-color: #969696;
}

.add-button.active {
    background-color: #964BDC;
}

.add-button.active:highlighted {
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