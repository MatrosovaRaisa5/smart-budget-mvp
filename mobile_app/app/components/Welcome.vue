<template>
    <Page actionBarHidden="true" @loaded="onPageLoaded">
        <GridLayout
            rows="*"
            columns="*"
            backgroundColor="#ffffff"
            @tap="goToNextPage"
        >
            <Image
                src="~/assets/images/logo.png"
                width="60%"
                stretch="aspectFit"
                horizontalAlignment="center"
                verticalAlignment="center"
            />
        </GridLayout>
    </Page>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { $navigateTo } from 'nativescript-vue';
import Login from './Login.vue';
import MyBudget from './MyBudget.vue';
import { AuthProvider } from '~/providers/auth.provider';

export default defineComponent({
    data() {
        return {
            auth: new AuthProvider(),
            navigationStarted: false
        };
    },

    methods: {
        onPageLoaded(): void {
            this.autoNavigate();
        },

        async autoNavigate(): Promise<void> {
            setTimeout(async () => {
                if (!this.navigationStarted) {
                    this.navigationStarted = true;
                    await this.navigateToNextScreen();
                }
            }, 1500);
        },

        async navigateToNextScreen(): Promise<void> {
            const isAuthenticated = await this.auth.checkAuth();

            if (isAuthenticated) {
                $navigateTo(MyBudget, {
                    transition: { name: 'fade', duration: 300 },
                    clearHistory: true
                });
            } else {
                $navigateTo(Login, {
                    transition: { name: 'fade', duration: 300 },
                    clearHistory: true
                });
            }
        },

        goToNextPage(): void {
            if (!this.navigationStarted) {
                this.navigationStarted = true;
                this.navigateToNextScreen();
            }
        }
    }
});
</script>
