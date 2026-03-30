<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-history">
        <GridLayout rows="*, auto" columns="*" backgroundColor="#13131A">
            
            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (СКРОЛЛИТСЯ) -->
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" paddingLeft="16" paddingRight="16" paddingBottom="120">
                    
                    <!-- Верхняя панель с заголовком -->
                    <GridLayout rows="auto" columns="auto, *" marginTop="16" marginBottom="24">
                        <Image col="0" src="~/assets/images/back.png" 
                               width="20" height="17" 
                               horizontalAlignment="left"
                               @tap="goBack" />
                        
                        <Label col="1" text="История" 
                               class="page-title" 
                               marginLeft="24"
                               horizontalAlignment="left" />
                    </GridLayout>
                    
                    <!-- Поисковая строка и фильтр -->
                    <GridLayout rows="auto" columns="*, auto" marginBottom="16">
                        <!-- Поле поиска -->
                        <GridLayout col="0" class="search-field" 
                                   :class="{ 'search-focused': searchFocused || searchText }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            <GridLayout columns="auto, *" width="100%" alignItems="center">
                                <Image col="0" src="~/assets/images/search.png" 
                                       width="16" height="16" />
                                <TextField col="1" 
                                          v-model="searchText"
                                          hint="Найти транзакцию"
                                          class="search-input"
                                          @focus="searchFocused = true"
                                          @blur="searchFocused = false"
                                          @textChange="onSearchChange"
                                          marginLeft="8" />
                            </GridLayout>
                        </GridLayout>
                        
                        <!-- Кнопка фильтра -->
                        <Image col="1" src="~/assets/images/filter.png" 
                               width="20" height="20" 
                               marginLeft="16"
                               verticalAlignment="center"
                               @tap="showFilterModal" />
                    </GridLayout>
                    
                    <!-- Активные фильтры (каждый фильтр отдельно с двумя колонками, ширина по контенту) -->
                    <FlexboxLayout v-if="activeFilters.length > 0" flexDirection="row" flexWrap="wrap" marginBottom="16">
                        <GridLayout v-for="filter in activeFilters" :key="filter"
                                rows="auto" columns="auto, auto"
                                class="filter-chip"
                                marginRight="8" marginBottom="8"
                                paddingLeft="12" paddingRight="12"
                                paddingTop="4" paddingBottom="4">
                            <Label col="0" :text="filter" 
                                class="filter-chip-text" />
                            <Image col="1" src="~/assets/images/delete.png" 
                                width="8" height="8"
                                marginLeft="8"
                                @tap="removeFilter(filter)" />
                        </GridLayout>
                    </FlexboxLayout>
                    
                    <!-- Блоки транзакций по датам -->
                    <StackLayout>
                        <!-- Сегодня -->
                        <StackLayout v-if="groupedTransactions.today.length > 0" marginBottom="16">
                            <StackLayout class="section-container" padding="16" backgroundColor="#2F2D44" borderRadius="16">
                                <Label text="Сегодня" class="section-title" marginBottom="12" />
                                
                                <StackLayout v-for="transaction in groupedTransactions.today" :key="transaction.id"
                                           class="transaction-item"
                                           marginBottom="12">
                                    
                                    <GridLayout rows="auto" columns="auto, *, auto, auto" width="100%">
                                        <Image col="0" src="~/assets/images/shop.png" 
                                               width="48" height="48" 
                                               horizontalAlignment="left" />
                                        
                                        <StackLayout col="1" marginLeft="12" verticalAlignment="center">
                                            <Label :text="transaction.name" class="transaction-name" />
                                            <Label :text="transaction.date" class="transaction-date" />
                                        </StackLayout>
                                        
                                        <Label col="2" :text="'- ' + formatAmount(transaction.amount) + ' ₽'" 
                                               class="transaction-amount"
                                               verticalAlignment="center"
                                               marginRight="16" />
                                        
                                        <Image col="3" src="~/assets/images/trash.png" 
                                               width="18" height="18"
                                               verticalAlignment="center"
                                               @tap="showDeleteModal(transaction)" />
                                    </GridLayout>
                                </StackLayout>
                            </StackLayout>
                        </StackLayout>
                        
                        <!-- Вчера -->
                        <StackLayout v-if="groupedTransactions.yesterday.length > 0" marginBottom="16">
                            <StackLayout class="section-container" padding="16" backgroundColor="#2F2D44" borderRadius="16">
                                <Label text="Вчера" class="section-title" marginBottom="12" />
                                
                                <StackLayout v-for="transaction in groupedTransactions.yesterday" :key="transaction.id"
                                           class="transaction-item"
                                           marginBottom="12">
                                    
                                    <GridLayout rows="auto" columns="auto, *, auto, auto" width="100%">
                                        <Image col="0" src="~/assets/images/shop.png" 
                                               width="48" height="48" 
                                               horizontalAlignment="left" />
                                        
                                        <StackLayout col="1" marginLeft="12" verticalAlignment="center">
                                            <Label :text="transaction.name" class="transaction-name" />
                                            <Label :text="transaction.date" class="transaction-date" />
                                        </StackLayout>
                                        
                                        <Label col="2" :text="'- ' + formatAmount(transaction.amount) + ' ₽'" 
                                               class="transaction-amount"
                                               verticalAlignment="center"
                                               marginRight="16" />
                                        
                                        <Image col="3" src="~/assets/images/trash.png" 
                                               width="18" height="18"
                                               verticalAlignment="center"
                                               @tap="showDeleteModal(transaction)" />
                                    </GridLayout>
                                </StackLayout>
                            </StackLayout>
                        </StackLayout>
                        
                        <!-- Ранее -->
                        <StackLayout v-if="groupedTransactions.earlier.length > 0" marginBottom="16">
                            <StackLayout class="section-container" padding="16" backgroundColor="#2F2D44" borderRadius="16">
                                <Label text="Ранее" class="section-title" marginBottom="12" />
                                
                                <StackLayout v-for="transaction in groupedTransactions.earlier" :key="transaction.id"
                                           class="transaction-item"
                                           marginBottom="12">
                                    
                                    <GridLayout rows="auto" columns="auto, *, auto, auto" width="100%">
                                        <Image col="0" src="~/assets/images/shop.png" 
                                               width="48" height="48" 
                                               horizontalAlignment="left" />
                                        
                                        <StackLayout col="1" marginLeft="12" verticalAlignment="center">
                                            <Label :text="transaction.name" class="transaction-name" />
                                            <Label :text="transaction.date" class="transaction-date" />
                                        </StackLayout>
                                        
                                        <Label col="2" :text="'- ' + formatAmount(transaction.amount) + ' ₽'" 
                                               class="transaction-amount"
                                               verticalAlignment="center"
                                               marginRight="16" />
                                        
                                        <Image col="3" src="~/assets/images/trash.png" 
                                               width="18" height="18"
                                               verticalAlignment="center"
                                               @tap="showDeleteModal(transaction)" />
                                    </GridLayout>
                                </StackLayout>
                            </StackLayout>
                        </StackLayout>
                        
                        <!-- Сообщение если нет транзакций -->
                        <Label v-if="totalTransactionsCount === 0" 
                               text="Нет транзакций" 
                               class="empty-text"
                               textAlignment="center"
                               marginTop="32" />
                    </StackLayout>
                    
                </FlexboxLayout>
            </ScrollView>
            
            <!-- СЛОЙ 2: КНОПКА ДОБАВИТЬ ТРАТУ (НАД МЕНЮ, ПОВЕРХ ВСЕГО) -->
            <GridLayout row="0" col="0" rows="auto" columns="*"
                       verticalAlignment="bottom"
                       paddingLeft="16" paddingRight="16" paddingBottom="16"
                       zIndex="100">
                
                <Button text="Добавить трату" 
                        class="add-expense-button"
                        @tap="showAddExpenseModal" />
            </GridLayout>
            
            <!-- СЛОЙ 3: МЕНЮ -->
            <Menu row="1" col="0" 
                  verticalAlignment="bottom"
                  :activeTab="activeTab" 
                  @update:activeTab="activeTab = $event"
                  zIndex="1" />
            
            <!-- МОДАЛЬНОЕ ОКНО ФИЛЬТРОВ -->
            <GridLayout v-if="showFilterModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeFilterModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showFilterModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="filter-modal" @tap="preventClose">
                    
                    <Label text="Фильтры" class="filter-modal-title" marginBottom="16" />
                    
                    <StackLayout v-for="category in availableCategories" :key="category"
                               class="filter-option"
                               marginBottom="12"
                               @tap="toggleFilter(category)">
                        
                        <GridLayout rows="auto" columns="auto, *" width="100%">
                            <GridLayout col="0" width="20" height="20" 
                                       class="border-3 rounded-full mr-4 items-center justify-center"
                                       :class="[isFilterActive(category) ? 'border-[#964BDC]' : 'border-[#969696]']">
                                <GridLayout v-if="isFilterActive(category)" 
                                           width="10" height="10" 
                                           class="bg-[#964BDC] rounded-full" />
                            </GridLayout>
                            
                            <Label col="1" :text="category" 
                                   class="filter-option-text"
                                   verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <Button text="Применить" 
                            class="apply-button"
                            @tap="applyFilters"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>
            
            <!-- МОДАЛЬНОЕ ОКНО ДОБАВЛЕНИЯ ТРАТЫ -->
            <GridLayout v-if="showAddModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAddModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showAddModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="add-modal" @tap="preventClose">
                    
                    <Label text="Новая трата" class="add-modal-title" marginBottom="16" />
                    
                    <!-- Поле Название траты -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': nameFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            <TextField v-model="newExpense.name"
                                      hint="Название траты"
                                      class="input-text"
                                      @focus="nameFocused = true"
                                      @blur="nameFocused = false" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Сумма -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto" 
                                   class="input-field" 
                                   :class="{ 'input-focused': amountFocused }"
                                   paddingLeft="16"
                                   height="56">
                            <TextField col="0" 
                                      v-model="displayAmount"
                                      hint="0"
                                      class="input-text"
                                      keyboardType="number"
                                      @focus="amountFocused = true"
                                      @blur="amountFocused = false" />
                            <Label col="1" text="₽" class="currency-symbol" verticalAlignment="center" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Категория -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': categoryFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            <TextField v-model="newExpense.category"
                                      hint="Категория"
                                      class="input-text"
                                      @focus="categoryFocused = true"
                                      @blur="categoryFocused = false" />
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Поле Дата -->
                    <StackLayout class="input-wrapper" marginBottom="5">
                        <GridLayout rows="auto" columns="*" 
                                   class="input-field" 
                                   :class="{ 'input-focused': dateFocused, 'input-error': dateError }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56">
                            <TextField v-model="newExpense.date"
                                      hint="ДД.ММ.ГГГГ"
                                      class="input-text"
                                      @focus="dateFocused = true"
                                      @blur="validateDate" />
                        </GridLayout>
                        <Label v-if="dateError" :text="dateError" class="error-message" marginTop="4" />
                    </StackLayout>
                    
                    <!-- Кнопки -->
                    <Button text="Добавить трату" 
                            :class="['add-button', isFormValid ? 'active' : 'inactive']"
                            :isEnabled="isFormValid"
                            @tap="addNewExpense"
                            marginTop="16" />
                    <Button text="Отмена" class="cancel-button-modal" @tap="closeAddModal" marginTop="16" />
                </StackLayout>
            </GridLayout>
            
            <!-- МОДАЛЬНОЕ ОКНО УДАЛЕНИЯ -->
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeDeleteModal"
                       zIndex="1000" />
            
            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="delete-modal" @tap="preventClose">
                    <Label text="Вы действительно хотите удалить транзакцию?" 
                           class="delete-modal-text"
                           textWrap="true"
                           marginBottom="24" />
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end">
                        <Label text="Отмена" class="cancel-text" marginRight="40" @tap="closeDeleteModal" />
                        <Label text="Удалить" class="delete-text" @tap="closeDeleteModal" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>
            
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import Menu from './Menu.vue';
import { getAllTransactions, addTransaction, searchTransactions, groupTransactionsByDate, Transaction } from './data/transactions';

export default defineComponent({
    components: {
        Menu
    },
    data() {
        return {
            activeTab: 'history',
            allTransactions: [] as Transaction[],
            filteredTransactions: [] as Transaction[],
            groupedTransactions: {
                today: [] as Transaction[],
                yesterday: [] as Transaction[],
                earlier: [] as Transaction[]
            },
            searchText: '',
            searchFocused: false,
            activeFilters: [] as string[],
            availableCategories: ['Продукты', 'Кафе', 'Транспорт', 'Развлечения', 'Здоровье', 'Покупки'],
            
            showFilterModalFlag: false,
            showAddModalFlag: false,
            showDeleteModalFlag: false,
            
            nameFocused: false,
            amountFocused: false,
            categoryFocused: false,
            dateFocused: false,
            dateError: '',
            
            newExpense: {
                name: '',
                amount: '',
                category: '',
                date: this.getTodayDate()
            },
            
            selectedTransaction: null as Transaction | null
        };
    },
    computed: {
        displayAmount: {
            get(): string { return this.newExpense.amount; },
            set(value: string) { this.newExpense.amount = value.replace(/[^\d]/g, ''); }
        },
        isFormValid(): boolean {
            return this.newExpense.name.trim() !== '' && 
                   this.newExpense.amount !== '' && 
                   parseFloat(this.newExpense.amount) > 0 &&
                   this.newExpense.category.trim() !== '' &&
                   !this.dateError &&
                   this.isValidDate(this.newExpense.date);
        },
        totalTransactionsCount(): number {
            return this.filteredTransactions.length;
        }
    },
    created() {
        this.loadTransactions();
    },
    methods: {
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
        
        validateDate() {
            this.dateFocused = false;
            if (!this.isValidDate(this.newExpense.date)) {
                this.dateError = 'Неверный формат даты. Используйте ДД.ММ.ГГГГ';
            } else {
                this.dateError = '';
            }
        },
        
        loadTransactions() {
            this.allTransactions = getAllTransactions();
            this.applyFiltersAndSearch();
        },
        
        applyFiltersAndSearch() {
            let filtered = [...this.allTransactions];
            
            if (this.searchText) {
                filtered = searchTransactions(this.searchText);
            }
            
            if (this.activeFilters.length > 0) {
                filtered = filtered.filter(t => 
                    this.activeFilters.includes(t.categoryName || '')
                );
            }
            
            this.filteredTransactions = filtered;
            this.groupedTransactions = groupTransactionsByDate(this.filteredTransactions);
        },
        
        onSearchChange() {
            this.applyFiltersAndSearch();
        },
        
        formatAmount(amount: number): string {
            return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },
        
        showFilterModal() {
            this.showFilterModalFlag = true;
        },
        
        closeFilterModal() {
            this.showFilterModalFlag = false;
        },
        
        isFilterActive(category: string): boolean {
            return this.activeFilters.includes(category);
        },
        
        toggleFilter(category: string) {
            const index = this.activeFilters.indexOf(category);
            if (index !== -1) {
                this.activeFilters.splice(index, 1);
            } else {
                this.activeFilters.push(category);
            }
        },
        
        applyFilters() {
            this.closeFilterModal();
            this.applyFiltersAndSearch();
        },
        
        removeFilter(filter: string) {
            const index = this.activeFilters.indexOf(filter);
            if (index !== -1) {
                this.activeFilters.splice(index, 1);
                this.applyFiltersAndSearch();
            }
        },
        
        showDeleteModal(transaction: Transaction) {
            this.selectedTransaction = transaction;
            this.showDeleteModalFlag = true;
        },
        
        closeDeleteModal() {
            this.showDeleteModalFlag = false;
            this.selectedTransaction = null;
        },
        
        showAddExpenseModal() {
            this.resetForm();
            this.showAddModalFlag = true;
        },
        
        closeAddModal() {
            this.showAddModalFlag = false;
            this.resetForm();
        },
        
        resetForm() {
            this.newExpense = {
                name: '',
                amount: '',
                category: '',
                date: this.getTodayDate()
            };
            this.nameFocused = false;
            this.amountFocused = false;
            this.categoryFocused = false;
            this.dateFocused = false;
            this.dateError = '';
        },
        
        addNewExpense() {
            if (this.isFormValid) {
                const newTransaction = addTransaction({
                    name: this.newExpense.name,
                    amount: parseFloat(this.newExpense.amount),
                    date: this.newExpense.date,
                    categoryId: 0,
                    categoryName: this.newExpense.category
                });
                this.loadTransactions();
                this.closeAddModal();
                console.log('New expense added:', newTransaction);
            }
        },
        
        goBack() {
            this.$navigateBack();
        },
        
        preventClose(event: any) {
            event.cancelBubble = true;
        }
    }
});
</script>

<style scoped>
.page-history {
    background-color: #13131A;
}

.page-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.search-field {
    background-color: #2F2D44;
    border-radius: 16;
    border-width: 2;
    border-color: #2F2D44;
    height: 56;
}

.search-focused {
    border-color: #964BDC;
}

.search-input {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
    background-color: transparent;
    padding: 0;
    placeholder-color: #969696;
}

.filter-chip {
    background-color: white;
    border-radius: 15;
    padding: 4 12;
    flex-direction: row;
    align-items: center;
    height: auto;
    width: auto;
}

.filter-chip-text {
    color: #969696;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 12;
    text-align: center;
}

.section-container {
    background-color: #2F2D44;
    border-radius: 16;
    padding: 16;
}

.section-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.transaction-item {
    background-color: #3F3D5B;
    border-radius: 12;
    padding: 12;
    margin-bottom: 12;
}

.transaction-item:last-child {
    margin-bottom: 0;
}

.transaction-name {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.transaction-date {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-top: 2;
}

.transaction-amount {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
}

.empty-text {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
}

.add-expense-button {
    background-color: #964BDC;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 55;
    border-radius: 16;
    width: 100%;
}

.add-expense-button:highlighted {
    background-color: #7B3CB0;
}

.filter-modal {
    background-color: #1E1D2E;
    border-radius: 16;
    padding: 16;
    width: 90%;
    max-width: 400;
}

.filter-modal-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
    text-align: left;
}

.filter-option {
    padding: 8 0;
}

.filter-option-text {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
}

.apply-button {
    background-color: #964BDC;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 48;
    border-radius: 16;
    width: 100%;
}

.apply-button:highlighted {
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

.currency-symbol {
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