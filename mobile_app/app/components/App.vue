<template>
  <Frame id="root-frame">
    <Welcome v-if="showWelcome" />
    <Login v-else-if="!isAuthenticated" />
    <MyBudget v-else />
  </Frame>
</template>

<script lang="ts">
import { defineComponent } from 'nativescript-vue';
import { AuthProvider } from '~/providers/auth.provider';
import Welcome from './Welcome.vue';
import Login from './Login.vue';
import MyBudget from './MyBudget.vue';

export default defineComponent({
  components: {
    Welcome,
    Login,
    MyBudget,
  },

  data() {
    return {
      isAuthenticated: false,
      showWelcome: true,
      auth: new AuthProvider()
    };
  },

  mounted() {
    setTimeout(async () => {
      this.isAuthenticated = await this.auth.checkAuth();
      this.showWelcome = false;
    }, 3000);
  }
});
</script>
