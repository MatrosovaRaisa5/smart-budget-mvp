<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView v-if="!isLoading" row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">

                    <FlexboxLayout alignItems="center" class="mb-6">
                        <Label text="Мои цели" class="text-white font-inter font-semibold text-2xl" />
                    </FlexboxLayout>

                    <StackLayout v-if="goals.length > 0">
                        <StackLayout v-for="goal in goals" :key="goal.id" class="bg-[#1E1E1E] rounded-3xl p-4 mb-4">
                            <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center" class="mb-3">
                                <FlexboxLayout flexDirection="row" alignItems="center" class="flex-1">
                                    <Image src="~/assets/images/goals.png" width="34" height="34" />
                                    <Label :text="goal.name" class="text-white font-inter font-semibold text-s ml-3 flex-1" textWrap="true" />
                                </FlexboxLayout>
                                <Button text="Действие" class="bg-[#4AC13D] text-[#121212] font-inter font-semibold text-xs pl-5 pr-5 h-8 rounded-3xl px-3" @tap="showActionModal(goal)" />
                            </FlexboxLayout>

                            <Label :text="'Рекомендуемый ежемесячный взнос ' + formatAmount(calculateMonthly(goal)) + ' ₽'" class="text-white font-inter font-semibold text-sm mb-3" textWrap="true" />

                            <GridLayout rows="auto" columns="*" height="4" class="rounded-full overflow-hidden mb-3">
                                <GridLayout col="0" backgroundColor="#454545" height="4" width="100%" borderRadius="2" />
                                <GridLayout col="0" :backgroundColor="getProgressColor(goal)" :width="getProgressPercent(goal) + '%'" height="4" borderRadius="2" horizontalAlignment="left" />
                            </GridLayout>

                            <StackLayout>
                                <Label v-if="goal.targetAmount - goal.savedAmount <= 0" text="Цель достигнута" class="text-[#A2E809] font-inter font-semibold text-sm text-right" />
                                <Label v-else :text="'Осталось ' + formatAmount(goal.targetAmount - goal.savedAmount) + ' ₽'" class="text-white font-inter font-semibold text-sm text-right" />
                                <Label :text="'Цель ' + formatAmount(goal.targetAmount) + ' ₽ до ' + formatDate(goal.deadline)" class="text-[#8A8A8A] font-inter font-normal text-xs text-right mt-1" />
                            </StackLayout>
                        </StackLayout>
                    </StackLayout>

                    <Label v-if="goals.length === 0 && !isLoading" text="Нет целей" class="text-[#8A8A8A] font-inter font-semibold text-sm text-center mt-8" textWrap="true" />

                </FlexboxLayout>
            </ScrollView>

            <ActivityIndicator v-if="isLoading" row="0" col="0" :busy="true" color="#964BDC" class="my-auto" />

            <GridLayout row="1" col="0" rows="auto, auto" columns="*" class="bg-[#121212]">
                <StackLayout row="0" class="px-4 pb-4">
                    <Button text="Добавить цель" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full" @tap="showAddModal" />
                </StackLayout>

                <Menu row="1" col="0" verticalAlignment="bottom" :activeTab="activeTab" @update:activeTab="activeTab = $event" />
            </GridLayout>

            <GridLayout v-if="showWelcomePopup && goals.length === 0" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeWelcomePopup" zIndex="1000" />

            <GridLayout v-if="showWelcomePopup && goals.length === 0" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" @tap="closeWelcomePopup" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-80" @tap="preventClose">
                    <Image src="~/assets/images/smile.png" width="60" height="60" class="self-center" />
                    <Label text="Пора ставить цели!" class="text-[#8A8A8A] font-inter font-semibold text-base text-center mt-3" textWrap="true" />
                    <Label text="Нажмите кнопку ниже" class="text-[#8A8A8A] font-inter font-semibold text-xs text-center mt-1" textWrap="true" />
                    <Button text="Добавить цель" class="bg-[#964BDC] text-white font-inter font-semibold text-sm h-12 rounded-2xl mt-4" @tap="showAddModalFromWelcome" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeAddModal" zIndex="1000" />

            <GridLayout v-if="showAddModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Новая цель" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="nameFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusName">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="На что копите" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="nameField" v-model="newGoal.name" hint="Новая цель" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="nameFocused = true" @blur="nameFocused = false" autocorrect="false" />
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="targetAmountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusTargetAmount">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Сколько хотите накопить" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="targetAmountField" v-model="displayTargetAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" keyboardType="number" @focus="targetAmountFocused = true" @blur="targetAmountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="dateFocused ? 'border-[#964BDC] border-5' : dateError ? 'border-[#FF0000] border-5' : 'border-[#262626] border-5'" @tap="focusDate">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Дата" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="dateField" v-model="newGoal.deadline" hint="ДД.ММ.ГГГГ" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="dateFocused = true" @blur="validateDate" />
                        </StackLayout>
                    </GridLayout>
                    <Label v-if="dateError" :text="dateError" class="text-[#FF0000] font-inter text-xs ml-1 mt-1 mb-2" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="currentAmountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusCurrentAmount">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Текущая накопленная сумма" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="currentAmountField" v-model="displayCurrentAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" keyboardType="number" @focus="currentAmountFocused = true" @blur="currentAmountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <Label :text="'Рекомендуемый ежемесячный взнос ' + formatAmount(calculatedMonthly) + ' ₽'" class="text-[#8A8A8A] font-inter font-normal text-xs mb-4" textWrap="true" />

                    <ActivityIndicator v-if="isAdding" :busy="true" color="#964BDC" class="my-4" />

                    <Button v-else text="Добавить цель" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', isAddGoalValid ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="isAddGoalValid" @tap="addGoal" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeAddModal" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showEditModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeEditModal" zIndex="1000" />

            <GridLayout v-if="showEditModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Настройка цели" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="editNameFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusEditName">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="На что копите" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="editNameField" v-model="editingGoal.name" hint="Новая цель" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="editNameFocused = true" @blur="editNameFocused = false" autocorrect="false" />
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="editTargetAmountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusEditTargetAmount">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Сколько хотите накопить" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="editTargetAmountField" v-model="displayEditTargetAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" keyboardType="number" @focus="editTargetAmountFocused = true" @blur="editTargetAmountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="editDateFocused ? 'border-[#964BDC] border-5' : editDateError ? 'border-[#FF0000] border-5' : 'border-[#262626] border-5'" @tap="focusEditDate">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Дата" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="editDateField" v-model="editingGoal.deadline" hint="ДД.ММ.ГГГГ" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="editDateFocused = true" @blur="validateEditDate" />
                        </StackLayout>
                    </GridLayout>
                    <Label v-if="editDateError" :text="editDateError" class="text-[#FF0000] font-inter text-xs ml-1 mt-1 mb-2" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="editCurrentAmountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusEditCurrentAmount">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Текущая накопленная сумма" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="editCurrentAmountField" v-model="displayEditCurrentAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" keyboardType="number" @focus="editCurrentAmountFocused = true" @blur="editCurrentAmountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <Label :text="'Рекомендуемый ежемесячный взнос ' + formatAmount(calculateMonthly(editingGoal)) + ' ₽'" class="text-[#8A8A8A] font-inter font-normal text-xs mb-4" textWrap="true" />

                    <Button text="Сохранить" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', isEditGoalValid ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="isEditGoalValid" @tap="updateGoal" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeEditModal" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showDepositModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeDepositModal" zIndex="1000" />

            <GridLayout v-if="showDepositModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Пополнение" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="depositAmountFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusDepositAmount">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="На сколько хотите пополнить" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <FlexboxLayout flexDirection="row" alignItems="center">
                                <TextField ref="depositAmountField" v-model="displayDepositAmount" hint="0" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" keyboardType="number" @focus="depositAmountFocused = true" @blur="depositAmountFocused = false" />
                                <Label text="₽" class="text-white font-inter font-medium text-sm ml-1" />
                            </FlexboxLayout>
                        </StackLayout>
                    </GridLayout>

                    <Button text="Пополнить" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', depositAmount && parseFloat(depositAmount) > 0 ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="depositAmount && parseFloat(depositAmount) > 0" @tap="addDeposit" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeDepositModal" />
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeDeleteModal" zIndex="1000" />

            <GridLayout v-if="showDeleteModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-white rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Удалить цель без возможности восстановления?" class="text-[#8A8A8A] font-inter font-semibold text-base text-left" textWrap="true" />
                    <FlexboxLayout flexDirection="row" justifyContent="flex-end" class="mt-4">
                        <Label text="Отмена" class="text-[#964BDC] font-inter font-semibold text-sm py-2 px-4 mr-4" @tap="closeDeleteModal" />
                        <Label text="Удалить" class="text-[#DE6C35] font-inter font-semibold text-sm py-2 px-4" @tap="confirmDelete" />
                    </FlexboxLayout>
                </StackLayout>
            </GridLayout>

            <GridLayout v-if="showActionModalFlag" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeActionModal" zIndex="1000" />

            <GridLayout v-if="showActionModalFlag" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Действия с целью" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" columns="auto, *" class="mb-4 items-center py-2" @tap="closeActionModalAndDeposit">
                        <Image col="0" src="~/assets/images/deposit.png" width="24" height="24" />
                        <Label col="1" text="Пополнить" class="text-white font-inter font-semibold text-sm ml-4" />
                    </GridLayout>

                    <GridLayout rows="auto" columns="auto, *" class="mb-4 items-center py-2" @tap="closeActionModalAndEdit">
                        <Image col="0" src="~/assets/images/edit.png" width="20" height="20" />
                        <Label col="1" text="Редактировать цель" class="text-white font-inter font-semibold text-sm ml-4" />
                    </GridLayout>

                    <GridLayout rows="auto" columns="auto, *" class="mb-4 items-center py-2" @tap="closeActionModalAndDelete">
                        <Image col="0" src="~/assets/images/trash.png" width="24" height="24" />
                        <Label col="1" text="Удалить цель" class="text-white font-inter font-semibold text-sm ml-4" />
                    </GridLayout>

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-4" @tap="closeActionModal" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { Frame } from '@nativescript/core';
import Menu from './Menu.vue';
import { GoalsProvider } from '../providers/goals.provider';
import { SavingsGoal } from '../models/goals.types';

export default defineComponent({
    components: {
        Menu
    },
    data() {
        return {
            activeTab: 'goals',
            goals: [] as SavingsGoal[],
            isLoading: true,
            isAdding: false,
            showWelcomePopup: true,

            showAddModalFlag: false,
            showEditModalFlag: false,
            showDepositModalFlag: false,
            showDeleteModalFlag: false,
            showActionModalFlag: false,
            selectedActionGoal: null as SavingsGoal | null,

            nameFocused: false,
            targetAmountFocused: false,
            dateFocused: false,
            currentAmountFocused: false,
            dateError: '',

            newGoal: {
                name: '',
                targetAmount: '',
                deadline: this.getTodayDate(),
                savedAmount: ''
            },

            editingGoal: {} as SavingsGoal,
            editNameFocused: false,
            editTargetAmountFocused: false,
            editDateFocused: false,
            editCurrentAmountFocused: false,
            editDateError: '',

            depositAmount: '',
            depositAmountFocused: false,
            selectedGoalId: null as number | null,
            deleteGoalId: null as number | null,

            goalsProvider: new GoalsProvider()
        };
    },
    computed: {
        displayTargetAmount: {
            get(): string { return this.newGoal.targetAmount; },
            set(value: string) { this.newGoal.targetAmount = value.replace(/[^\d]/g, ''); }
        },
        displayCurrentAmount: {
            get(): string { return this.newGoal.savedAmount; },
            set(value: string) { this.newGoal.savedAmount = value.replace(/[^\d]/g, ''); }
        },
        displayEditTargetAmount: {
            get(): string { return this.editingGoal.targetAmount?.toString() || ''; },
            set(value: string) { if (this.editingGoal) this.editingGoal.targetAmount = parseFloat(value.replace(/[^\d]/g, '')); }
        },
        displayEditCurrentAmount: {
            get(): string { return this.editingGoal.savedAmount?.toString() || ''; },
            set(value: string) { if (this.editingGoal) this.editingGoal.savedAmount = parseFloat(value.replace(/[^\d]/g, '')); }
        },
        displayDepositAmount: {
            get(): string { return this.depositAmount; },
            set(value: string) { this.depositAmount = value.replace(/[^\d]/g, ''); }
        },
        isAddGoalValid(): boolean {
            return this.newGoal.name.trim() !== '' &&
                   parseFloat(this.newGoal.targetAmount) > 0 &&
                   !this.dateError &&
                   this.isValidDate(this.newGoal.deadline);
        },
        isEditGoalValid(): boolean {
            return this.editingGoal.name?.trim() !== '' &&
                   (this.editingGoal.targetAmount || 0) > 0 &&
                   !this.editDateError &&
                   this.isValidDate(this.editingGoal.deadline);
        },
        calculatedMonthly(): number {
            const target = parseFloat(this.newGoal.targetAmount) || 0;
            const current = parseFloat(this.newGoal.savedAmount) || 0;
            const months = this.getMonthsDifference(this.newGoal.deadline);
            if (months <= 0) return 0;
            return Math.max(0, Math.ceil((target - current) / months));
        }
    },
    async mounted() {
        await this.loadData();
    },
    methods: {
        showActionModal(goal: SavingsGoal) {
            this.selectedActionGoal = goal;
            this.showActionModalFlag = true;
        },
        closeActionModal() {
            this.showActionModalFlag = false;
            this.selectedActionGoal = null;
        },
        closeActionModalAndDeposit() {
            const goal = this.selectedActionGoal;
            if (goal && goal.id) {
                this.closeActionModal();
                this.showDepositModal(goal);
            } else {
                this.closeActionModal();
            }
        },
        closeActionModalAndEdit() {
            const goal = this.selectedActionGoal;
            if (goal && goal.id) {
                this.closeActionModal();
                this.showEditModal(goal);
            } else {
                this.closeActionModal();
            }
        },
        closeActionModalAndDelete() {
            const goal = this.selectedActionGoal;
            if (goal && goal.id) {
                this.closeActionModal();
                this.showDeleteModal(goal);
            } else {
                this.closeActionModal();
            }
        },
        focusName() {
            this.nameFocused = true;
            setTimeout(() => {
                const field = this.$refs.nameField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusTargetAmount() {
            this.targetAmountFocused = true;
            setTimeout(() => {
                const field = this.$refs.targetAmountField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusDate() {
            this.dateFocused = true;
            setTimeout(() => {
                const field = this.$refs.dateField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusCurrentAmount() {
            this.currentAmountFocused = true;
            setTimeout(() => {
                const field = this.$refs.currentAmountField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusEditName() {
            this.editNameFocused = true;
            setTimeout(() => {
                const field = this.$refs.editNameField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusEditTargetAmount() {
            this.editTargetAmountFocused = true;
            setTimeout(() => {
                const field = this.$refs.editTargetAmountField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusEditDate() {
            this.editDateFocused = true;
            setTimeout(() => {
                const field = this.$refs.editDateField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusEditCurrentAmount() {
            this.editCurrentAmountFocused = true;
            setTimeout(() => {
                const field = this.$refs.editCurrentAmountField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        focusDepositAmount() {
            this.depositAmountFocused = true;
            setTimeout(() => {
                const field = this.$refs.depositAmountField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },
        async loadData() {
            this.isLoading = true;
            try {
                let fetchedGoals = await this.goalsProvider.getGoals();
                // Преобразуем deadline из ISO (ГГГГ-ММ-ДД) в ДД.ММ.ГГГГ для единого формата
                this.goals = fetchedGoals.map(goal => ({
                    ...goal,
                    deadline: this.convertToDisplayDate(goal.deadline)
                }));
            } catch (error) {
                console.error('Failed to load goals:', error);
                if (error instanceof Error) {
                    alert(error.message);
                }
                this.goals = [];
            } finally {
                this.isLoading = false;
            }
        },

        convertToDisplayDate(dateString: string): string {
            if (!dateString) return '';
            const isoMatch = dateString.match(/^(\d{4})-(\d{2})-(\d{2})$/);
            if (isoMatch) {
                return `${isoMatch[3]}.${isoMatch[2]}.${isoMatch[1]}`;
            }
            return dateString;
        },

        formatAmount(amount: number): string {
            const rounded = Math.round(amount * 100) / 100;
            return rounded.toLocaleString('ru-RU', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 2
            }).replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
        },

        formatDate(dateString: string): string {
            if (!dateString) return '';
            const isoMatch = dateString.match(/^(\d{4})-(\d{2})-(\d{2})$/);
            if (isoMatch) {
                return `${isoMatch[3]}.${isoMatch[2]}.${isoMatch[1]}`;
            }
            if (dateString.match(/^\d{2}\.\d{2}\.\d{4}$/)) {
                return dateString;
            }
            return dateString;
        },

        getTodayDate(): string {
            const today = new Date();
            const day = String(today.getDate()).padStart(2, '0');
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const year = today.getFullYear();
            return `${day}.${month}.${year}`;
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

        calculateMonthly(goal: SavingsGoal): number {
            const months = this.getMonthsDifference(goal.deadline);
            if (months <= 0) return 0;
            const needed = goal.targetAmount - goal.savedAmount;
            if (needed <= 0) return 0;
            return Math.max(0, Math.ceil(needed / months));
        },

        getProgressPercent(goal: SavingsGoal): number {
            if (goal.targetAmount === 0) return 0;
            return Math.min((goal.savedAmount / goal.targetAmount) * 100, 100);
        },

        getProgressColor(goal: SavingsGoal): string {
            const percent = this.getProgressPercent(goal);
            if (percent >= 100) return '#A2E809';
            if (percent >= 80) return '#A2E809';
            if (percent >= 50) return '#FF5E00';
            return '#FF0000';
        },

        validateDate() {
            this.dateFocused = false;
            if (!this.isValidDate(this.newGoal.deadline)) {
                this.dateError = 'Введите корректную дату';
            } else {
                this.dateError = '';
            }
        },

        validateEditDate() {
            this.editDateFocused = false;
            if (!this.isValidDate(this.editingGoal.deadline)) {
                this.editDateError = 'Введите корректную дату';
            } else {
                this.editDateError = '';
            }
        },

        closeWelcomePopup() {
            this.showWelcomePopup = false;
        },

        showAddModalFromWelcome() {
            this.showWelcomePopup = false;
            this.showAddModal();
        },

        showAddModal() {
            this.newGoal = { name: '', targetAmount: '', deadline: this.getTodayDate(), savedAmount: '' };
            this.dateError = '';
            this.showAddModalFlag = true;
        },

        closeAddModal() {
            this.showAddModalFlag = false;
        },

        async addGoal() {
            if (!this.isAddGoalValid) return;
            this.isAdding = true;
            try {
                const savedAmount = parseFloat(this.newGoal.savedAmount) || 0;
                await this.goalsProvider.createGoal({
                    name: this.newGoal.name.trim(),
                    targetAmount: parseFloat(this.newGoal.targetAmount),
                    savedAmount: savedAmount,
                    deadline: this.newGoal.deadline
                });
                await this.loadData();
                this.closeAddModal();
            } catch (error) {
                console.error('Failed to add goal:', error);
                if (error instanceof Error) {
                    alert(error.message);
                }
            } finally {
                this.isAdding = false;
            }
        },

        showEditModal(goal: SavingsGoal) {
            if (!goal || !goal.id) return;
            // Копируем цель, deadline уже в ДД.ММ.ГГГГ после преобразования в loadData
            this.editingGoal = { ...goal };
            this.showEditModalFlag = true;
        },

        closeEditModal() {
            this.showEditModalFlag = false;
        },

        async updateGoal() {
            if (!this.isEditGoalValid) return;
            try {
                await this.goalsProvider.updateGoal(this.editingGoal.id, {
                    name: this.editingGoal.name,
                    targetAmount: this.editingGoal.targetAmount,
                    savedAmount: this.editingGoal.savedAmount,
                    deadline: this.editingGoal.deadline
                });
                await this.loadData();
                this.closeEditModal();
            } catch (error) {
                console.error('Failed to update goal:', error);
                if (error instanceof Error) {
                    alert(error.message);
                }
            }
        },

        showDepositModal(goal: SavingsGoal) {
            if (!goal || !goal.id) return;
            this.selectedGoalId = goal.id;
            this.depositAmount = '';
            this.showDepositModalFlag = true;
        },

        closeDepositModal() {
            this.showDepositModalFlag = false;
            this.selectedGoalId = null;
            this.depositAmount = '';
        },

        async addDeposit() {
            if (!this.selectedGoalId || !this.depositAmount || parseFloat(this.depositAmount) <= 0) return;
            try {
                await this.goalsProvider.addContribution(this.selectedGoalId, parseFloat(this.depositAmount));
                await this.loadData();
                this.closeDepositModal();
            } catch (error) {
                console.error('Failed to add contribution:', error);
                if (error instanceof Error) {
                    alert(error.message);
                }
            }
        },

        showDeleteModal(goal: SavingsGoal) {
            if (!goal || !goal.id) return;
            this.deleteGoalId = goal.id;
            this.showDeleteModalFlag = true;
        },

        closeDeleteModal() {
            this.showDeleteModalFlag = false;
            this.deleteGoalId = null;
        },

        async confirmDelete() {
            if (this.deleteGoalId) {
                try {
                    await this.goalsProvider.deleteGoal(this.deleteGoalId);
                    await this.loadData();
                    this.closeDeleteModal();
                } catch (error) {
                    console.error('Failed to delete goal:', error);
                    if (error instanceof Error) {
                        alert(error.message);
                    }
                }
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
        }
    }
});
</script>
