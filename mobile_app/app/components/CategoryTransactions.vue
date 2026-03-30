<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="page-category-transactions">
        <GridLayout rows="*, auto" columns="*" backgroundColor="#13131A">

            <!-- Индикатор загрузки -->
            <GridLayout v-if="isLoading" row="0" col="0" rows="*" columns="*"
                       backgroundColor="#13131A" zIndex="2000">
                <ActivityIndicator :busy="true" color="#964BDC" width="50" height="50" />
            </GridLayout>

            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (СКРОЛЛИТСЯ) -->
            <ScrollView v-else row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" paddingLeft="16" paddingRight="16">

                    <!-- Верхняя панель с заголовком -->
                    <GridLayout rows="auto" columns="auto, *" marginTop="16" marginBottom="24">
                        <Image col="0" src="~/assets/images/back.png"
                               width="20" height="17"
                               horizontalAlignment="left"
                               @tap="goBack" />

                        <Label col="1" :text="categoryName"
                               class="page-title"
                               marginLeft="24"
                               horizontalAlignment="left" />
                    </GridLayout>

                    <!-- Информация о тратах -->
                    <StackLayout backgroundColor="#2F2D44" borderRadius="16"
                                paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16"
                                marginBottom="16">

                        <Label :text="'Потрачено ' + formatAmount(spentAmount) + ' ₽'"
                               class="spent-amount"
                               marginBottom="4" />

                        <Label :text="'из ' + formatAmount(plannedAmount) + ' ₽ по плану'"
                               class="planned-amount"
                               marginBottom="16" />

                        <!-- Статус бар -->
                        <GridLayout rows="auto" columns="*" height="3" marginBottom="16">
                            <GridLayout col="0" backgroundColor="#969696" height="3" width="100%" />
                            <GridLayout col="0" :backgroundColor="getStatusBarColor(spentPercent)"
                                       :width="spentPercent + '%'" height="3"
                                       horizontalAlignment="left" />
                        </GridLayout>

                        <Label :text="formatAmount(remainingAmount) + ' ₽ до суммы, которую планировали потратить в этом месяце'"
                               class="remaining-text"
                               textWrap="true" />
                    </StackLayout>

                    <!-- Блок Транзакции -->
                    <StackLayout backgroundColor="#2F2D44" borderRadius="16"
                                paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16"
                                marginBottom="16">

                        <Label text="Транзакции"
                               class="transactions-title"
                               marginBottom="16" />

                        <!-- Список транзакций -->
                        <StackLayout v-for="transaction in transactions" :key="transaction.id"
                                   class="transaction-item"
                                   marginBottom="12">

                            <GridLayout rows="auto" columns="auto, *, auto, auto" width="100%">
                                <!-- Иконка категории -->
                                <Image col="0" src="~/assets/images/shop.png"
                                       width="48" height="48"
                                       horizontalAlignment="left" />

                                <!-- Информация о трате -->
                                <StackLayout col="1" marginLeft="12" verticalAlignment="center">
                                    <Label :text="transaction.description"
                                           class="transaction-name" />
                                    <Label :text="formatDate(transaction.date)"
                                           class="transaction-date" />
                                </StackLayout>

                                <!-- Сумма -->
                                <Label col="2" :text="'- ' + formatAmount(transaction.amount) + ' ₽'"
                                       class="transaction-amount"
                                       verticalAlignment="center"
                                       marginRight="16" />

                                <!-- Кнопка удаления -->
                                <Image col="3" src="~/assets/images/trash.png"
                                       width="18" height="18"
                                       verticalAlignment="center"
                                       @tap="showDeleteModal(transaction)" />
                            </GridLayout>
                        </StackLayout>

                        <!-- Сообщение если нет транзакций -->
                        <Label v-if="transactions.length === 0 && !isLoading"
                               text="Нет транзакций"
                               class="empty-text"
                               textAlignment="center"
                               marginTop="16" />
                    </StackLayout>

                </FlexboxLayout>
            </ScrollView>

            <!-- СЛОЙ 2: КНОПКА ДОБАВИТЬ ТРАТУ (ПРИБИТА К НИЗУ) -->
            <GridLayout row="1" col="0"
                       rows="auto"
                       columns="*"
                       backgroundColor="#13131A"
                       paddingLeft="16" paddingRight="16" paddingTop="16" paddingBottom="16">

                <Button text="Добавить трату"
                        class="add-expense-button"
                        @tap="showAddExpenseModal" />
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

                    <!-- Заголовок -->
                    <Label text="Новая трата"
                           class="add-modal-title"
                           marginBottom="16" />

                    <!-- Поле Название траты -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="*"
                                   class="input-field"
                                   :class="{ 'input-focused': nameFocused }"
                                   paddingLeft="16" paddingRight="16"
                                   height="56"
                                   @tap="focusName">

                            <TextField v-model="newExpense.name"
                                      ref="nameField"
                                      :hint="!newExpense.name ? 'Название траты' : ''"
                                      class="input-text"
                                      @focus="nameFocused = true"
                                      @blur="nameFocused = false"
                                      autocorrect="false"
                                      autocapitalizationType="sentences" />
                        </GridLayout>
                    </StackLayout>

                    <!-- Поле Сумма -->
                    <StackLayout class="input-wrapper" marginBottom="16">
                        <GridLayout rows="auto" columns="auto, auto"
                                   class="input-field"
                                   :class="{ 'input-focused': amountFocused }"
                                   paddingLeft="16"
                                   height="56"
                                   @tap="focusAmount">

                            <TextField col="0"
                                      ref="amountField"
                                      v-model="displayAmount"
                                      :hint="!newExpense.amount ? '0' : ''"
                                      class="input-text"
                                      keyboardType="number"
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
                                   height="56"
                                   @tap="focusDate">

                            <TextField v-model="newExpense.date"
                                      ref="dateField"
                                      :hint="!newExpense.date ? 'ДД.ММ.ГГГГ' : ''"
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
                    <Button v-else text="Добавить трату"
                            :class="['add-button', isFormValid ? 'active' : 'inactive']"
                            :isEnabled="isFormValid"
                            @tap="addNewExpense"
                            marginTop="16" />

                    <Button text="Отмена"
                            class="cancel-button-modal"
                            @tap="closeAddModal"
                            marginTop="16" />
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

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { Frame } from '@nativescript/core';
import { getStatusBarColor } from './data/budget';
import { TransactionsProvider } from '~/providers/transactions.provider';
import { Transaction, CreateTransactionRequest } from '../models/transactions.types';

export default defineComponent({
    props: {
        categoryId: {
            type: Number,
            required: true
        },
        categoryName: {
            type: String,
            default: ''
        },
        plannedAmount: {
            type: Number,
            default: 0
        },
        icon: {
            type: String,
            default: '~/assets/images/shop.png'
        }
    },
    data() {
        return {
            transactions: [] as Transaction[],
            isLoading: true,
            isAdding: false,
            isDeleting: false,
            showDeleteModalFlag: false,
            selectedTransaction: null as Transaction | null,
            showAddModalFlag: false,
            nameFocused: false,
            amountFocused: false,
            dateFocused: false,
            dateError: '',
            newExpense: {
                name: '',
                amount: '',
                date: this.getTodayDate(),
            },
            transactionsProvider: new TransactionsProvider()
        };
    },
    computed: {
        spentAmount(): number {
            const sum = this.transactions.reduce((sum, t) => sum + t.amount, 0);
            return Math.round(sum * 100) / 100;
        },
        spentPercent(): number {
            if (this.plannedAmount <= 0) return 0;
            const percent = (this.spentAmount / this.plannedAmount) * 100;
            return Math.round(percent * 10) / 10;
        },
        remainingAmount(): number {
            const rem = this.plannedAmount - this.spentAmount;
            return Math.max(0, Math.round(rem * 100) / 100);
        },
        displayAmount: {
            get(): string {
                return this.newExpense.amount;
            },
            set(value: string) {
                const numericValue = value.replace(/[^\d]/g, '');
                this.newExpense.amount = numericValue;
            }
        },
        isFormValid(): boolean {
            return this.newExpense.name.trim() !== '' &&
                   this.newExpense.amount !== '' &&
                   parseFloat(this.newExpense.amount) > 0 &&
                   !this.dateError &&
                   this.isValidDate(this.newExpense.date);
        }
    },
    async mounted() {
        await this.loadTransactions();
    },
    methods: {
        getTodayDate(): string {
            const today = new Date();
            const day = String(today.getDate()).padStart(2, '0');
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const year = today.getFullYear();
            return `${day}.${month}.${year}`;
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
            if (!this.isValidDate(this.newExpense.date)) {
                this.dateError = 'Введите корректную дату';
            } else {
                this.dateError = '';
            }
        },

        focusName(): void {
            const nameField = this.$refs.nameField as any;
            if (nameField && nameField.nativeView) {
                nameField.nativeView.focus();
            }
        },

        focusAmount(): void {
            const amountField = this.$refs.amountField as any;
            if (amountField && amountField.nativeView) {
                amountField.nativeView.focus();
            }
        },

        focusDate(): void {
            const dateField = this.$refs.dateField as any;
            if (dateField && dateField.nativeView) {
                dateField.nativeView.focus();
            }
        },

        async loadTransactions(): Promise<void> {
            this.isLoading = true;
            try {
                const now = new Date();
                const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
                const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
                const fromDate = this.formatDateForFilter(firstDay);
                const toDate = this.formatDateForFilter(lastDay);

                const filters = {
                    categoryId: this.categoryId,
                    fromDate,
                    toDate
                };
                this.transactions = await this.transactionsProvider.getTransactions(filters);
            } catch (error) {
                console.error('Failed to load transactions:', error);
                this.transactions = [];
            } finally {
                this.isLoading = false;
            }
        },

        formatDateForFilter(date: Date): string {
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            return `${day}.${month}.${year}`;
        },

        formatDate(dateString: string): string {
            if (/^\d{2}\.\d{2}\.\d{4}$/.test(dateString)) {
                return dateString;
            }
            const date = new Date(dateString);
            if (isNaN(date.getTime())) {
                return dateString;
            }
            const day = String(date.getDate()).padStart(2, '0');
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const year = date.getFullYear();
            return `${day}.${month}.${year}`;
        },

        formatAmount(amount: number): string {
            const rounded = Math.round(amount * 100) / 100;
            return rounded.toLocaleString('ru-RU', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }).replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },

        getStatusBarColor(percent: number): string {
            return getStatusBarColor(percent);
        },

        showDeleteModal(transaction: Transaction): void {
            this.selectedTransaction = transaction;
            this.showDeleteModalFlag = true;
        },

        closeDeleteModal(): void {
            this.showDeleteModalFlag = false;
            this.selectedTransaction = null;
        },

        async confirmDelete(): Promise<void> {
            if (!this.selectedTransaction) return;
            this.isDeleting = true;
            try {
                await this.transactionsProvider.deleteTransaction(this.selectedTransaction.id);
                await this.loadTransactions();
                this.closeDeleteModal();
            } catch (error) {
                console.error('Failed to delete transaction:', error);
            } finally {
                this.isDeleting = false;
            }
        },

        showAddExpenseModal(): void {
            this.resetForm();
            this.showAddModalFlag = true;
        },

        closeAddModal(): void {
            this.showAddModalFlag = false;
            this.resetForm();
        },

        resetForm(): void {
            this.newExpense = {
                name: '',
                amount: '',
                date: this.getTodayDate(),
            };
            this.nameFocused = false;
            this.amountFocused = false;
            this.dateFocused = false;
            this.dateError = '';
        },

        async addNewExpense(): Promise<void> {
            if (!this.isFormValid) return;
            this.isAdding = true;
            try {
                const amount = parseFloat(this.newExpense.amount);
                const request: CreateTransactionRequest = {
                    amount,
                    description: this.newExpense.name,
                    categoryId: this.categoryId,
                    date: this.newExpense.date
                };
                await this.transactionsProvider.addTransaction(request);
                await this.loadTransactions();
                this.closeAddModal();
            } catch (error) {
                console.error('Failed to add transaction:', error);
            } finally {
                this.isAdding = false;
            }
        },

        goBack(): void {
            try {
                const frame = Frame.topmost();
                if (frame) {
                    frame.goBack();
                } else {
                    console.error('Frame not found');
                }
            } catch (error) {
                console.error('Navigation back failed:', error);
            }
        },

        preventClose(event: any): void {
            event.cancelBubble = true;
        }
    }
});
</script>

<style scoped>
/* Стили остаются без изменений, как в исходном файле */
.page-category-transactions {
    background-color: #13131A;
}

.page-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.spent-amount {
    color: #E1E1E1;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 20;
}

.planned-amount {
    color: #969696;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 12;
}

.remaining-text {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 14;
    text-wrap: true;
}

.transactions-title {
    color: white;
    font-family: 'Inter';
    font-weight: bold;
    font-size: 18;
}

.transaction-item {
    background-color: #3F3D5B;
    border-radius: 12;
    padding: 12;
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
    height: 48;
    border-radius: 16;
    width: 100%;
}

.add-expense-button:highlighted {
    background-color: #7B3CB0;
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
</style>
