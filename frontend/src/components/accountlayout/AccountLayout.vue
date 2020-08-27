<template>
  <div class="account">
    <main class="container">
      <div class="starter-template row">

        <div v-if="errorMessage" class="col-md-6 col-md-offset-3 alert alert-danger">
          <a href="#" class="close" data-dismiss="alert" @click="setErrorMessage('')">&times;</a>
          <strong>Error!</strong> {{ errorMessage }}
        </div>


        <div v-if="infoMessage" class="col-md-6 col-md-offset-3 alert alert-info">
          <a href="#" class="close" data-dismiss="alert" @click="setInfoMessage('')">&times;</a>
          <strong>Info!</strong> {{ infoMessage }}
        </div>

        <div v-if="successMessage" class="col-md-6 col-md-offset-3 alert alert-success">
          <a href="#" class="close" data-dismiss="alert" @click="setSuccessMessage('')">&times;</a>
          <strong>Success!</strong> {{ successMessage }}
        </div>

        <router-view
            @errorMessage="setErrorMessage"
            @infoMessage="setInfoMessage"
            @successMessage="setSuccessMessage"/>
      </div>
    </main>
  </div>
</template>

<script>
import $ from "jquery";
import zxcvbn from "zxcvbn";

export default {
  name: 'AccountLayout',
  components: {
  },
  data() {
    return {
      errorMessage: '',
      infoMessage: '',
      successMessage: '',
    }
  },
  methods: {
    setErrorMessage(message) {
      this.errorMessage = message
    },
    setInfoMessage(message) {
      this.infoMessage = message
    },
    setSuccessMessage(message) {
      this.successMessage = message
    }
  }
}

$(function() {
  let strength = {
    0: "Worst",
    1: "Bad",
    2: "Weak",
    3: "Good",
    4: "Strong"
  };

  let password = $('[data-pwd="true"]');
  let meter = $('#password-strength-meter');
  let msg = $('#password-strength-text');

  function showFeedback() {
    let val = this.value;
    let result = zxcvbn(val);

    // Update the password strength meter
    meter.val(result.score);

    // Update the text indicator
    if (val !== "") {
      msg.text("Strength: " + strength[result.score]);
    } else {
      msg.text("");
    }
  }

  password.change(showFeedback);
  password.keyup(showFeedback);
});

</script>

<style>

.account {
  background-color: #f5f7f9;
  width: 100%;
  height: 100%;
}

h1 {
  text-align: center;
  font-size: 30px;
}
.starter-template {
  padding: 40px 15px;
}

input, button {
  margin: 5px 0;
}

input:hover, input:focus {
  outline: 0;
  transition: all .5s linear;
  box-shadow: inset 0 0 10px #ccc;
}

fieldset {
  margin-top: 100px !important;
}
legend {
  font-family: 'Montserrat', sans-serif;
  text-align: center;
  font-size: 20px;
  padding: 15px;
}
a {
  cursor: pointer;
}

.provider {
  display: inline-block;
  width: 64px;
  height: 64px;
  border-radius: 4px;
  outline: none;
}
.facebook { background: #3B5998; }
.google { background: #D14836; }
.twitter { background: #00ACED; }
.yahoo { background: #731A8B; }
.xing { background: #006567; }
.vk { background: #567ca4; }

.social-providers,
.sign-in-now,
.already-member,
.not-a-member {
  text-align: center;
  margin-top: 20px;
}
.disclaimer-links {
  text-align: center;
  margin-top: 20px;
}

.clef-button-wrapper {
  width: 190px!important;
  height: 34px!important;
  margin: 20px auto 0 auto!important;
}

.user {
  margin-top: 50px;
}
.user .data {
  margin-top: 10px;
}

.form-control {
  border-radius: 0;
}

[class^='ion-'] {
  font-size: 1.2em;
}

.has-feedback .form-control-feedback {
  top: 0;
  left: 0;
  width: 46px;
  height: 46px;
  line-height: 46px;
  color: #555;
}

.has-feedback .form-control {
  padding-left: 42.5px;
}

.btn {
  font-weight: bold;
  border-radius: 2px;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, .26);
}

.btn-lg {
  font-size: 18px;
}

</style>
