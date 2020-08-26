<template>
  <fieldset class="col-md-6 col-md-offset-3">
    <div>
      <form @submit.prevent="forgot">
        <legend>Forgot password</legend>
        <p class="info">
          Please enter your email address and we will send you an email
          with further instructions to reset your password.
        </p>
        <div class="form-group">
          <input required v-model="email" type="email" placeholder="Email"
                 class="form-control input-lg"/>
        </div>

        <div class="form-group">
          <button id="submit" type="submit" value="submit" class="btn btn-lg btn-primary btn-block">Sign Up</button>
        </div>

      </form>
    </div>
  </fieldset>
</template>

<script>
export default {
  name: 'PasswordForgot',
  components: {
  },
  data() {
    return {
      email: ""
    }
  },
  methods: {
    forgot: function () {
      console.log(this.email)
    }
  }
}
</script>
