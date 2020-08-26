<template>
  <fieldset class="col-md-6 col-md-offset-3">
    <div>
      <form @submit.prevent="login">
        <legend>Sign in with your credentials</legend>
        <div class="form-group">
          <input required v-model="username" type="email" placeholder="Email" class="form-control input-lg"/>
        </div>
        <div class="form-group">
          <input required v-model="password" type="password" placeholder="password"
                 class="form-control input-lg"/>
        </div>
        <div class="form-group">
          <div class="checkbox">
            <label for="rememberMe">
              <input type="checkbox" id="rememberMe" value="true" checked="true">
              Remember my login on this computer
            </label>
          </div>
        </div>
        <div class="form-group">
          <button id="submit" type="submit" value="submit" class="btn btn-lg btn-primary btn-block">Sign In</button>
        </div>
        <div>
          <p class="not-a-member">Not a member?
            <a href="/account/signUp">Sign Up Now</a> |
            <a href="@routes.ScalaRoutes.getPasswordForgot()" title="@messages(forgot.your.password)">Forgot your password?</a>
          </p>
        </div>
        <div>
          <p class="disclaimer-links">
            <a href="https://www.htwg-konstanz.de/en/info/disclaimer/" target="_blank">Disclaimer</a> &
            <a href="https://www.htwg-konstanz.de/en/info/imprint/" target="_blank">Imprint</a>
          </p>
        </div>
      </form>
    </div>
  </fieldset>
</template>

<script>
import {AUTH_REQUEST} from "@/store/actions/auth"

export default {
  name: 'SignIn',
  components: {
  },
  data() {
    return {
      username: "",
      password: ""
    }
  },
  methods: {
    login: function () {
      const {username, password} = this
      this.$store.dispatch(AUTH_REQUEST, {username, password}).then(() => {
        this.$router.push({ path: '/'})
      })
    }
  }
}
</script>

<style>

</style>
