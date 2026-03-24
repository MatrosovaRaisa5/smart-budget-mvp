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

                    <!-- Верхняя панель с заголовком и настройками -->
                    <GridLayout rows="auto" columns="*, auto" marginTop="16">
                        <Label col="0" text="Мой бюджет"
                               class="budget-title"
                               horizontalAlignment="left" />

                        <Image col="1" src="~/assets/images/settings.png"
                               width="22" height="22"
                               horizontalAlignment="right"
                               @tap="goToSettings" />
                    </GridLayout>

                    <!-- БЛОК ПРЕДУПРЕЖДЕНИЙ (показываем только если есть) -->
                    <StackLayout v-if="hasWarnings" marginTop="24" backgroundColor="#2F2D44" borderRadius="16"
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
                                        <Label col="0" :text="warning.text + ' ' + warning.category"
                                               class="warning-text"
                                               textWrap="true"
                                               width="220" />

                                        <Image col="1" src="~/assets/images/close.png"
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
                                        <Label col="0" :text="warning.text + ' ' + warning.category"
                                               class="warning-text-vertical"
                                               textWrap="true" />

                                        <Image col="1" src="~/assets/images/close.png"
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

                    <!-- БЛОК ДОХОДОВ (показываем только если есть доходы) -->
                    <StackLayout v-if="hasIncomes" marginTop="16" backgroundColor="#2F2D44" borderRadius="16"
                                paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16">

                        <!-- Заголовок -->
                        <Label :text="'Доходы в ' + currentMonth"
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
                               @tap="addIncome" />
                    </StackLayout>

                    <!-- БЛОК РАСХОДОВ (показываем только если есть расходы) -->
                    <StackLayout v-if="hasExpenses" marginTop="16" backgroundColor="#2F2D44" borderRadius="16"
                                paddingLeft="16" paddingRight="16" paddingTop="16" marginBottom="100">

                        <!-- Заголовок -->
                        <Label text="Расходы в этом месяце"
                               class="expense-title"
                               marginBottom="16" />

                        <!-- Список расходов -->
                        <StackLayout v-for="expense in expenses" :key="expense.id"
                                   class="expense-item"
                                   marginBottom="16">

                            <!-- Верхняя часть с категорией и суммой -->
                            <GridLayout rows="auto" columns="auto, *, auto" marginBottom="12">
                                <Image col="0" :src="'~/assets/images/' + expense.icon + '.png'"
                                       width="48" height="48"
                                       horizontalAlignment="left" />

                                <StackLayout col="1" marginLeft="8" verticalAlignment="center">
                                    <Label :text="expense.category"
                                           class="expense-category" />
                                    <Label :text="expense.transactionCount + ' ' + getTransactionWord(expense.transactionCount)"
                                           class="expense-transactions" />
                                </StackLayout>

                                <StackLayout col="2" verticalAlignment="center">
                                    <Label :text="'Потрачено ' + expense.spentPercent + '%'"
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

                        <!-- Кнопка Добавить трату -->
                        <Button text="Добавить трату"
                               class="expense-button"
                               @tap="addExpense" />
                    </StackLayout>

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
            <GridLayout v-if="showPopup && !hasIncomes" row="0" col="0" rows="*" columns="*"
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closePopup"
                       zIndex="1000" />

            <!-- СЛОЙ 4: МОДАЛЬНОЕ ОКНО (САМЫЙ ВЕРХ) - показываем только если нет данных -->
            <GridLayout v-if="showPopup && !hasIncomes" row="0" col="0" rows="auto" columns="auto"
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
                           @tap="goToSettingsFromPopup" />
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
import { getWarnings, removeWarning, Warning } from './data/warnings';
import {
    getExpenses,
    Expense,
    getStatusBarColor
} from './data/budget';
import { IncomesProvider } from '~/providers/income.provider';
import { Income } from '../models/income.types';

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
            expenses: [] as Expense[],
            isLoading: true,
            incomesProvider: new IncomesProvider(),
            currentMonth: ''
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
        }
    },
    async mounted() {
        this.currentMonth = this.getCurrentMonth();
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
                await this.loadIncomes();
                this.loadWarnings();
                this.loadExpenses();

                if (this.hasIncomes) {
                    this.showPopup = false;
                }
            } catch (error) {
                this.showPopup = true;
            } finally {
                this.isLoading = false;
            }
        },

        async loadIncomes(): Promise<void> {
            this.incomes = await this.incomesProvider.getIncomes();
        },

        loadWarnings(): void {
            this.warnings = getWarnings();
        },

        loadExpenses(): void {
            this.expenses = getExpenses();
        },

        getCurrentMonth(): string {
            const months = [
                'январе', 'феврале', 'марте', 'апреле', 'мае', 'июне',
                'июле', 'августе', 'сентябре', 'октябре', 'ноябре', 'декабре'
            ];
            const currentMonthIndex = new Date().getMonth();
            return months[currentMonthIndex];
        },

        removeWarning(id: number): void {
            const removed = removeWarning(id);
            if (removed) {
                this.loadWarnings();
                console.log(`Warning ${id} removed`);
            }
        },

        toggleWarningsView(): void {
            this.showAllWarnings = !this.showAllWarnings;
            console.log('Show all warnings:', this.showAllWarnings);
        },

        async addIncome(): Promise<void> {
            $navigateTo(Settings, {
                transition: {
                    name: 'slideLeft',
                    duration: 300
                }
            });
        },

        addExpense(): void {
            console.log('Add expense clicked');
        },

        formatAmount(amount: number): string {
            return amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
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
                transition: {
                    name: 'slideLeft',
                    duration: 300
                }
            });
        },

        goToSettingsFromPopup(): void {
            this.showPopup = false;
            $navigateTo(Settings, {
                transition: {
                    name: 'slideLeft',
                    duration: 300
                }
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
.page-my-budget {
    background-color: #13131A;
}

.budget-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 24;
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
</style>
