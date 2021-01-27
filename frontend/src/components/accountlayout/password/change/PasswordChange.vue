<template>
  <v-container class="col-md-4 col-md-offset-3">
    <v-form
        ref="form"
        @submit.prevent="validate"
        lazy-validation
    >
      <p class="font-weight-bold">
        Change password
      </p>
      <p>
        Strong passwords include numbers, letters and punctuation marks.
      </p>
      <v-text-field
          v-model="oldPassword"
          :append-icon="showOldPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showOldPassword ? 'text' : 'password'"
          @click:append="showOldPassword = !showOldPassword"
          :rules="oldPasswordRules"
          label="Old password"
          required
      ></v-text-field>
      <v-text-field
          v-model="newPassword"
          :append-icon="showNewPassword ? 'mdi-eye' : 'mdi-eye-off'"
          :type="showNewPassword ? 'text' : 'password'"
          @click:append="showNewPassword = !showNewPassword"
          :rules="newPasswordRules"
          label="New password"
          required
      ></v-text-field>
      <v-progress-linear
          :color="score.color"
          :value="score.value"
      ></v-progress-linear>
      <br></br>
      <v-btn
          type="submit"
          @submit="validate"
          block
          color="primary"
      >
        Change password
      </v-btn>
      <br></br>
      <p>
        Already a member?
        <router-link to="/account/signIn">Sign in now</router-link>
      </p>
      </v-form>
  </v-container>
</template>

<script>
import axios from 'axios'
import {AUTH_LOGOUT} from "@/store/actions/auth";
import {EventBus} from "@/eventbus/eventbus";
import zxcvbn from "zxcvbn";

export default {
  name: 'PasswordChange',
  components: {
  },
  data() {
    return {
      oldPassword: "",
      newPassword: "",
      newPasswordRules: [
      v => !!v || 'Password is required',
      v => zxcvbn(v).score >= 3 || 'Please choose a stronger password. Try a mix of letters, numbers, and symbols.',
      ],
      oldPasswordRules: [
        v => !!v || 'Password is required',
      ],
      showNewPassword: false,
      showOldPassword: false,
    }
  },
  methods: {
    change: function () {
      axios.post(
          'http://localhost:9000/password/change',
          {
            "current-password": this.oldPassword,
            "new-password": this.newPassword
          },
          {withCredentials: true}
      ).then(
          (response) => {
            EventBus.$emit('successMessage', "Password was successfully changed");
            this.$store.dispatch(AUTH_LOGOUT);
          },
          (error) => EventBus.$emit('errorMessage', error)
      )
    },
    validate () {
      if(this.$refs.form.validate())
        this.change()
    },
  },
  computed: {
    score() {
      const result = zxcvbn(this.newPassword);

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
