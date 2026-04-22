<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">
            
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">
                    
                    <Label text="Мой бюджет" class="text-white font-inter font-semibold text-2xl mb-6" />

                    <StackLayout v-if="hasWarnings" class="bg-[#1E1E1E] rounded-3xl px-4 pt-4 pb-4 mb-4">
                        <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center" class="mb-3">
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <Label text="Предупреждения" class="text-white font-inter font-semibold text-base" />
                                <Label :text="warningsCount.toString()" class="text-[#8A8A8A] font-inter font-semibold text-base ml-2" />
                            </FlexboxLayout>
                            <Label :text="showAllWarnings ? 'Скрыть' : 'Все'" class="text-[#964BDC] font-inter font-semibold text-xs" @tap="toggleWarningsView" />
                        </FlexboxLayout>

                        <ScrollView v-if="!showAllWarnings" orientation="horizontal" height="auto">
                            <FlexboxLayout flexDirection="row">
                                <StackLayout v-for="warning in warnings" :key="warning.id" class="bg-[#FF5E00] rounded-2xl p-4 mr-2" width="250">
                                    <Label :text="warning.text" class="text-white font-inter font-semibold text-sm" textWrap="true" />
                                </StackLayout>
                            </FlexboxLayout>
                        </ScrollView>

                        <StackLayout v-else>
                            <StackLayout v-for="warning in warnings" :key="warning.id" class="bg-[#FF5E00] rounded-2xl p-4 mb-2">
                                <Label :text="warning.text" class="text-white font-inter font-semibold text-sm" textWrap="true" />
                            </StackLayout>
                        </StackLayout>
                    </StackLayout>

                    <StackLayout class="bg-[#1E1E1E] rounded-3xl px-4 pt-5 pb-4 mb-4">
                        <Label text="Доходы в этом месяце" class="text-white font-inter font-semibold text-xl mb-4" />
                        
                        <FlexboxLayout flexDirection="row" alignItems="center" class="mb-4">
                            <Image src="~/assets/images/money.png" width="48" height="48" />
                            <StackLayout class="ml-4">
                                <Label :text="formatAmount(totalIncome) + ' ₽'" class="text-white font-inter font-semibold text-xl" />
                                <Label :text="totalIncomeSources + ' ' + getSourceWord(totalIncomeSources)" class="text-[#8A8A8A] font-inter font-normal text-xs" />
                            </StackLayout>
                        </FlexboxLayout>

                        <Button text="Добавить доход" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl" @tap="showAddModal" />
                    </StackLayout>

                    <StackLayout class="bg-[#1E1E1E] rounded-3xl px-4 pt-5 pb-1 mb-20">
                        <Label text="Расходы в этом месяце" class="text-white font-inter font-semibold text-xl mb-4" />

                        <StackLayout v-if="hasExpenses">
                            <StackLayout v-for="expense in expenses" :key="expense.id" class="bg-[#262626] rounded-2xl p-4 mb-3" @tap="goToCategoryTransactions(expense)">
                                <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="flex-start" class="mb-3">
                                    <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                        <Image :src="getCategoryIcon(expense.category)" width="40" height="40" />
                                        <StackLayout class="ml-3" width="60%">
                                            <Label :text="expense.category" class="text-white font-inter font-semibold text-sm" textWrap="true" />
                                            <Label :text="expense.transactionCount + ' ' + getTransactionWord(expense.transactionCount)" class="text-[#8A8A8A] font-inter font-normal text-xs mt-1" />
                                        </StackLayout>
                                    </FlexboxLayout>
                                    <StackLayout class="flex-shrink-0 mt-1">
                                        <Label :text="'Потрачено ' + (expense.spentPercent.toFixed ? expense.spentPercent.toFixed(1) : expense.spentPercent) + '%'" class="text-white font-inter font-semibold text-xs text-right" />
                                        <Label :text="'Осталось ' + formatAmount(expense.remainingAmount) + ' ₽'" class="text-[#8A8A8A] font-inter font-normal text-xs text-right mt-1" />
                                    </StackLayout>
                                </FlexboxLayout>
                                <GridLayout rows="auto" columns="*" height="4" class="rounded-full overflow-hidden" horizontalAlignment="left">
                                    <GridLayout col="0" backgroundColor="#454545" height="4" width="100%" borderRadius="2" />
                                    <GridLayout col="0" :backgroundColor="getStatusBarColor(expense.spentPercent)" :width="expense.spentPercent + '%'" height="4" borderRadius="2" horizontalAlignment="left" />
                                </GridLayout>
                            </StackLayout>
                        </StackLayout>

                        <Label v-else text="Нет распределённых категорий. Настройте бюджет в разделе настроек." class="text-[#8A8A8A] font-inter font-semibold text-sm text-center" textWrap="true" />
                    </StackLayout>
                </FlexboxLayout>
            </ScrollView>

            <Menu row="0" col="0" verticalAlignment="bottom" :activeTab="activeTab" @update:activeTab="activeTab = $event" zIndex="1" />

            <GridLayout v-if="showPopup && !hasIncomes && !hasExpenses" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closePopup" zIndex="1000" />

            <GridLayout v-if="showPopup && !hasIncomes && !hasExpenses" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" @tap="closePopup" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl px-5 py-5 w-64" @tap="preventClose">
                    <Image src="~/assets/images/smile.png" width="60" height="60" class="self-center" />
                    <Label text="Ещё не задали бюджет?" class="text-[#8A8A8A] font-inter font-semibold text-base text-center mt-3" textWrap="true" />
                    <Label text="Нажмите кнопку ниже, чтобы установить лимиты" class="text-[#8A8A8A] font-inter font-semibold text-xs text-center mt-1" textWrap="true" />
                    <Button text="Настроить бюджет" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl mt-4" @tap="goToSettings" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeAddModal" zIndex="1000" />

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Новый доход" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="sourceFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusSource">
                        <StackLayout class="ml-1 py-1">
                            <Label text="Источник дохода" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="sourceField" v-model="newIncome.source" hint="Зарплата" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" color="white" @focus="sourceFocused = true" @blur="sourceFocused = false" autocorrect="false" />
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="amountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusAmount">
                        <StackLayout class="ml-1 py-1">
                            <Label text="Сумма дохода" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="amountField" v-model="displayAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" keyboardType="number" @focus="amountFocused = true" @blur="amountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="dateFocused ? 'border-[#964BDC] border-5' : dateError ? 'border-[#FF0000] border-5' : 'border-[#262626] border-5'" @tap="focusDate">
                        <StackLayout class="ml-1 py-1">
                            <Label text="Дата" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="dateField" v-model="newIncome.date" hint="ДД.ММ.ГГГГ" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="dateFocused = true" @blur="validateDate" />
                        </StackLayout>
                    </GridLayout>
                    <Label v-if="dateError" :text="dateError" class="text-[#FF0000] font-inter text-xs ml-1 mt-1 mb-2" />

                    <ActivityIndicator v-if="isAdding" :busy="true" color="#964BDC" class="my-4" />

                    <Button v-else text="Добавить доход" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', isAddFormValid ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="isAddFormValid" @tap="addNewIncome" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeAddModal" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showAddExpenseModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeAddExpenseModal" zIndex="1000" />

            <GridLayout v-if="showAddExpenseModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-64" @tap="preventClose">
                    <Image src="~/assets/images/smile.png" width="60" height="60" class="self-center" />
                    <Label text="Нажмите на любую категорию, чтобы добавить трату для неё" class="text-[#8A8A8A] font-inter font-semibold text-base text-center mt-3" textWrap="true" />
                    <Button text="Назад" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl mt-4" @tap="closeAddExpenseModal" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { $navigateTo } from 'nativescript-vue';
import Settings from './Settings.vue';
import Menu from './Menu.vue';
import CategoryTransactions from './CategoryTransactions.vue';
import { getStatusBarColor } from './data/budget';
import { IncomesProvider } from '~/providers/income.provider';
import { BudgetProvider } from '~/providers/budget.provider';
import { TransactionsProvider } from '~/providers/transactions.provider';
import { CategoriesProvider } from '~/providers/categories.provider';
import { Income } from '../models/income.types';
import { BudgetCategoryStatus } from '../models/budget.types';
import { Category } from '../models/categories.types';
import { Transaction } from '../models/transactions.types';

interface Warning {
    id: number;
    text: string;
}

interface ExpenseItem {
    id: number;
    category: string;
    spentAmount: number;
    remainingAmount: number;
    spentPercent: number;
    transactionCount: number;
}

export default defineComponent({
    components: {
        Menu
    },
    data() {
        return {
            showPopup: true,
            activeTab: 'budget',
            warnings: [] as Warning[],
            showAllWarnings: false,
            incomes: [] as Income[],
            expenses: [] as ExpenseItem[],
            isLoading: true,
            incomesProvider: new IncomesProvider(),
            budgetProvider: new BudgetProvider(),
            transactionsProvider: new TransactionsProvider(),
            categoriesProvider: new CategoriesProvider(),
            showAddModalFlag: false,
            isAdding: false,
            sourceFocused: false,
            amountFocused: false,
            dateFocused: false,
            dateError: '',
            newIncome: {
                source: '',
                amount: '',
                date: this.getTodayDate()
            },
            showAddExpenseModalFlag: false,
            isAddingExpense: false,
            expenseNameFocused: false,
            expenseAmountFocused: false,
            expenseDateFocused: false,
            expenseDateError: '',
            expenseCategories: [] as Category[],
            newExpense: {
                name: '',
                amount: '',
                date: this.getTodayDate(),
            },
            selectedExpenseCategory: null as Category | null
        };
    },
    computed: {
        hasWarnings(): boolean {
            return this.warnings.length > 0;
        },
        warningsCount(): number {
            return this.warnings.length;
        },
        hasIncomes(): boolean {
            return this.incomes && this.incomes.length > 0;
        },
        hasExpenses(): boolean {
            return this.expenses && this.expenses.length > 0;
        },
        totalIncome(): number {
            return this.incomesProvider.getTotalAmount(this.incomes);
        },
        totalIncomeSources(): number {
            return this.incomes.length;
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
        isAddFormValid(): boolean {
            return this.newIncome.source.trim() !== '' &&
                   this.newIncome.amount.trim() !== '' &&
                   parseFloat(this.newIncome.amount) > 0 &&
                   !this.dateError &&
                   this.isValidDate(this.newIncome.date);
        },
        isExpenseFormValid(): boolean {
            return this.newExpense.name.trim() !== '' &&
                   this.newExpense.amount !== '' &&
                   parseFloat(this.newExpense.amount) > 0 &&
                   !this.expenseDateError &&
                   this.isValidDate(this.newExpense.date) &&
                   this.selectedExpenseCategory !== null;
        }
    },
    async mounted() {
        await this.loadData();
    },
    methods: {
        onPageLoaded() {
            this.loadData();
        },

        onPageResumed() {
            this.loadData();
        },

        focusSource() {
            this.sourceFocused = true;
            (this.$refs.sourceField as any).nativeView.focus();
        },

        focusAmount() {
            this.amountFocused = true;
            (this.$refs.amountField as any).nativeView.focus();
        },

        focusDate() {
            this.dateFocused = true;
            (this.$refs.dateField as any).nativeView.focus();
        },

        async loadData(): Promise<void> {
            this.isLoading = true;
            try {
                await Promise.all([
                    this.loadIncomes(),
                    this.loadBudgetData(),
                    this.loadCategories(),
                    this.loadAlerts()
                ]);
                if (this.hasIncomes) {
                    this.showPopup = false;
                }
            } catch (error) {
                console.error('Error loading data:', error);
                this.showPopup = true;
            } finally {
                this.isLoading = false;
            }
        },

        async loadIncomes(): Promise<void> {
            this.incomes = await this.incomesProvider.getIncomes();
        },

        async loadBudgetData(): Promise<void> {
            try {
                const status = await this.budgetProvider.getBudgetStatus();
                const now = new Date();
                const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
                const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
                const fromDate = this.formatDateForFilter(firstDay);
                const toDate = this.formatDateForFilter(lastDay);
                const allTransactions = await this.transactionsProvider.getTransactions({ fromDate, toDate });

                const transactionsByCategory = new Map<number, { amount: number; count: number }>();
                allTransactions.forEach(tx => {
                    const catId = tx.category.id;
                    const existing = transactionsByCategory.get(catId);
                    if (existing) {
                        existing.amount += tx.amount;
                        existing.count += 1;
                    } else {
                        transactionsByCategory.set(catId, { amount: tx.amount, count: 1 });
                    }
                });

                this.expenses = status.categories.map((cat: BudgetCategoryStatus) => {
                    const spentData = transactionsByCategory.get(cat.categoryId) || { amount: 0, count: 0 };
                    const spentAmount = spentData.amount;
                    const plannedAmount = cat.spent + cat.remaining;
                    const remainingAmount = Math.max(0, plannedAmount - spentAmount);
                    const spentPercent = plannedAmount > 0 ? (spentAmount / plannedAmount) * 100 : 0;
                    const roundedPercent = Math.round(spentPercent * 10) / 10;
                    return {
                        id: cat.categoryId,
                        category: cat.categoryName,
                        spentAmount: spentAmount,
                        remainingAmount: remainingAmount,
                        spentPercent: roundedPercent,
                        transactionCount: spentData.count
                    };
                });
            } catch (error) {
                console.error('Failed to load budget status:', error);
                this.expenses = [];
            }
        },

        async loadCategories(): Promise<void> {
            try {
                this.expenseCategories = await this.categoriesProvider.getCategories();
            } catch (error) {
                console.error('Failed to load categories:', error);
                this.expenseCategories = [];
            }
        },

        async loadAlerts(): Promise<void> {
            try {
                const alerts = await this.budgetProvider.getAlerts();
                this.warnings = alerts.map((text, index) => ({
                    id: index,
                    text: text
                }));
            } catch (error) {
                console.error('Failed to load alerts:', error);
                this.warnings = [];
            }
        },

        getCategoryIcon(categoryName: string): string {
            const iconMap: Record<string, string> = {
                'Продукты': 'shop',
                'Семья': 'family',
                'Одежда': 'clothes'
            };
            const icon = iconMap[categoryName] || 'star';
            return `~/assets/images/${icon}.png`;
        },

        formatDateForFilter(date: Date): string {
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            return `${day}.${month}.${year}`;
        },

        getTodayDate(): string {
            return this.formatDateForFilter(new Date());
        },

        isValidDate(dateString: string): boolean {
            const dateRegex = /^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$/;
            if (!dateRegex.test(dateString)) return false;
            const [day, month, year] = dateString.split('.').map(Number);
            const date = new Date(year, month - 1, day);
            return date.getDate() === day && date.getMonth() === month - 1 && date.getFullYear() === year;
        },

        validateDate(): void {
            this.dateFocused = false;
            if (!this.isValidDate(this.newIncome.date)) {
                this.dateError = 'Введите корректную дату';
            } else {
                this.dateError = '';
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

        async addNewIncome(): Promise<void> {
            if (this.isAddFormValid) {
                this.isAdding = true;
                try {
                    const amount = parseFloat(this.newIncome.amount);
                    await this.incomesProvider.createIncome({
                        source: this.newIncome.source,
                        amount: amount,
                        date: this.newIncome.date
                    });

                    await this.loadIncomes();
                    await this.updateBudgetAfterIncomeChange();
                    await Promise.all([
                        this.loadBudgetData(),
                        this.loadCategories(),
                        this.loadAlerts()
                    ]);

                    this.closeAddModal();
                } finally {
                    this.isAdding = false;
                }
            }
        },

        async updateBudgetAfterIncomeChange(): Promise<void> {
            try {
                const status = await this.budgetProvider.getBudgetStatus();
                const percentages: Record<string, number> = {};

                status.categories.forEach(cat => {
                    if (cat.percent > 0) {
                        percentages[cat.categoryId.toString()] = cat.percent;
                    }
                });

                if (Object.keys(percentages).length > 0) {
                    await this.budgetProvider.setupBudget({
                        plannedIncome: this.totalIncome,
                        percentages: percentages
                    });
                }
            } catch (error) {
                console.error('Failed to update budget after income change:', error);
            }
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

        showAddExpenseModal(): void {
            this.showAddExpenseModalFlag = true;
        },

        closeAddExpenseModal(): void {
            this.showAddExpenseModalFlag = false;
        },

        removeWarning(id: number): void {
            this.warnings = this.warnings.filter(w => w.id !== id);
        },

        toggleWarningsView(): void {
            this.showAllWarnings = !this.showAllWarnings;
        },

        goToCategoryTransactions(expense: ExpenseItem): void {
            $navigateTo(CategoryTransactions, {
                transition: { name: 'slideLeft', duration: 300 },
                props: {
                    categoryName: expense.category,
                    categoryId: expense.id,
                    spentAmount: expense.spentAmount,
                    spentPercent: expense.spentPercent,
                    remainingAmount: expense.remainingAmount,
                    plannedAmount: expense.spentAmount + expense.remainingAmount,
                    icon: this.getCategoryIcon(expense.category).replace('~/assets/images/', '').replace('.png', '')
                }
            });
        },

        formatAmount(amount: number): string {
            const rounded = Math.round(amount * 100) / 100;
            return rounded.toLocaleString('ru-RU', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }).replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },

        getSourceWord(count: number): string {
            if (count === 1) return 'источник';
            if (count >= 2 && count <= 4) return 'источника';
            return 'источников';
        },

        getTransactionWord(count: number): string {
            if (count === 1) return 'транзакция';
            if (count >= 2 && count <= 4) return 'транзакции';
            return 'транзакций';
        },

        getStatusBarColor(percent: number): string {
            return getStatusBarColor(percent);
        },

        goToSettings(): void {
            $navigateTo(Settings, {
                transition: { name: 'slideLeft', duration: 300 }
            });
        },

        closePopup(): void {
            this.showPopup = false;
        },

        preventClose(event: any): void {
            event.cancelBubble = true;
        },

        async refreshData(): Promise<void> {
            await this.loadData();
        }
    }
});
</script>