<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-settings">
        <GridLayout rows="*" columns="*" backgroundColor="#13131A">
            
            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (СКРОЛЛИТСЯ) -->
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" paddingLeft="16" paddingRight="16" paddingBottom="200">
                    
                    <!-- Верхняя панель с заголовком -->
                    <GridLayout rows="auto" columns="auto, *" marginTop="16" marginBottom="16">
                        <Image col="0" src="~/assets/images/back.png" 
                               width="20" height="17" 
                               horizontalAlignment="left"
                               @tap="goBack" />
                        
                        <Label col="1" text="Настройки бюджета" 
                               class="settings-title" 
                               marginLeft="24"
                               horizontalAlignment="left" />
                    </GridLayout>
                    
                    <!-- БЛОК ДОХОДОВ -->
                    <StackLayout backgroundColor="#2F2D44" borderRadius="16" 
                                paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16">
                        
                        <!-- Заголовок с plus -->
                        <GridLayout rows="auto" columns="*, auto" marginBottom="16">
                            <Label col="0" text="Доходы в этом месяце" 
                                   class="income-title" 
                                   horizontalAlignment="left" />
                            
                            <Image col="1" src="~/assets/images/plus.png" 
                                   width="20" height="20"
                                   verticalAlignment="center"
                                   @tap="showAddModal" />
                        </GridLayout>
                        
                        <!-- Картинка и общая сумма доходов -->
                        <GridLayout rows="auto" columns="auto, *" marginBottom="16">
                            <Image col="0" src="~/assets/images/money.png" 
                                   width="48" height="48" 
                                   horizontalAlignment="left" />
                            
                            <StackLayout col="1" marginLeft="16" verticalAlignment="center">
                                <Label :text="formatAmount(totalIncome) + ' ₽'" 
                                       class="income-amount" />
                                <Label :text="totalIncomeSources + ' ' + getSourceWord(totalIncomeSources)" 
                                       class="income-source" />
                            </StackLayout>
                        </GridLayout>
                        
                        <!-- Список доходов с возможностью удаления -->
                        <StackLayout v-for="income in incomes" :key="income.id"
                                   class="income-item"
                                   marginBottom="16">
                            
                            <GridLayout rows="auto" columns="*, auto, auto" width="100%">
                                <!-- Информация о доходе -->
                                <StackLayout col="0" verticalAlignment="center">
                                    <Label :text="income.source" 
                                           class="income-item-category" />
                                    <Label :text="formatDate(income.date)" 
                                           class="income-item-date" />
                                </StackLayout>
                                
                                <!-- Сумма дохода -->
                                <Label col="1" :text="formatAmount(income.amount) + ' ₽'" 
                                       class="income-item-amount"
                                       marginRight="16"
                                       verticalAlignment="center" />
                                
                                <!-- Кнопка удаления -->
                                <Image col="2" src="~/assets/images/trash.png" 
                                       width="18" height="18"
                                       verticalAlignment="center"
                                       @tap="showDeleteModal(income)" />
                            </GridLayout>
                        </StackLayout>
                    </StackLayout>
                    
                    <!-- БЛОК РАСПРЕДЕЛЕНИЯ БЮДЖЕТА -->
                    <StackLayout v-if="distributionCategories.length > 0" 
                                marginTop="16" 
                                backgroundColor="#2F2D44" 
                                borderRadius="16" 
                                paddingLeft="16" 
                                paddingRight="16" 
                                paddingTop="16" 
                                paddingBottom="16">
                        
                        <!-- Заголовок с plus -->
                        <GridLayout rows="auto" columns="*, auto" marginBottom="16">
                            <Label col="0" text="Распределение бюджета" 
                                   class="distribution-title" 
                                   horizontalAlignment="left" />
                            
                            <Image col="1" src="~/assets/images/plus.png" 
                                   width="20" height="20"
                                   verticalAlignment="center"
                                   @tap="toggleNewCategory" />
                        </GridLayout>
                        
                        <!-- Карточки категорий (существующие) -->
                        <StackLayout v-for="category in distributionCategories" :key="category.id"
                                   class="distribution-item"
                                   marginBottom="16">
                            
                            <!-- Верхняя часть с категорией и удалением -->
                            <GridLayout rows="auto" columns="auto, *, auto" marginBottom="8">
                                <Image col="0" :src="'~/assets/images/' + category.icon + '.png'" 
                                       width="48" height="48" 
                                       horizontalAlignment="left" />
                                
                                <Label col="1" :text="category.name" 
                                       class="distribution-category"
                                       marginLeft="8"
                                       verticalAlignment="center" />
                                
                                <Image col="2" src="~/assets/images/trash.png" 
                                       width="18" height="18"
                                       verticalAlignment="center"
                                       @tap="showDeleteCategoryModal(category)" />
                            </GridLayout>
                            
                            <!-- Текст-пояснение -->
                            <Label text="Задайте процент от общего дохода для этой категории расходов" 
                                   class="distribution-description"
                                   textWrap="true"
                                   marginBottom="8" />
                            
                            <!-- Поле для ввода процента -->
                            <GridLayout rows="auto" columns="*" 
                                       class="percent-field" 
                                       :class="{ 'percent-focused': category.focused }"
                                       paddingLeft="16" paddingRight="16"
                                       minHeight="56"
                                       marginBottom="8">
                                
                                <StackLayout>
                                    <Label text="От 0 до 100 %" 
                                           class="percent-hint"
                                           :class="{ 'percent-hint-small': category.percent }" />
                                    
                                    <TextField v-model="category.percent"
                                              class="percent-input"
                                              keyboardType="number"
                                              maxLength="3"
                                              @focus="category.focused = true"
                                              @blur="category.focused = false"
                                              @textChange="validatePercent(category)" />
                                </StackLayout>
                            </GridLayout>
                            
                            <!-- Доступная сумма -->
                            <Label :text="'Будет доступно ' + formatAmount(calculateCategoryAmount(category.percent)) + ' ₽'" 
                                   class="distribution-amount"
                                   horizontalAlignment="right" />
                        </StackLayout>
                        
                        <!-- Новая категория (показывается только при showNewCategory) -->
                        <StackLayout v-if="showNewCategory" 
                                   class="distribution-item new-category-item"
                                   marginBottom="16">
                            
                            <!-- Верхняя часть (без картинки, только текст) -->
                            <GridLayout rows="auto" columns="*, auto" marginBottom="8">
                                <Label col="0" text="Новая категория" 
                                       class="distribution-category"
                                       marginLeft="0"
                                       verticalAlignment="center" />
                                
                                <Image col="1" src="~/assets/images/trash.png" 
                                       width="18" height="18"
                                       verticalAlignment="center"
                                       @tap="cancelNewCategory" />
                            </GridLayout>
                            
                            <!-- Текст-пояснение -->
                            <Label text="Задайте процент от общего дохода для новой категории расходов" 
                                   class="distribution-description"
                                   textWrap="true"
                                   marginBottom="8" />
                            
                            <!-- Поле для ввода процента -->
                            <GridLayout rows="auto" columns="*" 
                                       class="percent-field" 
                                       :class="{ 'percent-focused': newCategoryFocused }"
                                       paddingLeft="16" paddingRight="16"
                                       minHeight="56"
                                       marginBottom="8">
                                
                                <StackLayout>
                                    <Label text="От 0 до 100 %" 
                                           class="percent-hint"
                                           :class="{ 'percent-hint-small': newCategoryPercent }" />
                                    
                                    <TextField v-model="newCategoryPercent"
                                              class="percent-input"
                                              keyboardType="number"
                                              maxLength="3"
                                              @focus="newCategoryFocused = true"
                                              @blur="newCategoryFocused = false"
                                              @textChange="validateNewPercent" />
                                </StackLayout>
                            </GridLayout>
                            
                            <!-- Доступная сумма -->
                            <Label :text="'Будет доступно ' + formatAmount(calculateCategoryAmount(newCategoryPercent)) + ' ₽'" 
                                   class="distribution-amount"
                                   horizontalAlignment="right" />
                            
                            <!-- Кнопка выбора категории -->
                            <GridLayout rows="auto" columns="auto, *" 
                                       class="select-category-button"
                                       @tap="showCategorySelector"
                                       marginTop="12">
                                <Image col="0" src="~/assets/images/list.png" 
                                       width="14" height="32" 
                                       marginRight="16" />
                                <Label col="1" text="Выбрать категорию" 
                                       class="select-category-text" />
                            </GridLayout>
                        </StackLayout>
                    </StackLayout>
                    
                    <!-- Дополнительный отступ для контента -->
                    <StackLayout height="120" />
                    
                </FlexboxLayout>
            </ScrollView>
            
            <!-- СЛОЙ 2: СТАТУС БАР И КНОПКИ (ПРИБИТЫ К НИЗУ) -->
            <GridLayout v-if="distributionCategories.length > 0" 
                       row="0" col="0" 
                       rows="auto, auto, auto" 
                       columns="*"
                       verticalAlignment="bottom"
                       backgroundColor="#13131A"
                       borderTopWidth="2"
                       borderTopColor="#969696"
                       paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16"
                       zIndex="10">
                
                <!-- Статус бар -->
                <StackLayout row="0" marginBottom="16">
                    <Label :text="distributionStatusText" 
                           :class="['distribution-status-text', distributionStatusClass]"
                           horizontalAlignment="right"
                           marginBottom="8" />
                    
                    <GridLayout rows="auto" columns="*" height="3">
                        <GridLayout col="0" backgroundColor="#969696" height="3" width="100%" />
                        <GridLayout col="0" :backgroundColor="distributionBarColor" 
                                   :width="distributionPercentage + '%'" height="3" 
                                   horizontalAlignment="left" />
                    </GridLayout>
                </StackLayout>
                
                <!-- Кнопки -->
                <Button row="1" text="Сохранить" 
                        :class="['save-button', isDistributionValid ? 'active' : 'inactive']"
                        :isEnabled="isDistributionValid"
                        @tap="saveDistribution"
                        marginBottom="16" />

                <Button row="2" text="Отмена" 
                        class="cancel-button"
                        @tap="closeDistribution" />
            </GridLayout>
            
            <!-- ОСТАЛЬНЫЕ МОДАЛЬНЫЕ ОКНА (БЕЗ ИЗМЕНЕНИЙ) -->
            <!-- СЛОЙ 3: МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ ДОХОДА -->
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeDeleteModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="delete-modal" @tap="preventClose">
                    <Label text="Удалить доход без возможности восстановления?" 
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
            
            <!-- СЛОЙ 4: МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ КАТЕГОРИИ -->
            <GridLayout v-if="showDeleteCategoryModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeDeleteCategoryModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showDeleteCategoryModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="delete-modal" @tap="preventClose">
                    <Label text="Удалить категорию без возможности восстановления?" 
                           class="delete-modal-text"
                           textWrap="true"
                           marginBottom="16" />
                    
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end">
                        <Label text="Отмена" 
                               class="cancel-text"
                               marginRight="40"
                               @tap="closeDeleteCategoryModal" />
                        
                        <Label text="Удалить" 
                               class="delete-text"
                               @tap="confirmDeleteCategory" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>
            
            <!-- СЛОЙ 5: МОДАЛЬНОЕ ОКНО ДОБАВЛЕНИЯ ДОХОДА -->
            <GridLayout v-if="showAddModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAddModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showAddModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="add-modal" @tap="preventClose">
                    
                    <!-- Заголовок -->
                    <Label text="Новый доход" 
                           class="add-modal-title"
                           marginBottom="16" />
                    
                    <!-- Поле Источник дохода -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': sourceFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            
                            <TextField v-model="newIncome.source"
                                      :hint="!newIncome.source ? 'Источник дохода' : ''"
                                      class="input-text"
                                      @focus="sourceFocused = true"
                                      @blur="sourceFocused = false"
                                      autocorrect="false"
                                      autocapitalizationType="sentences" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Размер дохода с символом ₽ -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': amountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            
                            <TextField col="0" 
                                      v-model="displayAmount"
                                      :hint="!newIncome.amount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
                                      :width="amountWidth"
                                      @focus="amountFocused = true"
                                      @blur="amountFocused = false" />
                            
                            <Label col="1" text="₽" 
                                   class="currency-symbol"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Дата -->
                    <StackLayout class="input-wrapper" marginBottom="5">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': dateFocused, 'input-error': dateError }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            
                            <TextField v-model="newIncome.date"
                                      :hint="!newIncome.date ? 'ДД.ММ.ГГГГ' : ''"
                                      class="input-text"
                                      @focus="dateFocused = true"
                                      @blur="validateDate" />
                        </GridLayout>
                        
                        <Label v-if="dateError" 
                               :text="dateError"
                               class="error-message"
                               marginTop="4" />
                    </StackLayout>
                    
                    <!-- Кнопки -->
                    <Button text="Добавить доход" 
                            :class="['add-button', isAddFormValid ? 'active' : 'inactive']"
                            :isEnabled="isAddFormValid"
                            @tap="addNewIncome"
                            marginTop="16" />

                    <Button text="Отмена" 
                            class="cancel-button"
                            @tap="closeAddModal"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>
            
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { getIncomes, getTotalIncome, getTotalIncomeSources, Income, removeIncome, addIncome } from './data/budget';
import { 
    getBudgetCategories, 
    addBudgetCategory, 
    removeBudgetCategory, 
    updateBudgetCategoryPercent,
    BudgetCategory 
} from './data/budget-categories';
import CategorySelector from './CategorySelector.vue';

export default defineComponent({
    components: {
        CategorySelector
    },
    data() {
        return {
            activeTab: 'profile',
            incomes: [] as Income[],
            distributionCategories: [] as BudgetCategory[],
            showDeleteModalFlag: false,
            showDeleteCategoryModalFlag: false,
            showAddModalFlag: false,
            showNewCategory: false,
            newCategoryPercent: '',
            newCategoryFocused: false,
            selectedIncome: null as Income | null,
            selectedCategory: null as BudgetCategory | null,
            sourceFocused: false,
            amountFocused: false,
            dateFocused: false,
            dateError: '',
            newIncome: {
                source: '',
                amount: '',
                date: this.getTodayDate()
            },
            updateTrigger: 0
        };
    },
    computed: {
        totalIncome(): number {
            this.updateTrigger;
            return getTotalIncome();
        },
        totalIncomeSources(): number {
            this.updateTrigger;
            return getTotalIncomeSources();
        },
        displayAmount: {
            get(): string {
                return this.newIncome.amount;
            },
            set(value: string) {
                const numericValue = value.replace(/[^\d]/g, '');
                this.newIncome.amount = numericValue;
            }
        },
        amountWidth(): number {
            const baseWidth = 20;
            const charWidth = 8;
            const length = this.newIncome.amount.length || 1;
            return baseWidth + (length * charWidth);
        },
        isAddFormValid(): boolean {
            return this.newIncome.source.trim() !== '' && 
                   this.newIncome.amount.trim() !== '' && 
                   parseFloat(this.newIncome.amount) > 0 &&
                   !this.dateError &&
                   this.isValidDate(this.newIncome.date);
        },
        totalDistributionPercent(): number {
            let total = this.distributionCategories.reduce((sum, cat) => {
                const percent = parseFloat(cat.percent) || 0;
                return sum + percent;
            }, 0);
            
            if (this.showNewCategory && this.newCategoryPercent) {
                total += parseFloat(this.newCategoryPercent) || 0;
            }
            
            return total;
        },
        isDistributionValid(): boolean {
            return Math.abs(this.totalDistributionPercent - 100) < 0.01;
        },
        distributionBarColor(): string {
            return this.isDistributionValid ? '#A2E809' : '#FF0000';
        },
        distributionPercentage(): number {
            return Math.min(this.totalDistributionPercent, 100);
        },
        distributionStatusText(): string {
            if (this.isDistributionValid) {
                return '100 %';
            } else {
                const remaining = (100 - this.totalDistributionPercent).toFixed(1);
                return `Осталось распределить ${remaining} %`;
            }
        },
        distributionStatusClass(): string {
            return this.isDistributionValid ? 'status-green' : 'status-red';
        }
    },
    created() {
        this.loadIncomes();
        this.loadCategories();
    },
    methods: {
        loadIncomes(): void {
            this.incomes = getIncomes();
            this.updateTrigger++;
        },
        
        loadCategories(): void {
            this.distributionCategories = getBudgetCategories();
        },
        
        formatAmount(amount: number): string {
            return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },
        
        formatDate(date: string): string {
            return date;
        },
        
        getSourceWord(count: number): string {
            if (count === 1) return 'источник';
            if (count >= 2 && count <= 4) return 'источника';
            return 'источников';
        },
        
        getTodayDate(): string {
            const today = new Date();
            const day = String(today.getDate()).padStart(2, '0');
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const year = today.getFullYear();
            return `${day}.${month}.${year}`;
        },
        
        isValidDate(dateString: string): boolean {
            const dateRegex = /^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$/;
            
            if (!dateRegex.test(dateString)) {
                return false;
            }
            
            const [day, month, year] = dateString.split('.').map(Number);
            const date = new Date(year, month - 1, day);
            return date.getDate() === day && 
                   date.getMonth() === month - 1 && 
                   date.getFullYear() === year;
        },
        
        validateDate(): void {
            this.dateFocused = false;
            
            if (!this.isValidDate(this.newIncome.date)) {
                this.dateError = 'Введите корректную дату';
            } else {
                this.dateError = '';
            }
        },
        
        calculateCategoryAmount(percent: string): number {
            const percentValue = parseFloat(percent) || 0;
            return (this.totalIncome * percentValue) / 100;
        },
        
        calculateDistribution(): void {
            this.updateTrigger++;
        },
        
        validatePercent(category: BudgetCategory): void {
            let value = parseInt(category.percent);
            if (value > 100) {
                category.percent = '100';
            } else if (value < 0 || isNaN(value)) {
                category.percent = '0';
            }
            this.calculateDistribution();
        },
        
        validateNewPercent(): void {
            let value = parseInt(this.newCategoryPercent);
            if (value > 100) {
                this.newCategoryPercent = '100';
            } else if (value < 0 || isNaN(value)) {
                this.newCategoryPercent = '0';
            }
            this.calculateDistribution();
        },
        
        toggleNewCategory(): void {
            this.showNewCategory = !this.showNewCategory;
            if (!this.showNewCategory) {
                this.newCategoryPercent = '';
                this.newCategoryFocused = false;
            }
        },
        
        cancelNewCategory(): void {
            this.showNewCategory = false;
            this.newCategoryPercent = '';
            this.newCategoryFocused = false;
        },
        
        showDeleteModal(income: Income): void {
            this.selectedIncome = income;
            this.showDeleteModalFlag = true;
        },
        
        closeDeleteModal(): void {
            this.showDeleteModalFlag = false;
            this.selectedIncome = null;
        },
        
        confirmDelete(): void {
            if (this.selectedIncome) {
                const removed = removeIncome(this.selectedIncome.id);
                if (removed) {
                    this.loadIncomes();
                    console.log(`Income ${this.selectedIncome.id} deleted`);
                }
                this.closeDeleteModal();
            }
        },
        
        showDeleteCategoryModal(category: BudgetCategory): void {
            this.selectedCategory = category;
            this.showDeleteCategoryModalFlag = true;
        },
        
        closeDeleteCategoryModal(): void {
            this.showDeleteCategoryModalFlag = false;
            this.selectedCategory = null;
        },
        
        confirmDeleteCategory(): void {
            if (this.selectedCategory) {
                const removed = removeBudgetCategory(this.selectedCategory.id);
                if (removed) {
                    this.loadCategories();
                    console.log(`Category ${this.selectedCategory.id} deleted`);
                }
                this.closeDeleteCategoryModal();
            }
        },
        
        showAddModal(): void {
            this.resetNewIncome();
            this.showAddModalFlag = true;
        },
        
        closeAddModal(): void {
            this.showAddModalFlag = false;
            this.resetNewIncome();
        },

        showCategorySelector(): void {
            this.$navigateTo(CategorySelector, {
                transition: { name: 'slideLeft', duration: 300 },
                props: {
                    onCategorySelected: (category: any) => {
                        if (category) {
                            const newCategory = {
                                name: category.name,
                                icon: category.icon,
                                percent: this.newCategoryPercent || '0'
                            };
                            addBudgetCategory(newCategory);
                            this.loadCategories();
                            this.showNewCategory = false;
                            this.newCategoryPercent = '';
                        }
                    }
                }
            })
        },
        
        addNewIncome(): void {
            if (this.isAddFormValid) {
                const amount = parseFloat(this.newIncome.amount);
                const newId = addIncome({
                    source: this.newIncome.source,
                    amount: amount,
                    date: this.newIncome.date
                });
                
                if (newId) {
                    this.loadIncomes();
                    console.log('New income added:', newId);
                    this.closeAddModal();
                }
            }
        },
        
        saveDistribution(): void {
            if (this.isDistributionValid) {
                this.distributionCategories.forEach(cat => {
                    updateBudgetCategoryPercent(cat.id, cat.percent);
                });
                console.log('Distribution saved');
            }
        },
        
        closeDistribution(): void {
            this.loadCategories();
            this.showNewCategory = false;
            this.newCategoryPercent = '';
        },
        
        resetNewIncome(): void {
            this.newIncome = {
                source: '',
                amount: '',
                date: this.getTodayDate()
            };
            this.sourceFocused = false;
            this.amountFocused = false;
            this.dateFocused = false;
            this.dateError = '';
        },
        
        preventClose(event: any): void {
            event.cancelBubble = true;
        },
        
        goBack(): void {
            this.$navigateBack();
        }
    }
});
</script>

<style scoped>
.page-settings {
    background-color: #13131A;
}

.settings-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

/* Стили для блока доходов */
.income-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.income-amount {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.income-source {
    color: #969696;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 10;
    margin-top: 4;
}

.income-item {
    background-color: #3F3D5B;
    border-radius: 12;
    padding: 12;
    margin-bottom: 16;
}

.income-item-category {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
}

.income-item-date {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-top: 2;
}

.income-item-amount {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
}

/* Стили для блока распределения */
.distribution-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.distribution-item {
    background-color: #3F3D5B;
    border-radius: 12;
    padding: 16;
}

.new-category-item {
    border-width: 2;
    border-color: #964BDC;
    border-style: dashed;
}

.distribution-category {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.distribution-description {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 12;
    text-wrap: true;
}

.percent-field {
    background-color: #2F2D44;
    border-radius: 16;
    border-width: 2;
    border-color: #2F2D44;
    width: 100%;
    padding: 8 16;
}

.percent-field StackLayout {
    width: 100%;
}

.percent-focused {
    border-color: #964BDC;
}

.percent-hint {
    color: #969696;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 12;
    margin-bottom: 2;
}

.percent-hint-small {
    font-size: 10;
}

.percent-input {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    background-color: transparent;
    padding: 0;
}

.percent-input TextField {
    font-size: 14;
    font-family: 'Inter';
    font-weight: bold;
    color: white;
    background-color: transparent;
    padding: 0;
}

.distribution-amount {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.select-category-button {
    padding: 8 0;
}

.select-category-text {
    color: #964BDC;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.distribution-status-text {
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
}

.status-green {
    color: #A2E809;
}

.status-red {
    color: #FF0000;
}

.save-button {
    height: 56;
    border-radius: 16;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    color: white;
    width: 100%;
}

.save-button.inactive {
    background-color: #969696;
}

.save-button.active {
    background-color: #964BDC;
}

.save-button.active:highlighted {
    background-color: #7B3CB0;
}

.cancel-button {
    background-color: #FF0000;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 56;
    border-radius: 16;
    width: 100%;
}

.cancel-button:highlighted {
    background-color: #CC0000;
}

/* Стили для модальных окон */
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

/* Стили для модального окна добавления */
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

.currency-symbol {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
}

.error-message {
    color: #FF0000;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-left: 0;
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

.popup-text {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 16;
    text-align: center;
    width: 100%;
}
</style>