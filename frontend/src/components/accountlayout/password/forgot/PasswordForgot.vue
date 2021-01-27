<template>
  <v-container class="col-md-4 col-md-offset-3">
    <div>
      <v-form
          ref="form"
          @submit.prevent="validate"
          lazy-validation
      >
        <p class="font-weight-bold">
          Forgot password
        </p>
        <p>
          Please enter your email address and we will send you an email
          with further instructions to reset your password.
        </p>
        <v-text-field
            v-model="email"
            :rules="emailRules"
            label="Email"
            required
        ></v-text-field>
        <v-btn
            type="submit"
            @submit="validate"
            block
            color="primary"
        >
          Sign Up
        </v-btn>
      </v-form>
    </div>
  </v-container>
</template>

<script>
export default {
  name: 'PasswordForgot',
  components: {
  },
  data() {
    return {
      email: "",
      emailRules: [
        v => !!v || 'Email is required',
        v => !v || /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
      ],
    }
  },
  methods: {
    forgot: function () {
      console.log(this.email)
    },
    validate () {
      if(this.$refs.form.validate())
        this.forgot()
    }
  }
}
</script>