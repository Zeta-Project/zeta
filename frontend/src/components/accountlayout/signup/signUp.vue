<template>
  <v-container class="col-md-4 col-md-offset-3">
    <v-form
        ref="form"
        lazy-validation
    >
      <p class="font-weight-bold">
        Sign up for a new account
      </p>
      <v-text-field
          v-model="firstName"
          :rules="nameRules"
          label="First name"
          required
      ></v-text-field>
      <v-text-field
          v-model="lastName"
          :rules="nameRules"
          label="Last name"
          required
      ></v-text-field>
      <v-text-field
          v-model="email"
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
      <v-progress-linear
          :color="score.color"
          :value="score.value"
      ></v-progress-linear>
      <br></br>
      <v-btn
          class="mr-4"
          @click="validate"
          block
          color="primary"
      >
        Sign Up
      </v-btn>
    </v-form>
  <br></br>
    <p>
      Already a member?
      <router-link to="/account/signIn">Sign in now</router-link>
    </p>
  </v-container>
</template>

<script>
import axios from 'axios'
import {EventBus} from "@/eventbus/eventbus";
import zxcvbn from "zxcvbn";

export default {
  name: 'SignUp',
  components: {},
  data() {
    return {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      nameRules: [
        v => !!v || 'Name is required'
      ],
      emailRules: [
        v => !!v || 'Email is required',
        v => !v || /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'E-mail must be valid'
      ],
      passwordRules: [
        v => !!v || 'Password is required',
        v => zxcvbn(v).score >= 3 || 'Please choose a stronger password. Try a mix of letters, numbers, and symbols.',
      ],
      showPassword: false,
    }
  },
  methods: {
    register: function () {
      const info =
          " You're almost done! We sent an activation mail to " +
          this.email +
          " Please follow the instructions in the email to activate your account." +
          "If it doesn't arrive, check your spam folder, or try to log in again to send another activation mail."

      axios.post(
          "http://localhost:9000/signUp",
          {
            firstName: this.firstName,
            lastName: this.lastName,
            email: this.email,
            password: this.password
          },
          {withCredentials: true}
      ).then(
          (response) => EventBus.$emit("successMessage", info),
          (error) => EventBus.$emit('errorMessage', error)
      )
    },
    validate () {
      if(this.$refs.form.validate())
        this.register()
    },
  },
  computed: {
    score() {
      const result = zxcvbn(this.password);

      switch (result.score) {
        case 4:
          return {
            color: "light-blue",
            value: 100
          };
        case 3:
          return {
            color: "light-green",
            value: 75
          };
        case 2:
          return {
            color: "yellow",
            value: 50
          };
        case 1:
          return {
            color: "orange",
            value: 25
          };
        default:
          return {
            color: "red",
            value: 0
          };
      }
    }
  }
}
</script>
