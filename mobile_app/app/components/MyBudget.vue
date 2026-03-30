<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-my-budget" @loaded="onPageLoaded" @resumed="onPageResumed">
        <GridLayout rows="*" columns="*" backgroundColor="#13131A">

            <!-- Индикатор загрузки -->
            <GridLayout v-if="isLoading" row="0" col="0" rows="*" columns="*"
                       backgroundColor="#13131A" zIndex="2000">
                <ActivityIndicator :busy="true" color="#964BDC" width="50" height="50" />
            </GridLayout>

            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (САМЫЙ НИЗ) -->
            <ScrollView v-else row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" paddingLeft="16" paddingRight="16">

                    <!-- Верхняя панель с заголовком и кнопкой настроек -->
                    <GridLayout rows="auto" columns="*, auto" marginTop="32" alignItems="center">
                        <Label col="0" text="Мой бюджет"
                            class="budget-title"
                            horizontalAlignment="left" />

                        <Button col="1" text="Настроить бюджет"
                                class="settings-button"
                                verticalAlignment="center"
                                @tap="goToSettings" />
                    </GridLayout>

                    <!-- БЛОК ПРЕДУПРЕЖДЕНИЙ (показываем только если есть) -->
                    <StackLayout v-if="hasWarnings" marginTop="16" backgroundColor="#2F2D44" borderRadius="16"
                                :paddingRight="showAllWarnings ? 16 : 0" paddingLeft="16" paddingTop="16" paddingBottom="16">

                        <!-- Заголовок с предупреждениями -->
                        <GridLayout rows="auto" columns="auto, auto, *" marginBottom="16" :paddingRight="showAllWarnings ? 0 : 16">
                            <Label col="0" text="Предупреждения"
                                   class="warnings-title" />

                            <Label col="1" :text="warningsCount.toString()"
                                   class="warnings-count"
                                   marginLeft="8" />

                            <Label col="2" :text="showAllWarnings ? 'Скрыть' : 'Все'"
                                   class="warnings-all"
                                   horizontalAlignment="right"
                                   @tap="toggleWarningsView" />
                        </GridLayout>

                        <!-- Горизонтальный скролл с предупреждениями (по умолчанию) -->
                        <ScrollView v-if="!showAllWarnings" orientation="horizontal" height="auto">
                            <FlexboxLayout flexDirection="row">
                                <StackLayout v-for="warning in warnings" :key="warning.id"
                                           class="warning-card"
                                           width="250"
                                           marginRight="8">

                                    <GridLayout rows="auto" columns="*, auto" width="100%">
                                        <Label col="0" :text="warning.text"
                                               class="warning-text"
                                               textWrap="true"
                                               width="220" />

                                        <Image v-if="false" col="1" src="~/assets/images/close.png"
                                               width="8" height="8"
                                               verticalAlignment="top"
                                               marginLeft="8"
                                               marginTop="4"
                                               @tap="removeWarning(warning.id)" />
                                    </GridLayout>
                                </StackLayout>
                            </FlexboxLayout>
                        </ScrollView>

                        <!-- Вертикальный список предупреждений (при нажатии на "Все") -->
                        <ScrollView v-else orientation="vertical" height="auto">
                            <StackLayout>
                                <StackLayout v-for="warning in warnings" :key="warning.id"
                                           class="warning-card-vertical"
                                           marginBottom="8">

                                    <GridLayout rows="auto" columns="*, auto" width="100%">
                                        <Label col="0" :text="warning.text"
                                               class="warning-text-vertical"
                                               textWrap="true" />

                                        <Image v-if="false" col="1" src="~/assets/images/close.png"
                                               width="8" height="8"
                                               verticalAlignment="top"
                                               marginLeft="8"
                                               marginTop="4"
                                               @tap="removeWarning(warning.id)" />
                                    </GridLayout>
                                </StackLayout>
                            </StackLayout>
                        </ScrollView>
                    </StackLayout>

                    <!-- БЛОК ДОХОДОВ (всегда виден) -->
                    <StackLayout marginTop="16" backgroundColor="#2F2D44" borderRadius="16"
                                paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16">

                        <!-- Заголовок -->
                        <Label text="Доходы в этом месяце"
                               class="income-title"
                               marginBottom="16" />

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

                        <!-- Кнопка Добавить доход -->
                        <Button text="Добавить доход"
                               class="income-button"
                               marginTop="0"
                               @tap="showAddModal" />
                    </StackLayout>

                    <!-- БЛОК РАСХОДОВ (всегда виден, но категории только если есть) -->
                    <StackLayout marginTop="16" backgroundColor="#2F2D44" borderRadius="16"
                                paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16"
                                marginBottom="100">

                        <!-- Заголовок -->
                        <Label text="Расходы в этом месяце"
                               class="expense-title"
                               marginBottom="16" />

                        <!-- Список расходов (если есть категории) -->
                        <StackLayout v-if="hasExpenses">
                            <StackLayout v-for="expense in expenses" :key="expense.id"
                                       class="expense-item"
                                       marginBottom="16"
                                       @tap="goToCategoryTransactions(expense)">

                                <!-- Верхняя часть с категорией и суммой -->
                                <GridLayout rows="auto" columns="auto, *, auto" marginBottom="12">
                                    <Image col="0" :src="getCategoryIcon(expense.category)"
                                           width="48" height="48"
                                           horizontalAlignment="left" />

                                    <StackLayout col="1" marginLeft="8" verticalAlignment="center">
                                        <Label :text="expense.category"
                                               class="expense-category" />
                                        <Label :text="expense.transactionCount + ' ' + getTransactionWord(expense.transactionCount)"
                                               class="expense-transactions" />
                                    </StackLayout>

                                    <StackLayout col="2" verticalAlignment="center">
                                        <Label :text="'Потрачено ' + (expense.spentPercent.toFixed ? expense.spentPercent.toFixed(1) : expense.spentPercent) + '%'"
                                               class="expense-percent"
                                               textAlignment="right" />
                                        <Label :text="'Осталось ' + formatAmount(expense.remainingAmount) + ' ₽'"
                                               class="expense-remaining"
                                               textAlignment="right" />
                                    </StackLayout>
                                </GridLayout>

                                <!-- Статус бар на всю ширину -->
                                <GridLayout rows="auto" columns="*" height="3">
                                    <!-- Фон статус бара (вся ширина) -->
                                    <GridLayout col="0" backgroundColor="#969696" height="3" width="100%" />
                                    <!-- Заполнение статус бара (слева) -->
                                    <GridLayout col="0" :backgroundColor="getStatusBarColor(expense.spentPercent)"
                                               :width="expense.spentPercent + '%'" height="3"
                                               horizontalAlignment="left" />
                                </GridLayout>
                            </StackLayout>

                            <!-- Кнопка Добавить трату (только если есть категории) -->
                            <Button text="Добавить трату"
                                   class="expense-button"
                                   @tap="showAddExpenseModal" />
                        </StackLayout>

                        <!-- Сообщение, если категорий нет -->
                        <Label v-else text="Нет распределённых категорий. Настройте бюджет в разделе настроек."
                               class="expense-empty-text"
                               textWrap="true"
                               marginBottom="16" />
                    </StackLayout>

                    <!-- Пустой контент (когда нет данных) -->
                    <FlexboxLayout v-if="!hasIncomes && !hasExpenses" flexDirection="column" justifyContent="center" alignItems="center"
                                  height="100%" minHeight="500">
                        <!-- Ничего не отображаем, только всплывающее окно -->
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

            <!-- СЛОЙ 3: ЗАТЕМНЯЮЩИЙ ФОН (ПОВЕРХ МЕНЮ) - показываем только если нет доходов и нет расходов -->
            <GridLayout v-if="showPopup && !hasIncomes && !hasExpenses" row="0" col="0" rows="*" columns="*"
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closePopup"
                       zIndex="1000" />

            <!-- СЛОЙ 4: МОДАЛЬНОЕ ОКНО (САМЫЙ ВЕРХ) - показываем только если нет доходов и нет расходов -->
            <GridLayout v-if="showPopup && !hasIncomes && !hasExpenses" row="0" col="0" rows="auto" columns="auto"
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

                    <Label text="Ещё не задали бюджет?"
                           class="popup-text"
                           textWrap="true"
                           horizontalAlignment="center"
                           marginTop="12" />
                    <Label text="Нажмите кнопку ниже, чтобы установить лимиты"
                           class="popup-text"
                           textWrap="true"
                           horizontalAlignment="center"
                           marginTop="4" />

                    <Button text="Настроить бюджет"
                           class="budget-button"
                           @tap="goToSettings" />
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

                    <!-- Поле Источник дохода (обязательное) -->
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

                    <!-- Поле Размер дохода с символом ₽ и динамической шириной -->
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
                                   class="currency-symbol-no-margin"
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

                    <!-- Индикатор загрузки при добавлении -->
                    <ActivityIndicator v-if="isAdding" :busy="true" color="#964BDC" width="50" height="50" />

                    <!-- Кнопки -->
                    <Button v-else text="Добавить доход"
                            :class="['add-button', isAddFormValid ? 'active' : 'inactive']"
                            :isEnabled="isAddFormValid"
                            @tap="addNewIncome"
                            marginTop="16" />

                    <Button text="Отмена"
                            class="cancel-button-modal"
                            @tap="closeAddModal"
                            marginTop="16" />
                </StackLayout>
            </GridLayout>

            <!-- СЛОЙ 6: МОДАЛЬНОЕ ОКНО ПОДСКАЗКИ (ДОБАВЛЕНИЕ ТРАТЫ) -->
            <GridLayout v-if="showAddExpenseModalFlag" row="0" col="0" rows="*" columns="*"
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeAddExpenseModal"
                       zIndex="1000" />

            <GridLayout v-if="showAddExpenseModalFlag" row="0" col="0" rows="auto" columns="auto"
                       horizontalAlignment="center" verticalAlignment="center"
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

                    <Label text="Нажмите на любую категорию, чтобы добавить трату для неё"
                           class="popup-text"
                           textWrap="true"
                           horizontalAlignment="center"
                           marginTop="12" />

                    <Button text="Назад"
                           class="budget-button"
                           @tap="closeAddExpenseModal" />
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
        amountWidth(): number {
            const baseWidth = 20;
            const charWidth = 8;
            const length = this.newIncome.amount.length || 1;
            return baseWidth + length * charWidth;
        },
        isAddFormValid(): boolean {
            return this.newIncome.source.trim() !== '' &&
                   this.newIncome.amount.trim() !== '' &&
                   parseFloat(this.newIncome.amount) > 0 &&
                   !this.dateError &&
                   this.isValidDate(this.newIncome.date);
        },
        displayExpenseAmount: {
            get(): string {
                return this.newExpense.amount;
            },
            set(value: string) {
                const numericValue = value.replace(/[^\d]/g, '');
                this.newExpense.amount = numericValue;
            }
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
            const icon = iconMap[categoryName] || 'shop';
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

        validateExpenseDate(): void {
            this.expenseDateFocused = false;
            if (!this.isValidDate(this.newExpense.date)) {
                this.expenseDateError = 'Введите корректную дату';
            } else {
                this.expenseDateError = '';
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

        resetExpenseForm(): void {
            this.newExpense = {
                name: '',
                amount: '',
                date: this.getTodayDate(),
            };
            this.selectedExpenseCategory = null;
            this.expenseNameFocused = false;
            this.expenseAmountFocused = false;
            this.expenseDateFocused = false;
            this.expenseDateError = '';
        },

        focusExpenseName(): void {
            const nameField = this.$refs.expenseNameField as any;
            if (nameField && nameField.nativeView) {
                nameField.nativeView.focus();
            }
        },

        focusExpenseAmount(): void {
            const amountField = this.$refs.expenseAmountField as any;
            if (amountField && amountField.nativeView) {
                amountField.nativeView.focus();
            }
        },

        focusExpenseDate(): void {
            const dateField = this.$refs.expenseDateField as any;
            if (dateField && dateField.nativeView) {
                dateField.nativeView.focus();
            }
        },

        selectExpenseCategory(category: Category): void {
            if (this.selectedExpenseCategory?.id === category.id) {
                this.selectedExpenseCategory = null;
            } else {
                this.selectedExpenseCategory = category;
            }
        },

        async addNewExpenseToBudget(): Promise<void> {
            if (this.isExpenseFormValid && this.selectedExpenseCategory) {
                this.isAddingExpense = true;
                try {
                    const amount = parseFloat(this.newExpense.amount);
                    await this.transactionsProvider.addTransaction({
                        amount: amount,
                        description: this.newExpense.name,
                        categoryId: this.selectedExpenseCategory.id,
                        date: this.newExpense.date
                    });
                    await this.loadData();
                    this.closeAddExpenseModal();
                } catch (error) {
                    console.error('Failed to add expense:', error);
                } finally {
                    this.isAddingExpense = false;
                }
            }
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

<style scoped>
/* Стили остаются без изменений, как в предоставленном файле */
.page-my-budget {
    background-color: #13131A;
}

.budget-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 24;
}

.settings-button {
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

.settings-button:highlighted {
    background-color: #8BCA00;
}

.warnings-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.warnings-count {
    color: #969696;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.warnings-all {
    color: #964BDC;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
}

.warning-card {
    background-color: #FF5E00;
    border-radius: 15;
    padding: 16;
    width: 250;
}

.warning-card-vertical {
    background-color: #FF5E00;
    border-radius: 15;
    padding: 16;
    width: 100%;
}

.warning-text {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
    text-wrap: true;
    width: 220;
}

.warning-text-vertical {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
    text-wrap: true;
    width: 100%;
}

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

.income-button {
    background-color: #964BDC;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 48;
    border-radius: 16;
    width: 100%;
}

.income-button:highlighted {
    background-color: #7B3CB0;
}

.expense-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.expense-item {
    background-color: #3F3D5B;
    border-radius: 15;
    padding: 16;
    width: 100%;
}

.expense-category {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
}

.expense-transactions {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-top: 2;
}

.expense-percent {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
}

.expense-remaining {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 12;
    margin-top: 2;
}

.expense-button {
    background-color: #964BDC;
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    height: 48;
    border-radius: 16;
    width: 100%;
    margin-bottom: 16;
}

.expense-button:highlighted {
    background-color: #7B3CB0;
}

.expense-empty-text {
    color: #969696;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
    text-align: center;
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

.category-label {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 16;
}

.category-text {
    color: white;
    font-family: 'Inter';
    font-weight: 600;
    font-size: 14;
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
</style>
