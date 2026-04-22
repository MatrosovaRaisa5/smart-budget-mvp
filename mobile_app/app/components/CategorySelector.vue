<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#121212]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#121212]">

            <ScrollView v-if="!isLoading" row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pt-8 pb-4">

                    <FlexboxLayout flexDirection="row" alignItems="center" class="mb-6">
                        <Image src="~/assets/images/back.png" width="25" height="25" @tap="goBack" />
                        <Label text="Выберите категорию" class="text-white font-inter font-semibold text-2xl ml-4 flex-1" />
                    </FlexboxLayout>

                    <GridLayout rows="auto" columns="*, auto" class="mb-4">
                        <GridLayout col="0" class="bg-[#262626] rounded-2xl px-4 h-14 items-center" :class="searchFocused || searchText ? 'border-[#964BDC] border-5' : ''">
                            <FlexboxLayout flexDirection="row" alignItems="center" class="w-full">
                                <Image src="~/assets/images/search.png" width="14" height="14" class="mr-2" />
                                <TextField v-model="searchText" hint="Найти категорию" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0 flex-1" @focus="searchFocused = true" @blur="searchFocused = false" @textChange="filterCategories" />
                            </FlexboxLayout>
                        </GridLayout>

                        <Label col="1" v-if="searchText" text="Отмена" class="text-[#964BDC] font-inter font-semibold text-sm ml-4 self-center" @tap="clearSearch" />
                    </GridLayout>

                    <StackLayout>
                        <GridLayout rows="auto" columns="auto, *" class="py-3 border-b border-[#262626]" @tap="showCreateCategoryModal">
                            <Label col="1" text="Создать новую категорию" class="text-[#964BDC] font-inter font-semibold text-sm" />
                        </GridLayout>

                        <StackLayout v-if="filteredGlobalCategories.length > 0" class="mt-2">
                            <Label text="Все категории" class="text-[#8A8A8A] font-inter font-semibold text-m mb-2" />
                            <GridLayout v-for="category in filteredGlobalCategories" :key="category.id" rows="auto" columns="auto, *, auto" class="py-3 border-b border-[#262626]" @tap="selectCategory(category)">
                                <Image col="0" :src="getCategoryIcon(category.name)" width="20" height="20" />
                                <Label col="1" :text="category.name" class="text-white font-inter font-semibold text-sm ml-3" />
                                <GridLayout col="2" width="20" height="20" class="rounded-full border-5 mr-2 items-center justify-center" :class="selectedCategoryId === category.id ? 'border-[#964BDC] bg-[#964BDC]' : 'border-[#8A8A8A]'">
                                    <Label v-if="selectedCategoryId === category.id" text="✓" color="white" fontSize="12" fontWeight="bold" textAlignment="center" verticalAlignment="center" />
                                </GridLayout>
                            </GridLayout>
                        </StackLayout>

                        <StackLayout v-if="filteredUserCategories.length > 0" class="mt-4">
                            <Label text="Мои категории" class="text-[#8A8A8A] font-inter font-semibold text-m mb-2" />
                            <GridLayout v-for="category in filteredUserCategories" :key="category.id" rows="auto" columns="auto, *, auto" class="py-3 border-b border-[#262626]" @tap="selectCategory(category)">
                                <Image col="0" :src="getCategoryIcon(category.name)" width="20" height="20" />
                                <Label col="1" :text="category.name" class="text-white font-inter font-semibold text-sm ml-3" />
                                <GridLayout col="2" width="20" height="20" class="rounded-full border-5 mr-2 items-center justify-center" :class="selectedCategoryId === category.id ? 'border-[#964BDC] bg-[#964BDC]' : 'border-[#8A8A8A]'">
                                    <Label v-if="selectedCategoryId === category.id" text="✓" color="white" fontSize="12" fontWeight="bold" textAlignment="center" verticalAlignment="center" />
                                </GridLayout>
                            </GridLayout>
                        </StackLayout>

                        <Label v-if="filteredGlobalCategories.length === 0 && filteredUserCategories.length === 0 && searchText" text="Категории не найдены" class="text-[#8A8A8A] font-inter font-semibold text-sm text-center mt-8" textWrap="true" />
                    </StackLayout>

                </FlexboxLayout>
            </ScrollView>

            <ActivityIndicator v-if="isLoading" row="0" col="0" :busy="true" color="#964BDC" class="my-auto" />

            <GridLayout row="1" col="0" rows="auto" columns="*" class="px-4 pb-4">
                <Button text="Выбрать категорию" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full', selectedCategoryId ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="!!selectedCategoryId" @tap="confirmSelection" />
            </GridLayout>

            <GridLayout v-if="showCreateModal" row="0" col="0" rowSpan="2" backgroundColor="#818181" opacity="0.64" @tap="closeCreateModal" zIndex="1000" />

            <GridLayout v-if="showCreateModal" row="0" col="0" rowSpan="2" horizontalAlignment="center" verticalAlignment="center" zIndex="1001">
                <StackLayout class="bg-[#1E1E1E] rounded-3xl p-5 w-80" @tap="preventClose">
                    <Label text="Новая категория" class="text-white font-inter font-extrabold text-xl text-left mb-4" />

                    <GridLayout rows="auto" class="bg-[#262626] rounded-2xl px-4 min-h-14 items-center pt-1 mb-4" :class="newCategoryFocused ? 'border-[#964BDC] border-5' : 'border-[#262626] border-5'" @tap="focusNewCategory">
                        <StackLayout class="ml-1 py-1 w-full">
                            <Label text="Название категории" class="text-[#8A8A8A] font-inter font-semibold text-xs" />
                            <TextField ref="newCategoryField" v-model="newCategoryName" hint="Новая категория" hintColor="#BEBEBE" class="text-white font-inter font-medium text-sm bg-transparent p-0" @focus="newCategoryFocused = true" @blur="newCategoryFocused = false" />
                        </StackLayout>
                    </GridLayout>
                    <Label v-if="categoryError" :text="categoryError" class="text-[#FF0000] font-inter text-xs ml-1 mt-1 mb-2" />

                    <ActivityIndicator v-if="isCreating" :busy="true" color="#964BDC" class="my-4" />

                    <Button v-else text="Добавить" :class="['text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-2', newCategoryName ? 'bg-[#964BDC]' : 'bg-[#969696]']" :isEnabled="!!newCategoryName" @tap="addNewCategory" />

                    <Button text="Отмена" class="bg-[#DE6C35] text-white font-inter font-semibold text-sm h-12 rounded-2xl w-full mt-3" @tap="closeCreateModal" />
                </StackLayout>
            </GridLayout>

        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { $navigateBack } from 'nativescript-vue';
import { CategoriesProvider } from '~/providers/categories.provider';
import { Category } from '~/models/categories.types';

export default defineComponent({
    props: {
        onCategorySelected: {
            type: Function,
            default: null
        }
    },
    data() {
        return {
            categoriesProvider: new CategoriesProvider(),
            allCategories: [] as Category[],
            filteredGlobalCategories: [] as Category[],
            filteredUserCategories: [] as Category[],
            selectedCategoryId: null as number | null,
            selectedCategoryData: null as Category | null,
            searchText: '',
            searchFocused: false,
            showCreateModal: false,
            newCategoryName: '',
            newCategoryFocused: false,
            categoryError: '',
            isLoading: true,
            isCreating: false
        };
    },
    async mounted() {
        await this.loadCategories();
    },
    methods: {
        focusNewCategory() {
            this.newCategoryFocused = true;
            setTimeout(() => {
                const field = this.$refs.newCategoryField as any;
                if (field && field.nativeView) {
                    field.nativeView.focus();
                }
            }, 100);
        },

        async loadCategories(): Promise<void> {
            this.isLoading = true;
            try {
                this.allCategories = await this.categoriesProvider.getCategories();
                this.filterCategories();
            } finally {
                this.isLoading = false;
            }
        },

        filterCategories(): void {
            if (!this.searchText) {
                this.filteredGlobalCategories = this.allCategories.filter(c => c.global);
                this.filteredUserCategories = this.allCategories.filter(c => !c.global);
                return;
            }
            const searchLower = this.searchText.toLowerCase();
            const filtered = this.allCategories.filter(c => c.name.toLowerCase().includes(searchLower));
            this.filteredGlobalCategories = filtered.filter(c => c.global);
            this.filteredUserCategories = filtered.filter(c => !c.global);
        },

        clearSearch(): void {
            this.searchText = '';
            this.filterCategories();
        },

        confirmSelection(): void {
            if (this.selectedCategoryData) {
                if (this.onCategorySelected) {
                    this.onCategorySelected(this.selectedCategoryData);
                }
                this.$navigateBack();
            }
        },

        showCreateCategoryModal(): void {
            this.newCategoryName = this.searchText || '';
            this.categoryError = '';
            this.showCreateModal = true;
        },

        closeCreateModal(): void {
            this.showCreateModal = false;
            this.newCategoryName = '';
            this.newCategoryFocused = false;
            this.categoryError = '';
        },

        async addNewCategory(): Promise<void> {
            if (!this.newCategoryName) return;

            this.isCreating = true;
            this.categoryError = '';

            try {
                const newCategory = await this.categoriesProvider.createCategory({
                    name: this.newCategoryName.trim()
                });

                if (this.onCategorySelected) {
                    this.onCategorySelected(newCategory);
                }

                this.closeCreateModal();
                this.$navigateBack();

            } catch (error) {
                if (error instanceof Error) {
                    this.categoryError = error.message;
                }
            } finally {
                this.isCreating = false;
            }
        },

        selectCategory(category: Category): void {
            this.selectedCategoryId = category.id;
            this.selectedCategoryData = category;
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

        preventClose(event: any): void {
            event.cancelBubble = true;
        },

        goBack(): void {
            $navigateBack();
        }
    },
    watch: {
        searchText() {
            this.filterCategories();
        }
    }
});
</script>