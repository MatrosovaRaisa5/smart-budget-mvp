<!-- app/components/Menu.vue -->
<template>
    <!-- Меню всегда прибито к низу -->
    <FlexboxLayout flexDirection="row" justifyContent="space-around" alignItems="center"
                   class="menu-container"
                   paddingLeft="16" paddingRight="16"
                   paddingTop="12" paddingBottom="12"
                   @tap="preventClose">

        <!-- Секция 1: Мой бюджет -->
        <StackLayout class="menu-section" @tap="navigateTo('budget')">
            <Image :src="activeTab === 'budget' ? '~/assets/images/budgetActive.png' : '~/assets/images/budgetUnActive.png'"
                   width="26" height="24"
                   horizontalAlignment="center" />
            <Label text="Мой бюджет"
                   :class="['menu-text', activeTab === 'budget' ? 'active' : 'inactive']"
                   textWrap="false" />
        </StackLayout>

        <!-- Секция 2: История -->
        <StackLayout class="menu-section" @tap="navigateTo('history')">
            <Image :src="activeTab === 'history' ? '~/assets/images/clockActive.png' : '~/assets/images/clockUnActive.png'"
                   width="24" height="24"
                   horizontalAlignment="center" />
            <Label text="История"
                   :class="['menu-text', activeTab === 'history' ? 'active' : 'inactive']"
                   textWrap="false" />
        </StackLayout>

        <!-- Секция 3: Мои цели -->
        <StackLayout class="menu-section" @tap="navigateTo('goals')">
            <Image :src="activeTab === 'goals' ? '~/assets/images/aimActive.png' : '~/assets/images/aimUnActive.png'"
                   width="25" height="24"
                   horizontalAlignment="center" />
            <Label text="Мои цели"
                   :class="['menu-text', activeTab === 'goals' ? 'active' : 'inactive']"
                   textWrap="false" />
        </StackLayout>

        <!-- Секция 4: Профиль -->
        <StackLayout class="menu-section" @tap="navigateTo('profile')">
            <Image :src="activeTab === 'profile' ? '~/assets/images/profileActive.png' : '~/assets/images/profileUnActive.png'"
                   width="18" height="24"
                   horizontalAlignment="center" />
            <Label text="Профиль"
                   :class="['menu-text', activeTab === 'profile' ? 'active' : 'inactive']"
                   textWrap="false" />
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
            // Предотвращаем всплытие события
        }
    }
});
</script>

<style scoped>
.menu-container {
    background-color: #2F2D44;
    border-radius: 50;
    margin-left: 16;
    margin-right: 16;
    margin-bottom: 16;
}

.menu-section {
    horizontal-align: center;
    vertical-align: center;
}

.menu-text {
    font-family: 'Inter';
    font-weight: bold;
    font-size: 10;
    margin-top: 4;
    text-align: center;
    width: auto;
    color: #969696;
}

.menu-text.active {
    color: #964BDC;
}
</style>
