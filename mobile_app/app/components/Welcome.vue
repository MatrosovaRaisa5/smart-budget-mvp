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
// НЕ ИМПОРТИРУЕМ FRAME ВООБЩЕ

export default defineComponent({
    methods: {
        onPageLoaded(): void {
            console.log('Welcome page loaded');
        },
        
        goToNextPage(): void {
            console.log('Navigating to Login...');
            
            // ТОЛЬКО ТАК - БЕЗ Frame
            this.$navigateTo(require('./Login.vue').default, {
                transition: {
                    name: 'fade',
                    duration: 300
                }
            });
        }
    }
});
</script>