<template>
  <fieldset class="col-md-6 col-md-offset-3">
    <div>
      <form @submit.prevent="change">
        <legend>Change password</legend>
        <p class="info">
          Strong passwords include numbers, letters and punctuation marks.
        </p>

        <div class="form-group">
          <input required v-model="oldPassword" type="password" placeholder="password"
                 class="form-control input-lg"/>
        </div>

        <section>
          <div class="form-group">
            <input required v-model="newPassword" type="password" placeholder="Password"
                   class="form-control input-lg" data-pwd="true"/>
            <meter max="4" id="password-strength-meter"></meter>
            <p id="password-strength-text"></p>
          </div>
        </section>

        <div class="form-group">
          <button id="submit" type="submit" value="submit" class="btn btn-lg btn-primary btn-block">Sign Up</button>
        </div>

        <div class="sign-in-now">
          <p>
            Already a member?
            <router-link to="/account/signIn">Sign in now</router-link>
          </p>
        </div>

      </form>
    </div>
  </fieldset>
</template>

<script>

export default {
  name: 'PasswordChange',
  components: {
  },
  data() {
    return {
      oldPassword: "",
      newPassword: "",
    }
  },
  methods: {
    change: function () {
      console.log(this.oldPassword)
      console.log(this.newPassword)
    }
  }
}

</script>
