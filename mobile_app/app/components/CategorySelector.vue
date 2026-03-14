<template>
    <Page actionBarHidden="true" backgroundSpanUnderStatusBar="true" class="bg-[#13131A]">
        <GridLayout rows="*, auto" columns="*" class="bg-[#13131A]">
            
            <!-- СЛОЙ 1: ОСНОВНОЙ КОНТЕНТ (СКРОЛЛИТСЯ) -->
            <ScrollView row="0" col="0">
                <FlexboxLayout flexDirection="column" alignItems="stretch" class="px-4 pb-20">
                    
                    <!-- Верхняя панель с заголовком -->
                    <GridLayout rows="auto" columns="auto, *" class="mt-4 mb-6">
                        <Image col="0" src="~/assets/images/back.png" 
                               width="20" height="17" 
                               class="justify-start"
                               @tap="goBack" />
                        
                        <Label col="1" text="Выберите категорию" 
                               class="text-white font-inter font-bold text-[18px] ml-6 justify-start" />
                    </GridLayout>
                    
                    <!-- Поисковая строка с кнопкой отмена -->
                    <GridLayout rows="auto" columns="*, auto" class="mb-4">
                        <GridLayout col="0" class="bg-[#3F3D5B] rounded-[32px] border-2 border-[#3F3D5B] w-full h-12 px-4"
                                   :class="{ 'border-[#964BDC]': searchFocused || searchText }">
                            
                            <TextField v-model="searchText"
                                      :hint="!searchText ? 'Найти или создать категорию' : ''"
                                      class="text-white font-inter font-semibold text-[14px] bg-transparent p-0 placeholder-[#8E8E93] h-12"
                                      @focus="searchFocused = true"
                                      @blur="searchFocused = false"
                                      @textChange="filterCategories" />
                        </GridLayout>
                        
                        <Label col="1" v-if="searchText" 
                               text="Отмена" 
                               class="text-[#969696] font-inter font-semibold text-[14px] ml-4 align-middle"
                               @tap="clearSearch" />
                    </GridLayout>
                    
                    <!-- Список категорий -->
                    <StackLayout>
                        <!-- Строка создания новой категории (всегда первая) -->
                        <GridLayout rows="auto" columns="auto, *, auto" 
                                   class="py-3 border-b border-[#2F2D44]"
                                   @tap="showCreateCategoryModal">
                            
                            <Image col="0" src="~/assets/images/star.png" 
                                   width="22" height="22" 
                                   class="justify-start" />
                            
                            <Label col="1" text="Создать новую категорию" 
                                   class="text-white font-inter font-bold text-[14px] ml-4 align-middle" />
                            
                            <Image col="2" src="~/assets/images/next.png" 
                                   width="6" height="12"
                                   class="mr-4 align-middle" />
                        </GridLayout>
                        
                        <!-- Отфильтрованные категории -->
                        <GridLayout v-for="category in filteredCategories" :key="category.id"
                                   rows="auto" columns="auto, *, auto" 
                                   class="py-3 border-b border-[#2F2D44]"
                                   @tap="selectCategory(category)">
                            
                            <Image col="0" :src="'~/assets/images/' + category.icon + '.png'" 
                                   :width="category.iconSize?.width || 22" 
                                   :height="category.iconSize?.height || 22"
                                   class="justify-start" />
                            
                            <Label col="1" :text="category.name" 
                                   class="text-white font-inter font-bold text-[14px] ml-4 align-middle" />
                            
                            <!-- Чекбокс с толщиной 5 -->
                            <GridLayout col="2" width="20" height="20" 
                                       class="border-[5px] border-[#969696] rounded-full mr-4 items-center justify-center"
                                       :class="{ 'border-[#964BDC]': selectedCategoryId === category.id }">
                                <GridLayout v-if="selectedCategoryId === category.id" 
                                           width="10" height="10" 
                                           class="bg-[#964BDC] rounded-full" />
                            </GridLayout>
                        </GridLayout>
                        
                        <!-- Сообщение если ничего не найдено -->
                        <Label v-if="filteredCategories.length === 0 && searchText" 
                               text="Категории не найдены" 
                               class="text-[#969696] font-inter font-semibold text-[14px] text-center mt-6" />
                    </StackLayout>
                    
                    <!-- Дополнительный отступ для контента -->
                    <StackLayout height="20" />
                    
                </FlexboxLayout>
            </ScrollView>
            
            <!-- СЛОЙ 2: КНОПКА ВЫБОРА КАТЕГОРИИ (ПРИБИТА К НИЗУ) -->
            <GridLayout row="1" col="0" 
                       rows="auto" 
                       columns="*"
                       class="px-4 pb-4">
                
                <Button text="Выбрать категорию" 
                        class="h-14 rounded-[32px] font-inter font-bold text-[14px] text-white w-full"
                        :class="selectedCategoryId ? 'bg-[#964BDC] active:bg-[#7B3CB0]' : 'bg-[#969696]'"
                        :isEnabled="!!selectedCategoryId"
                        @tap="confirmSelection" />
            </GridLayout>
            
            <!-- МОДАЛЬНОЕ ОКНО СОЗДАНИЯ КАТЕГОРИИ (ПРОЗРАЧНЫЙ ФОН) -->
            <!-- Затемняющий фон с прозрачностью -->
            <GridLayout v-if="showCreateModal" row="0" col="0" rows="*" columns="*" 
                       backgroundColor="#818181" opacity="0.64"
                       @tap="closeCreateModal"
                       zIndex="1000" />
            
            <!-- Модальное окно поверх фона -->
            <GridLayout v-if="showCreateModal" row="0" col="0" rows="auto" columns="auto" 
                       horizontalAlignment="center" verticalAlignment="center"
                       zIndex="1001">
                <StackLayout class="bg-[#1E1D2E] rounded-[32px] p-4 w-11/12 max-w-400" @tap="preventClose">
                    
                    <!-- Заголовок -->
                    <Label text="Новая категория" 
                           class="text-white font-inter font-bold text-[18px] text-left mb-4" />
                    
                    <!-- Поле ввода названия -->
                    <StackLayout class="w-full mb-4">
                        <GridLayout rows="auto" columns="*" 
                                   class="bg-[#2F2D44] rounded-[32px] border-2 border-[#2F2D44] w-full px-4 pt-2 pb-1 h-14 items-center"
                                   :class="{ 'border-[#964BDC]': newCategoryFocused }">
                            
                            <StackLayout class="align-middle w-full">
                                <Label text="Название категории" 
                                       class="text-[#B4B4B4] font-inter font-semibold text-[12px] mb-0" />
                                
                                <TextField v-model="newCategoryName"
                                          class="text-white font-inter font-semibold text-[14px] bg-transparent p-0 placeholder-[#B4B4B4] h-6"
                                          @focus="newCategoryFocused = true"
                                          @blur="newCategoryFocused = false" />
                            </StackLayout>
                        </GridLayout>
                    </StackLayout>
                    
                    <!-- Кнопки -->
                    <Button text="Добавить" 
                            class="h-14 rounded-[32px] font-inter font-bold text-[14px] text-white w-full mt-4"
                            :class="newCategoryName ? 'bg-[#964BDC] active:bg-[#7B3CB0]' : 'bg-[#969696]'"
                            :isEnabled="!!newCategoryName"
                            @tap="addNewCategory" />

                    <Button text="Отмена" 
                            class="h-14 rounded-[32px] font-inter font-bold text-[14px] text-white w-full bg-[#FF0000] active:bg-[#CC0000] mt-4"
                            @tap="closeCreateModal" />
                </StackLayout>
            </GridLayout>
            
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { getAllCategories, getCategoryById, addCategory, searchCategories, Category } from './data/categories-list';

export default defineComponent({
    props: {
        onCategorySelected: {
            type: Function,
            default: null
        }
    },
    data() {
        return {
            allCategories: [] as Category[],
            filteredCategories: [] as Category[],
            selectedCategoryId: null as number | null,
            selectedCategoryData: null as Category | null,
            searchText: '',
            searchFocused: false,
            showCreateModal: false,
            newCategoryName: '',
            newCategoryFocused: false
        };
    },
    created() {
        this.loadCategories();
    },
    methods: {
        loadCategories(): void {
            this.allCategories = getAllCategories();
            this.filteredCategories = [...this.allCategories];
        },
        
        filterCategories(): void {
            this.filteredCategories = searchCategories(this.searchText);
        },
        
        clearSearch(): void {
            this.searchText = '';
            this.filteredCategories = [...this.allCategories];
        },
        
        selectCategory(category: Category): void {
            this.selectedCategoryId = category.id;
            this.selectedCategoryData = category;
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
            this.showCreateModal = true;
        },
        
        closeCreateModal(): void {
            this.showCreateModal = false;
            this.newCategoryName = '';
            this.newCategoryFocused = false;
        },
        
        addNewCategory(): void {
            if (this.newCategoryName) {
                const newCategory = {
                    name: this.newCategoryName,
                    icon: 'default',
                    iconSize: { width: 48, height: 48 }
                };
                
                const newId = addCategory(newCategory);
                const createdCategory = { ...newCategory, id: newId };
                this.loadCategories();
                
                this.closeCreateModal();
                
                this.selectedCategoryId = newId;
                this.selectedCategoryData = createdCategory;
            }
        },
        
        preventClose(event: any): void {
            event.cancelBubble = true;
        },
        
        goBack(): void {
            this.$navigateBack();
        }
    }
});
</script>