<template>
    <FlexboxLayout flexDirection="row" justifyContent="space-between" alignItems="center"
                   class="bg-[#121212] py-3 px-4"
                   @tap="preventClose">

        <StackLayout class="items-center" @tap="navigateTo('budget')">
            <Image :src="activeTab === 'budget' ? '~/assets/images/budgetActive.png' : '~/assets/images/budgetUnActive.png'"
                   width="26" height="26" />
            <Label text="Мой бюджет"
                   :class="['font-inter font-semibold text-[10px] mt-1 text-center', activeTab === 'budget' ? 'text-[#964BDC]' : 'text-[#969696]']" />
        </StackLayout>

        <StackLayout class="items-center" @tap="navigateTo('settings')">
            <Image :src="activeTab === 'settings' ? '~/assets/images/settingsActive.png' : '~/assets/images/settingsUnActive.png'"
                   width="26" height="26" />
            <StackLayout class="items-center">
                <Label text="Настройки"
                    :class="['font-inter font-semibold text-[10px] mt-1 text-center', activeTab === 'settings' ? 'text-[#964BDC]' : 'text-[#969696]']" />
            </StackLayout>
        </StackLayout>

        <StackLayout class="items-center" @tap="navigateTo('history')">
            <Image :src="activeTab === 'history' ? '~/assets/images/clockActive.png' : '~/assets/images/clockUnActive.png'"
                   width="26" height="26" />
            <Label text="История"
                   :class="['font-inter font-semibold text-[10px] mt-1 text-center', activeTab === 'history' ? 'text-[#964BDC]' : 'text-[#969696]']" />
        </StackLayout>

        <StackLayout class="items-center" @tap="navigateTo('goals')">
            <Image :src="activeTab === 'goals' ? '~/assets/images/aimActive.png' : '~/assets/images/aimUnActive.png'"
                   width="26" height="26" />
            <Label text="Мои цели"
                   :class="['font-inter font-semibold text-[10px] mt-1 text-center', activeTab === 'goals' ? 'text-[#964BDC]' : 'text-[#969696]']" />
        </StackLayout>

        <StackLayout class="items-center" @tap="navigateTo('profile')">
            <Image :src="activeTab === 'profile' ? '~/assets/images/profileActive.png' : '~/assets/images/profileUnActive.png'"
                   width="26" height="26" />
            <Label text="Профиль"
                   :class="['font-inter font-semibold text-[10px] mt-1 text-center', activeTab === 'profile' ? 'text-[#964BDC]' : 'text-[#969696]']" />
        </StackLayout>

    </FlexboxLayout>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';

export default defineComponent({
    props: {
        activeTab: {
            type: String,
            default: 'budget'
        }
    },
    methods: {
        navigateTo(tab: string): void {
            this.$emit('update:activeTab', tab);

            switch(tab) {
                case 'budget':
                    this.$navigateTo(require('./MyBudget.vue').default, {
                        transition: { name: 'fade', duration: 300 }
                    });
                    break;
                case 'settings':
                    this.$navigateTo(require('./Settings.vue').default, {
                        transition: { name: 'fade', duration: 300 }
                    });
                    break;
                case 'history':
                    this.$navigateTo(require('./History.vue').default, {
                        transition: { name: 'fade', duration: 300 }
                    });
                    break;
                case 'goals':
                    this.$navigateTo(require('./Goals.vue').default, {
                        transition: { name: 'fade', duration: 300 }
                    });
                    break;
                case 'profile':
                    this.$navigateTo(require('./Profile.vue').default, {
                        transition: { name: 'fade', duration: 300 }
                    });
                    break;
            }
        },

        preventClose(): void {
        }
    }
});
</script>