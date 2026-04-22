<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView v-if="!isLoading" row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">

                    <FlexboxLayout alignItems="center" class="mb-6">
                        <Label text="История" class="text-white font-inter font-semibold text-2xl" />
                    </FlexboxLayout>

                    <GridLayout rows="auto" columns="*, auto" class="mb-4">
                        <GridLayout col="0" class="bg-[#262626] rounded-2xl px-4 h-14 items-center" :class="searchFocused || searchText ? 'border-[#964BDC] border-5' : ''">
                            <FlexboxLayout flexDirection="row" alignItems="center" class="w-full">
                                <Image src="~/assets/images/search.png" width="14" height="14" class="mr-2" />
                                <TextField v-model="searchText" hint="Поиск" hintColor="#FFFFFF" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" @focus="searchFocused = true" @blur="searchFocused = false" @textChange="onSearchChange" />
                            </FlexboxLayout>
                        </GridLayout>

                        <Image col="1" src="~/assets/images/filter.png" width="20" height="20" class="ml-4 self-center" @tap="showFilterModal" />
                    </GridLayout>

                    <FlexboxLayout v-if="activeFilters.length > 0" flexDirection="row" flexWrap="wrap" class="mb-4">
                        <GridLayout v-for="filter in activeFilters" :key="filter" rows="auto" columns="auto, auto" class="bg-[#1E1D2E] rounded-2xl px-3 py-1 mr-2 mb-2 items-center" @tap="removeFilter(filter)">
                            <Label col="0" :text="filter" class="text-white font-inter font-semibold text-xs" />
                            <Image col="1" src="~/assets/images/close.png" width="8" height="8" class="ml-2" />
                        </GridLayout>
                    </FlexboxLayout>

                    <StackLayout v-if="groupedTransactions.today.length > 0" class="bg-[#1E1E1E] rounded-3xl px-4 pt-4 pb-3 mb-4">
                        <Label text="Сегодня" class="text-white font-inter font-semibold text-xl mb-3" />
                        <StackLayout v-for="transaction in groupedTransactions.today" :key="transaction.id" class="bg-[#262626] rounded-2xl p-4 mb-3">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center">
                                <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                    <Image :src="getCategoryIcon(transaction.categoryName)" width="40" height="40" />
                                    <StackLayout class="ml-3">
                                        <Label :text="transaction.name" class="text-white font-inter font-semibold text-sm" />
                                        <Label :text="transaction.date" class="text-[#8A8A8A] font-inter font-normal text-xs mt-1" />
                                    </StackLayout>
                                </FlexboxLayout>
                                <FlexboxLayout flexDirection="row" alignItems="center">
                                    <Label :text="'- ' + formatAmount(transaction.amount) + ' ₽'" class="text-white font-inter font-semibold text-sm mr-4" />
                                    <Image src="~/assets/images/trash.png" width="24" height="24" @tap="showDeleteModal(transaction)" />
                                </FlexboxLayout>
                            </FlexboxLayout>
                        </StackLayout>
                    </StackLayout>

                    <StackLayout v-if="groupedTransactions.yesterday.length > 0" class="bg-[#1E1E1E] rounded-3xl px-4 pt-4 pb-3 mb-4">
                        <Label text="Вчера" class="text-white font-inter font-semibold text-xl mb-3" />
                        <StackLayout v-for="transaction in groupedTransactions.yesterday" :key="transaction.id" class="bg-[#262626] rounded-2xl p-4 mb-3">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center">
                                <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                    <Image :src="getCategoryIcon(transaction.categoryName)" width="40" height="40" />
                                    <StackLayout class="ml-3">
                                        <Label :text="transaction.name" class="text-white font-inter font-semibold text-sm" />
                                        <Label :text="transaction.date" class="text-[#8A8A8A] font-inter font-normal text-xs mt-1" />
                                    </StackLayout>
                                </FlexboxLayout>
                                <FlexboxLayout flexDirection="row" alignItems="center">
                                    <Label :text="'- ' + formatAmount(transaction.amount) + ' ₽'" class="text-white font-inter font-semibold text-sm mr-4" />
                                    <Image src="~/assets/images/trash.png" width="24" height="24" @tap="showDeleteModal(transaction)" />
                                </FlexboxLayout>
                            </FlexboxLayout>
                        </StackLayout>
                    </StackLayout>

                    <StackLayout v-if="groupedTransactions.earlier.length > 0" class="bg-[#1E1E1E] rounded-3xl px-4 pt-4 pb-3 mb-20">
                        <Label text="Ранее" class="text-white font-inter font-semibold text-xl mb-3" />
                        <StackLayout v-for="transaction in groupedTransactions.earlier" :key="transaction.id" class="bg-[#262626] rounded-2xl p-4 mb-3">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center">
                                <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                    <Image :src="getCategoryIcon(transaction.categoryName)" width="40" height="40" />
                                    <StackLayout class="ml-3">
                                        <Label :text="transaction.name" class="text-white font-inter font-semibold text-sm" />
                                        <Label :text="transaction.date" class="text-[#8A8A8A] font-inter font-normal text-xs mt-1" />
                                    </StackLayout>
                                </FlexboxLayout>
                                <FlexboxLayout flexDirection="row" alignItems="center">
                                    <Label :text="'- ' + formatAmount(transaction.amount) + ' ₽'" class="text-white font-inter font-semibold text-sm mr-4" />
                                    <Image src="~/assets/images/trash.png" width="24" height="24" @tap="showDeleteModal(transaction)" />
                                </FlexboxLayout>
                            </FlexboxLayout>
                        </StackLayout>
                    </StackLayout>

                    <Label v-if="totalTransactionsCount === 0 && !isLoading" text="Нет транзакций" class="text-[#8A8A8A] font-inter font-semibold text-sm text-center mt-8" textWrap="true" />
                </FlexboxLayout>
            </ScrollView>

            <ActivityIndicator v-if="isLoading" row="0" col="0" :busy="true" color="#964BDC" class="my-auto" />

            <GridLayout row="1" col="0" rows="auto, auto" columns="*" class="bg-[#121212]">
                <StackLayout row="0" class="px-4 pb-4">
                    <Button 
                        text="Добавить трату" 
                        class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full" 
                        @tap="showAddExpenseModal" />
                </StackLayout>

                <Menu 
                    row="1" 
                    col="0" 
                    verticalAlignment="bottom" 
                    :activeTab="activeTab" 
                    @update:activeTab="activeTab = $event" />

            </GridLayout>


            <GridLayout v-if="showFilterModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeFilterModal" zIndex="1000" />

            <GridLayout v-if="showFilterModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Фильтры" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <ScrollView height="auto" maxHeight="60%">
                        <StackLayout>
                            <GridLayout v-for="category in availableCategories" :key="category" rows="auto" columns="auto, *" class="mb-3 items-center" @tap="toggleFilter(category)">
                                <GridLayout col="0" width="20" height="20" class="rounded-full border-5 mr-3 items-center justify-center" :class="isFilterActive(category) ? 'border-[#964BDC] bg-[#964BDC]' : 'border-[#8A8A8A]'">
                                    <Label v-if="isFilterActive(category)" text="✓" color="white" fontSize="12" fontWeight="bold" textAlignment="center" verticalAlignment="center" />
                                </GridLayout>
                                <Label col="1" :text="category" class="text-white font-inter font-semibold text-sm" />
                            </GridLayout>
                        </StackLayout>
                    </ScrollView>

                    <Button text="Применить" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-4" @tap="applyFilters" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeAddModal" zIndex="1000" />

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Новая трата" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="nameFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusName">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Название траты" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="nameField" v-model="newExpense.name" hint="Покупка" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="nameFocused = true" @blur="nameFocused = false" autocorrect="false" />
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="amountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusAmount">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Сумма" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="amountField" v-model="displayAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" keyboardType="number" @focus="amountFocused = true" @blur="amountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" columns="16, *" class="bg-[#262626] rounded-2xl mb-4 px-4 min-h-14 items-center pt-1" @tap="showCategorySelector">
                        <Image col="0" src="~/assets/images/list.png" width="14" height="14" class="self-center" />
                        <StackLayout col="1" class="ml-4 py-1">
                            <Label text="Категория" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <Label :text="newExpense.category || 'Выберите категорию'" class="text-white font-inter font-medium text-sm" />
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="dateFocused ? 'border-[#964BDC] border-5' : dateError ? 'border-[#FF0000] border-5' : 'border-[#262626] border-5'" @tap="focusDate">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Дата" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="dateField" v-model="newExpense.date" hint="ДД.ММ.ГГГГ" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="dateFocused = true" @blur="validateDate" />
                        </StackLayout>
                    </GridLayout>
                    <Label v-if="dateError" :text="dateError" class="text-[#FF0000] font-inter text-xs ml-1 mt-1 mb-2" />

                    <ActivityIndicator v-if="isAdding" :busy="true" color="#964BDC" class="my-4" />

                    <Button v-else text="Добавить трату" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', isFormValid ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="isFormValid" @tap="addNewExpense" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeAddModal" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeDeleteModal" zIndex="1000" />

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Вы действительно хотите удалить транзакцию?" class="text-[#8A8A8A] font-inter font-semibold text-base text-left" textWrap="true" />
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end" class="mt-4">
                        <Label text="Отмена" class="text-[#964BDC] font-inter font-semibold text-sm py-2 px-4 mr-4" @tap="closeDeleteModal" />
                        <Label text="Удалить" class="text-[#DE6C35] font-inter font-semibold text-sm py-2 px-4" @tap="confirmDelete" />
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
import CategorySelector from './CategorySelector.vue';
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
            isLoading: false,
            isAdding: false,
            isDeleting: false,
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
            const day = String(today.getDate()).padStart(2, '0');
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const year = today.getFullYear();
            return `${day}.${month}.${year}`;
        },

        getCategoryIcon(categoryName?: string): string {
            const iconMap: Record<string, string> = {
                'Продукты': 'shop',
                'Семья': 'family',
                'Одежда': 'clothes',
                'Кафе': 'cafe',
                'Развлечения': 'entertainment',
                'Здоровье': 'health',
                'Покупки': 'shopping'
            };
            const icon = iconMap[categoryName || ''] || 'star';
            return `~/assets/images/${icon}.png`;
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
            const rounded = Math.round(amount * 100) / 100;
            return rounded.toLocaleString('ru-RU', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }).replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
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

        async confirmDelete() {
            if (!this.selectedTransaction) return;
            this.isDeleting = true;
            try {
                await this.$deleteTransaction(this.selectedTransaction.id);
                this.loadTransactions();
                this.closeDeleteModal();
            } catch (error) {
                console.error('Failed to delete transaction:', error);
            } finally {
                this.isDeleting = false;
            }
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

        focusName() {
            this.nameFocused = true;
            (this.$refs.nameField as any).nativeView.focus();
        },

        focusAmount() {
            this.amountFocused = true;
            (this.$refs.amountField as any).nativeView.focus();
        },

        focusDate() {
            this.dateFocused = true;
            (this.$refs.dateField as any).nativeView.focus();
        },

        showCategorySelector() {
            $navigateTo(CategorySelector, {
                transition: { name: 'slideLeft', duration: 300 },
                props: {
                    allCategories: [],
                    onCategorySelected: (category: any) => {
                        if (category && category.name) {
                            this.newExpense.category = category.name;
                        }
                    }
                }
            });
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
            const frame = Frame.topmost();
            if (frame) {
                frame.goBack();
            }
        },

        preventClose(event: any) {
            event.cancelBubble = true;
        },

        $deleteTransaction(id: number): Promise<void> {
            return Promise.resolve();
        }
    }
});
</script>