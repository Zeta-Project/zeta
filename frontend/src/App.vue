<template>
  <v-app>
    <div class="alerts">
      <transition name="fade">
        <v-alert dense type="error" v-if="errorMessage" dismissible class="col-md-6 offset-md-3">
          <strong>Error!</strong> {{ errorMessage }}
        </v-alert>
      </transition>
      <transition name="fade">
        <v-alert dense type="info" v-if="infoMessage" dismissible class="col-md-6 offset-md-3">
          <strong>Info!</strong> {{ infoMessage }}
        </v-alert>
      </transition>
      <transition name="fade">
        <v-alert dense type="success" v-if="successMessage" dismissible class="col-md-6 offset-md-3">
          <strong>Success!</strong> {{ successMessage }}
        </v-alert>
      </transition>
    </div>

    <v-main>
      <v-container fluid>
        <router-view/>
      </v-container>
    </v-main>

  </v-app>
</template>

<script>
import axios from "axios"
import {AUTH_LOGOUT} from "@/store/actions/auth";
import {EventBus} from "@/eventbus/eventbus";

export default {
  name: 'App',
  components: {},
  data() {
    return {
      errorMessage: '',
      infoMessage: '',
      successMessage: '',
      timer: undefined
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
    },
    resetTimer() {
      if (this.timer) clearTimeout(this.timer);
    }
  },
  created: function () {
    EventBus.$on('errorMessage', message => {
      this.setErrorMessage(message)
      this.resetTimer()
      this.timer = setTimeout(() => {
        this.setErrorMessage('')
      }, 5000);
    })
    EventBus.$on('infoMessage', message => {
      this.setInfoMessage(message)
      this.resetTimer()
      this.timer = setTimeout(() => {
        this.setInfoMessage('')
      }, 5000);
    })
    EventBus.$on('successMessage', message => {
      this.setSuccessMessage(message)
      this.resetTimer()
      this.timer = setTimeout(() => {
        this.setSuccessMessage('')
      }, 5000);
    })

    axios.interceptors.response.use(response => {
      if (
          response.request.responseURL !== response.config.url &&
          response.request.responseURL.endsWith("/signIn")
      ) {
        localStorage.removeItem("user-token");
        console.log(this.$router)
        this.$router.push('/account/signIn')

      }
      return response
    }, err => {
      return new Promise(function (resolve, reject) {
        if (err.status === 401 && err.config && !err.config.__isRetryRequest) {
          // if you ever get an unauthorized, logout the user
          this.$store.dispatch(AUTH_LOGOUT)
          // you can also redirect to /login if needed !
        }
        throw err;
      });
    });
  }
}
</script>

<style>

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0px;
  width: 100%;
  height: 100%;
}

.alerts {
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
  z-index: 999999;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s ease-out;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}

.modal-dialog {
  margin: 100px auto !important;
}

</style>
