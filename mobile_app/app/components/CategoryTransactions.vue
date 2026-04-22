<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView v-if="!isLoading" row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">

                    <GridLayout rows="auto" columns="auto, *" class="mb-6" alignItems="center">
                        <Image col="0" src="~/assets/images/back.png" width="25" height="25" @tap="goBack" />
                        <Label col="1" :text="categoryName" class="text-white font-inter font-semibold text-2xl ml-4" textWrap="true" lineHeight="0" />
                    </GridLayout>

                    <StackLayout class="bg-[#1E1E1E] rounded-3xl px-4 pt-5 pb-4 mb-4">
                        <Label :text="'Потрачено ' + formatAmount(spentAmount) + ' ₽'" class="text-white font-inter font-semibold text-xl mb-1" />
                        <Label :text="'из ' + formatAmount(plannedAmount) + ' ₽ по плану'" class="text-[#8A8A8A] font-inter font-normal text-xs mb-4" />

                        <GridLayout rows="auto" columns="*" height="4" class="rounded-full overflow-hidden mb-4">
                            <GridLayout col="0" backgroundColor="#454545" height="4" width="100%" borderRadius="2" />
                            <GridLayout col="0" :backgroundColor="getStatusBarColor(spentPercent)" :width="spentPercent + '%'" height="4" borderRadius="2" horizontalAlignment="left" />
                        </GridLayout>

                        <Label :text="formatAmount(remainingAmount) + ' ₽ до суммы, которую планировали потратить в этом месяце'" class="text-[#8A8A8A] font-inter font-normal text-sm" textWrap="true" />
                    </StackLayout>

                    <StackLayout class="bg-[#1E1E1E] rounded-3xl px-4 pt-5 pb-3 mb-20">
                        <Label text="Транзакции" class="text-white font-inter font-semibold text-xl mb-4" />

                        <StackLayout v-for="transaction in transactions" :key="transaction.id" class="bg-[#262626] rounded-2xl p-4 mb-3">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center">
                                <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                    <Image src="~/assets/images/shop.png" width="40" height="40" />
                                    <StackLayout class="ml-3">
                                        <Label :text="transaction.description" class="text-white font-inter font-semibold text-sm" />
                                        <Label :text="formatDate(transaction.date)" class="text-[#8A8A8A] font-inter font-normal text-xs mt-1" />
                                    </StackLayout>
                                </FlexboxLayout>
                                <FlexboxLayout flexDirection="row" alignItems="center">
                                    <Label :text="'- ' + formatAmount(transaction.amount) + ' ₽'" class="text-white font-inter font-semibold text-sm mr-4" />
                                    <Image src="~/assets/images/trash.png" width="24" height="24" @tap="showDeleteModal(transaction)" />
                                </FlexboxLayout>
                            </FlexboxLayout>
                        </StackLayout>

                        <Label v-if="transactions.length === 0 && !isLoading" text="Нет транзакций" class="text-[#8A8A8A] font-inter font-semibold text-sm text-center" textWrap="true" />
                    </StackLayout>
                </FlexboxLayout>
            </ScrollView>

            <ActivityIndicator v-if="isLoading" row="0" col="0" :busy="true" color="#964BDC" class="my-auto" />

            <GridLayout row="1" col="0" rows="auto" columns="*" class="px-4 pb-4">
                <Button text="Добавить трату" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl" @tap="showAddExpenseModal" />
            </GridLayout>

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeAddModal" zIndex="1000" />

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Новая трата" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="nameFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusName">
                        <StackLayout class="ml-1 py-1">
                            <Label text="Название траты" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="nameField" v-model="newExpense.name" hint="Покупка" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="nameFocused = true" @blur="nameFocused = false" autocorrect="false" />
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="amountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusAmount">
                        <StackLayout class="ml-1 py-1">
                            <Label text="Сумма" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="amountField" v-model="displayAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" keyboardType="number" @focus="amountFocused = true" @blur="amountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="dateFocused ? 'border-[#964BDC] border-5' : dateError ? 'border-[#FF0000] border-5' : 'border-[#262626] border-5'" @tap="focusDate">
                        <StackLayout class="ml-1 py-1">
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
            default: 'shop'
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
            this.nameFocused = true;
            (this.$refs.nameField as any).nativeView.focus();
        },

        focusAmount(): void {
            this.amountFocused = true;
            (this.$refs.amountField as any).nativeView.focus();
        },

        focusDate(): void {
            this.dateFocused = true;
            (this.$refs.dateField as any).nativeView.focus();
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
            const frame = Frame.topmost();
            if (frame) {
                frame.goBack();
            }
        },

        preventClose(event: any): void {
            event.cancelBubble = true;
        }
    }
});
</script>