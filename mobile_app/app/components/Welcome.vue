<template>
    <Page actionBarHidden="true">
        <GridLayout
            rows="*"
            columns="*"
            backgroundColor="#ffffff"
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

export default defineComponent({
});
</script>