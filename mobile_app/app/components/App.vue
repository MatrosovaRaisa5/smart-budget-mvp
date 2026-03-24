<template>
  <Frame id="root-frame">
    <Welcome v-if="!initialCheckDone" />
    <template v-else>
      <Login v-if="!isAuthenticated" />
      <MyBudget v-else />
    </template>
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
    MyBudget
  },

  data() {
    return {
      isAuthenticated: false,
      initialCheckDone: false,
      auth: new AuthProvider()
    };
  },

  mounted() {
    this.checkAuth();
  },

  methods: {
    async checkAuth() {
      this.isAuthenticated = await this.auth.checkAuth();
      this.initialCheckDone = true;
    }
  }
});
</script>
