<template>
  <fieldset class="col-md-6 col-md-offset-3">
    <div>
      <form @submit.prevent="register">
        <legend>Sign up for a new account</legend>
        <div class="form-group">
          <input required v-model="firstName" type="text" placeholder="First name" class="form-control input-lg"/>
        </div>
        <div class="form-group">
          <input required v-model="lastName" type="text" placeholder="Last name"
                 class="form-control input-lg"/>
        </div>
        <div class="form-group">
          <input required v-model="email" type="email" placeholder="Email"
                 class="form-control input-lg"/>
        </div>

        <section>
          <div class="form-group">
            <input required v-model="password" type="password" placeholder="Password"
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
import axios from 'axios'
import {EventBus} from "@/eventbus/eventbus";

export default {
  name: 'SignUp',
  components: {},
  data() {
    return {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
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
    }
  }
}

</script>

<style>
meter {
  /* Reset the default appearance */
  -moz-appearance: none;
  appearance: none;

  margin: 1em auto 1em;
  width: 100%;
  height: .5em;

  /* Applicable only to Firefox */
  background: none;
  background-color: rgba(0, 0, 0, 0.1);
}

meter::-webkit-meter-bar {
  background: none;
  background-color: rgba(0, 0, 0, 0.1);
}

meter[value="0"]::-webkit-meter-optimum-value,
meter[value="1"]::-webkit-meter-optimum-value {
  background: red;
}

meter[value="2"]::-webkit-meter-optimum-value {
  background: orange;
}

meter[value="3"]::-webkit-meter-optimum-value {
  background: yellow;
}

meter[value="4"]::-webkit-meter-optimum-value {
  background: green;
}

meter::-webkit-meter-even-less-good-value {
  background: red;
}

meter::-webkit-meter-suboptimum-value {
  background: orange;
}

meter::-webkit-meter-optimum-value {
  background: green;
}

meter[value="1"]::-moz-meter-bar,
meter[value="1"]::-moz-meter-bar {
  background: red;
}

meter[value="2"]::-moz-meter-bar {
  background: orange;
}

meter[value="3"]::-moz-meter-bar {
  background: yellow;
}

meter[value="4"]::-moz-meter-bar {
  background: green;
}

meter::-webkit-meter-optimum-value {
  transition: width .4s ease-out;
}
</style>
