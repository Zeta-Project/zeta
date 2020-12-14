<template>
  <v-container class="col-md-4 col-md-offset-3">
    <v-form
        ref="form"
        @submit="validate"
        lazy-validation
    >
      <p class="font-weight-bold">
        Sign in with your credentials
      </p>
      <v-text-field
          v-model="username"
          :rules="emailRules"
          label="Email"
          required
      ></v-text-field>
      <v-text-field
          v-model="password"
          :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showPassword ? 'text' : 'password'"
          @click:append="showPassword = !showPassword"
          :rules="passwordRules"
          label="Password"
          required
      ></v-text-field>
      <v-checkbox
          v-model="rememberMe"
          label="Remember my login on this computer"
      ></v-checkbox>
      <v-btn
          type="submit"
          @submit="validate"
          block
          color="primary"
      >
        Sign In
      </v-btn>
      <br></br>
      <p class="not-a-member">Not a member?
        <router-link to="/account/signUp">Sign Up Now</router-link> |
        <router-link to="/account/password/forgot">Forgot your password?</router-link>
      </p>
      <p class="disclaimer-links">
        <a href="https://www.htwg-konstanz.de/en/info/disclaimer/" target="_blank">Disclaimer</a> &
        <a href="https://www.htwg-konstanz.de/en/info/imprint/" target="_blank">Imprint</a>
      </p>
    </v-form>
  </v-container>
</template>

<script>
import {AUTH_REQUEST} from "@/store/actions/auth"
import {EventBus} from "@/eventbus/eventbus";

export default {
  name: 'SignIn',
  components: {
  },
  data() {
    return {
      username: "",
      password: "",
      rememberMe: true,
      emailRules: [
        v => !!v || 'Email is required',
        v => !v || /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
      ],
      passwordRules: [
        v => !!v || 'Password is required'
      ],
      showPassword: false
    }
  },
  methods: {
    login: function () {
      const {username, password, rememberMe} = this
      this.$store.dispatch(AUTH_REQUEST, {username, password, rememberMe}).then(() => {
        this.$router.push({ path: '/'}).catch(err => {})
      }).catch(reason => EventBus.$emit('errorMessage', reason))
    },
    validate () {
      if(this.$refs.form.validate())
        this.login()
    },
  }
}
</script>