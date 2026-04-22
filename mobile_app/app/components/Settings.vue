<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView v-if="!isLoading" row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">

                    <Label text="Настройки бюджета" class="text-white font-inter font-semibold text-2xl mb-6" />

                    <StackLayout class="bg-[#1E1E1E] rounded-3xl px-4 pt-5 pb-1 mb-4">
                        <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center" class="mb-4">
                            <Label text="Доходы в этом месяце" class="text-white font-inter font-semibold text-xl" />
                            <Image src="~/assets/images/plus.png" width="20" height="20" @tap="showAddModal" />
                        </FlexboxLayout>

                        <FlexboxLayout flexDirection="row" alignItems="center" class="mb-4">
                            <Image src="~/assets/images/money.png" width="48" height="48" />
                            <StackLayout class="ml-4">
                                <Label :text="formatAmount(totalIncome) + ' ₽'" class="text-white font-inter font-semibold text-xl" />
                                <Label :text="totalIncomeSources + ' ' + getSourceWord(totalIncomeSources)" class="text-[#8A8A8A] font-inter font-normal text-xs" />
                            </StackLayout>
                        </FlexboxLayout>

                        <StackLayout v-for="income in incomes" :key="income.id" class="bg-[#262626] rounded-2xl p-4 mb-3">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center">
                                <StackLayout class="flex-1">
                                    <Label :text="income.source || 'Без источника'" class="text-white font-inter font-semibold text-sm" />
                                    <Label :text="formatDate(income.date)" class="text-[#8A8A8A] font-inter font-normal text-xs mt-1" />
                                </StackLayout>
                                <FlexboxLayout flexDirection="row" alignItems="center">
                                    <Label :text="formatAmount(income.amount) + ' ₽'" class="text-white font-inter font-semibold text-sm mr-4" />
                                    <Image src="~/assets/images/trash.png" width="24" height="24" @tap="showDeleteModal(income)" />
                                </FlexboxLayout>
                            </FlexboxLayout>
                        </StackLayout>
                    </StackLayout>

                    <StackLayout class="bg-[#1E1E1E] rounded-3xl px-4 pt-5 pb-4 mb-20">
                        <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center" class="mb-4">
                            <Label text="Распределение бюджета" class="text-white font-inter font-semibold text-xl" />
                            <Image src="~/assets/images/plus.png" width="20" height="20" @tap="addNewCategoryFromPlus" />
                        </FlexboxLayout>

                        <StackLayout v-if="showNewCategory" class="bg-[#262626] rounded-2xl p-4 mb-3 border-[#964BDC] border-5 border-dashed">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center" class="mb-3">
                                <Label text="Новая категория" class="text-white font-inter font-semibold text-sm" />
                                <Image src="~/assets/images/trash.png" width="24" height="24" @tap="cancelNewCategory" />
                            </FlexboxLayout>

                            <GridLayout rows="auto" columns="16, *" class="bg-[#454545] rounded-2xl px-4 min-h-14 items-center pt-1 mb-3" @tap="showCategorySelector">
                                <Image col="0" src="~/assets/images/list.png" width="14" height="14" class="self-center" />
                                <StackLayout col="1" class="ml-4 py-1">
                                    <Label text="Категория" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                                    <Label :text="selectedCategoryName || 'Новая категория'" class="text-white font-inter font-medium text-sm" />
                                </StackLayout>
                            </GridLayout>

                            <Label text="Задайте процент от общего дохода для этой категории расходов" class="text-[#8A8A8A] font-inter font-normal text-xs mb-3" textWrap="true" />

                            <GridLayout rows="auto" columns="*" class="bg-[#454545] rounded-2xl px-4 min-h-14 items-center pt-1 mb-3" :class="newCategoryFocused ? 'border-[#964BDC] border-5' : ''">
                                <StackLayout class="ml-1 py-1 w-full">
                                    <Label text="От 0 до 100 %" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                                    <TextField v-model="newCategoryPercent" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" keyboardType="number" @focus="newCategoryFocused = true" @blur="newCategoryFocused = false" @textChange="onNewPercentChange" />
                                </StackLayout>
                            </GridLayout>

                            <Label :text="'Будет доступно ' + formatAmount(calculateCategoryAmount(newCategoryPercent)) + ' ₽'" class="text-[#8A8A8A] font-inter font-normal text-xs text-right" />
                        </StackLayout>

                        <StackLayout v-for="category in distributionCategories" :key="category.id" class="bg-[#262626] rounded-2xl p-4 mb-3">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center" class="mb-3">
                                <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                    <Image :src="getCategoryIcon(category.name)" width="40" height="40" />
                                    <Label :text="category.name" class="text-white font-inter font-semibold text-sm ml-3" />
                                </FlexboxLayout>
                                <Image src="~/assets/images/trash.png" width="24" height="24" @tap="showDeleteCategoryModal(category)" />
                            </FlexboxLayout>

                            <Label text="Задайте процент от общего дохода для этой категории расходов" class="text-[#8A8A8A] font-inter font-normal text-xs mb-3" textWrap="true" />

                            <GridLayout rows="auto" columns="*" class="bg-[#454545] rounded-2xl px-4 min-h-14 items-center pt-1 mb-3" :class="category.focused ? 'border-[#964BDC] border-5' : ''">
                                <StackLayout class="ml-1 py-1 w-full">
                                    <Label text="От 0 до 100 %" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                                    <TextField v-model="category.percent" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" keyboardType="number" @focus="category.focused = true" @blur="category.focused = false" @textChange="onPercentChange(category)" />
                                </StackLayout>
                            </GridLayout>

                            <Label :text="'Будет доступно ' + formatAmount(calculateCategoryAmount(category.percent)) + ' ₽'" class="text-[#8A8A8A] font-inter font-normal text-xs text-right" />
                        </StackLayout>
                    </StackLayout>
                </FlexboxLayout>
            </ScrollView>

            <ActivityIndicator v-if="isLoading" row="0" col="0" :busy="true" color="#964BDC" class="my-auto" />

            <GridLayout row="1" col="0" rows="auto" columns="*" class="bg-[#121212]">
                <StackLayout>
                    <StackLayout class="px-4 pt-4 pb-2" v-if="distributionCategories.length > 0 || showNewCategory">
                        <Label :text="distributionStatusText" :class="['text-right font-inter font-semibold text-sm mb-2', isDistributionValid ? 'text-[#A2E809]' : 'text-[#FF0000]']" />
                        <GridLayout rows="auto" columns="*" height="4" class="rounded-full overflow-hidden">
                            <GridLayout col="0" backgroundColor="#454545" height="4" width="100%" borderRadius="2" />
                            <GridLayout col="0" :backgroundColor="distributionBarColor" :width="distributionPercentage + '%'" height="4" borderRadius="2" horizontalAlignment="left" />
                        </GridLayout>
                    </StackLayout>

                    <StackLayout class="px-4 pb-4" v-if="distributionCategories.length > 0 || showNewCategory">
                        <Button text="Сохранить" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mb-0', isSaveEnabled && !isSaving ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="isSaveEnabled && !isSaving" @tap="saveDistribution" />
                    </StackLayout>

                    <Menu :activeTab="activeTab" @update:activeTab="activeTab = $event" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeDeleteModal" zIndex="1000" />

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Удалить доход без возможности восстановления?" class="text-[#8A8A8A] font-inter font-semibold text-base text-left" textWrap="true" />
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end" class="mt-4">
                        <Label text="Отмена" class="text-[#964BDC] font-inter font-semibold text-sm py-2 px-4 mr-4" @tap="closeDeleteModal" />
                        <Label text="Удалить" class="text-[#DE6C35] font-inter font-semibold text-sm py-2 px-4" @tap="confirmDelete" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showDeleteCategoryModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeDeleteCategoryModal" zIndex="1000" />

            <GridLayout v-if="showDeleteCategoryModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Удалить категорию без возможности восстановления?" class="text-[#8A8A8A] font-inter font-semibold text-base text-left" textWrap="true" />
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end" class="mt-4">
                        <Label text="Отмена" class="text-[#964BDC] font-inter font-semibold text-sm py-2 px-4 mr-4" @tap="closeDeleteCategoryModal" />
                        <Label text="Удалить" class="text-[#DE6C35] font-inter font-semibold text-sm py-2 px-4" @tap="confirmDeleteCategory" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeAddModal" zIndex="1000" />

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Новый доход" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="sourceFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusSource">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Источник дохода" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="sourceField" v-model="newIncome.source" hint="Зарплата" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="sourceFocused = true" @blur="sourceFocused = false" autocorrect="false" />
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

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="dateFocused ? 'border-[#964BDC] border-5' : dateError ? 'border-[#FF0000] border-5' : 'border-[#262626] border-5'" @tap="focusDate">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Дата" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="dateField" v-model="newIncome.date" hint="ДД.ММ.ГГГГ" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="dateFocused = true" @blur="validateDate" />
                        </StackLayout>
                    </GridLayout>
                    <Label v-if="dateError" :text="dateError" class="text-[#FF0000] font-inter text-xs ml-1 mt-1 mb-2" />

                    <ActivityIndicator v-if="isAdding" :busy="true" color="#964BDC" class="my-4" />

                    <Button v-else text="Добавить доход" :class="['bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', !isAddFormValid ? 'bg-[#969696]' : '']" :isEnabled="isAddFormValid" @tap="addNewIncome" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeAddModal" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { $navigateTo } from 'nativescript-vue';
import Menu from './Menu.vue';
import { IncomesProvider } from '~/providers/income.provider';
import { CategoriesProvider } from '~/providers/categories.provider';
import { BudgetProvider } from '~/providers/budget.provider';
import { Category } from '~/models/categories.types';
import { Income } from '../models/income.types';
import CategorySelector from './CategorySelector.vue';

interface BudgetCategory extends Category {
    percent: string;
    focused: boolean;
}

export default defineComponent({
    components: {
        Menu
    },
    data() {
        return {
            activeTab: 'settings',
            incomesProvider: new IncomesProvider(),
            categoriesProvider: new CategoriesProvider(),
            budgetProvider: new BudgetProvider(),

            incomes: [] as Income[],
            allCategories: [] as Category[],
            distributionCategories: [] as BudgetCategory[],
            plannedIncome: 0,

            isLoading: true,
            isAdding: false,
            isSaving: false,

            showDeleteModalFlag: false,
            showDeleteCategoryModalFlag: false,
            showAddModalFlag: false,

            showNewCategory: false,
            newCategoryPercent: '',
            newCategoryFocused: false,
            selectedCategory: null as Category | null,
            selectedCategoryName: '',

            selectedIncome: null as Income | null,
            selectedCategoryToDelete: null as BudgetCategory | null,

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
        remainingPercent(): number {
            return 100 - this.totalDistributionPercent;
        },
        isDistributionValid(): boolean {
            return Math.abs(this.totalDistributionPercent - 100) < 0.01;
        },
        hasEmptyPercentages(): boolean {
            const emptyInExisting = this.distributionCategories.some(cat => !cat.percent || cat.percent === '');
            const emptyInNew = this.showNewCategory && (!this.newCategoryPercent || this.newCategoryPercent === '');
            return emptyInExisting || emptyInNew;
        },
        isSaveEnabled(): boolean {
            return this.isDistributionValid && !this.hasEmptyPercentages;
        },
        distributionBarColor(): string {
            if (this.totalDistributionPercent > 100) {
                return '#FF0000';
            }
            return this.isDistributionValid ? '#A2E809' : '#FF0000';
        },
        distributionPercentage(): number {
            return Math.min(this.totalDistributionPercent, 100);
        },
        distributionStatusText(): string {
            if (this.totalDistributionPercent > 100) {
                return `Превышение на ${(this.totalDistributionPercent - 100).toFixed(1)} %`;
            } else if (this.isDistributionValid) {
                return '100 %';
            } else {
                const remaining = this.remainingPercent.toFixed(1);
                return `Осталось распределить ${remaining} %`;
            }
        }
    },
    async mounted() {
        await this.loadData();
    },
    methods: {
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
                await this.loadIncomes();
                await this.loadCategories();
                await this.loadBudgetStatus();
            } finally {
                this.isLoading = false;
            }
        },

        async loadIncomes(): Promise<void> {
            this.incomes = await this.incomesProvider.getIncomes();
        },

        async loadCategories(): Promise<void> {
            this.allCategories = await this.categoriesProvider.getCategories();
        },

        async loadBudgetStatus(): Promise<void> {
            try {
                const status = await this.budgetProvider.getBudgetStatus();

                this.plannedIncome = status.plannedIncome;

                const percentMap = new Map<number, number>();
                status.categories.forEach(cat => {
                    percentMap.set(cat.categoryId, cat.percent);
                });

                this.distributionCategories = this.allCategories
                    .filter(c => {
                        const percent = percentMap.get(c.id) || 0;
                        return percent > 0;
                    })
                    .map(c => ({
                        ...c,
                        percent: (percentMap.get(c.id) || 0).toString(),
                        focused: false
                    }));
            } catch (error) {
                if (error instanceof Error && error.message.includes('404')) {
                    this.distributionCategories = [];
                }
            }
        },

        formatAmount(amount: number): string {
            const rounded = Math.round(amount * 100) / 100;
            return rounded.toLocaleString('ru-RU', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }).replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },

        formatDate(date: string): string {
            const d = new Date(date);
            const day = d.getDate().toString().padStart(2, '0');
            const month = (d.getMonth() + 1).toString().padStart(2, '0');
            const year = d.getFullYear();
            return `${day}.${month}.${year}`;
        },

        getSourceWord(count: number): string {
            if (count === 1) return 'источник';
            if (count >= 2 && count <= 4) return 'источника';
            return 'источников';
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

        getOtherCategoriesTotal(excludeId: number): number {
            let total = 0;
            for (const cat of this.distributionCategories) {
                if (cat.id !== excludeId) {
                    const val = parseFloat(cat.percent);
                    if (!isNaN(val)) total += val;
                }
            }
            if (this.showNewCategory && this.newCategoryPercent) {
                const val = parseFloat(this.newCategoryPercent);
                if (!isNaN(val)) total += val;
            }
            return total;
        },

        onPercentChange(category: BudgetCategory): void {
            let value = category.percent;
            
            if (value === '' || value === null || value === undefined) {
                this.calculateDistribution();
                return;
            }
            
            let numValue = parseFloat(value);
            if (isNaN(numValue)) {
                if (value === '') {
                    this.calculateDistribution();
                }
                return;
            }
            
            if (numValue < 0) numValue = 0;
            if (numValue > 100) numValue = 100;
            
            category.percent = numValue.toString();
            
            this.calculateDistribution();
        },

        onNewPercentChange(): void {
            let value = this.newCategoryPercent;
            
            if (value === '' || value === null || value === undefined) {
                this.calculateDistribution();
                return;
            }
            
            let numValue = parseFloat(value);
            if (isNaN(numValue)) {
                if (value === '') {
                    this.calculateDistribution();
                }
                return;
            }
            
            if (numValue < 0) numValue = 0;
            if (numValue > 100) numValue = 100;
            
            this.newCategoryPercent = numValue.toString();
            
            this.calculateDistribution();
        },

        addNewCategoryFromPlus(): void {
            this.showNewCategory = true;
            this.newCategoryPercent = '';
            this.selectedCategoryName = '';
            setTimeout(() => {
                const scrollView = this.$refs.scrollView as any;
                if (scrollView && scrollView.nativeView) {
                    scrollView.nativeView.scrollToVerticalOffset(10000, true);
                }
            }, 100);
        },

        cancelNewCategory(): void {
            this.showNewCategory = false;
            this.newCategoryPercent = '';
            this.newCategoryFocused = false;
            this.selectedCategory = null;
            this.selectedCategoryName = '';
        },

        removeCategoryFromDistribution(category: BudgetCategory): void {
            const index = this.distributionCategories.findIndex(c => c.id === category.id);
            if (index !== -1) {
                this.distributionCategories.splice(index, 1);
                this.calculateDistribution();
            }
        },

        showDeleteModal(income: Income): void {
            this.selectedIncome = income;
            this.showDeleteModalFlag = true;
        },

        closeDeleteModal(): void {
            this.showDeleteModalFlag = false;
            this.selectedIncome = null;
        },

        async confirmDelete(): Promise<void> {
            if (this.selectedIncome) {
                this.isLoading = true;
                try {
                    await this.incomesProvider.deleteIncome(this.selectedIncome.id);
                    await this.loadIncomes();
                    await this.updateBudgetAfterIncomeChange();
                    await this.loadBudgetStatus();
                    await this.budgetProvider.getAlerts();
                } finally {
                    this.isLoading = false;
                    this.closeDeleteModal();
                }
            }
        },

        showDeleteCategoryModal(category: BudgetCategory): void {
            this.selectedCategoryToDelete = category;
            this.showDeleteCategoryModalFlag = true;
        },

        closeDeleteCategoryModal(): void {
            this.showDeleteCategoryModalFlag = false;
            this.selectedCategoryToDelete = null;
        },

        async confirmDeleteCategory(): Promise<void> {
            if (this.selectedCategoryToDelete) {
                this.removeCategoryFromDistribution(this.selectedCategoryToDelete);
                this.closeDeleteCategoryModal();
                this.calculateDistribution();
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
            $navigateTo(CategorySelector, {
                transition: { name: 'slideLeft', duration: 300 },
                props: {
                    allCategories: this.allCategories,
                    onCategorySelected: (category: Category) => {
                        if (!category) return;
                        const exists = this.distributionCategories.some(c => c.id === category.id);
                        if (exists) return;
                        this.selectedCategory = category;
                        this.selectedCategoryName = category.name;
                        this.distributionCategories.unshift({
                            ...category,
                            percent: '',
                            focused: false
                        });
                        if (!this.allCategories.some(c => c.id === category.id)) {
                            this.allCategories.push(category);
                        }
                        this.showNewCategory = false;
                        this.newCategoryPercent = '';
                        this.calculateDistribution();
                    }
                }
            });
        },

        async addNewIncome(): Promise<void> {
            if (this.isAddFormValid) {
                this.isAdding = true;
                try {
                    const amount = parseFloat(this.newIncome.amount);
                    await this.incomesProvider.createIncome({
                        source: this.newIncome.source || undefined,
                        amount: amount,
                        date: this.newIncome.date
                    });
                    await this.loadIncomes();
                    await this.updateBudgetAfterIncomeChange();
                    await this.loadBudgetStatus();
                    await this.budgetProvider.getAlerts();
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

        async saveDistribution(): Promise<void> {
            if (!this.isSaveEnabled) return;

            this.isSaving = true;
            try {
                const percentages: Record<string, number> = {};
                this.distributionCategories.forEach(category => {
                    const percentValue = parseFloat(category.percent);
                    if (category.percent && !isNaN(percentValue) && percentValue > 0) {
                        percentages[category.id.toString()] = percentValue;
                    }
                });

                if (Object.keys(percentages).length === 0) {
                    return;
                }

                await this.budgetProvider.setupBudget({
                    plannedIncome: this.totalIncome,
                    percentages: percentages
                });

                await this.budgetProvider.getAlerts();

                this.$navigateTo(require('./MyBudget.vue').default, {
                    transition: { name: 'slideLeft', duration: 300 },
                    clearHistory: true
                });
            } finally {
                this.isSaving = false;
            }
        },

        closeDistribution(): void {
            this.loadCategories();
            this.loadBudgetStatus();
            this.showNewCategory = false;
            this.newCategoryPercent = '';
            this.selectedCategory = null;
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
        }
    }
});
</script>