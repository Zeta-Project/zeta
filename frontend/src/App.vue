<template>
  <div id="app">

      <div class="alerts">
        <transition name="fade">
          <div v-if="errorMessage" class="col-md-6 col-md-offset-3 alert alert-danger">
            <a href="#" class="close" @click="setErrorMessage('')">&times;</a>
            <strong>Error!</strong> {{ errorMessage }}
          </div>
        </transition>
        <transition name="fade">
          <div v-if="infoMessage" class="col-md-6 col-md-offset-3 alert alert-info">
            <a href="#" class="close" @click="setInfoMessage('')">&times;</a>
            <strong>Info!</strong> {{ infoMessage }}
          </div>
        </transition>
        <transition name="fade">
          <div v-if="successMessage" class="col-md-6 col-md-offset-3 alert alert-success">
            <a href="#" class="close" @click="setSuccessMessage('')">&times;</a>
            <strong>Success!</strong> {{ successMessage }}
          </div>
        </transition>
      </div>

    <router-view />

  </div>
</template>

<script>
import axios from "axios"
import {AUTH_LOGOUT} from "@/store/actions/auth";
import {EventBus} from "@/eventbus/eventbus";
export default {
  name: 'App',
  components: {
  },
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
      if(this.timer) clearTimeout(this.timer);
    }
  },
  created: function () {
    EventBus.$on('errorMessage', message => {
      this.setErrorMessage(message)
      this.resetTimer()
      this.timer = setTimeout(()=>{ this.setErrorMessage('') }, 5000);
    })
    EventBus.$on('infoMessage', message => {
      this.setInfoMessage(message)
      this.resetTimer()
      this.timer = setTimeout(()=>{ this.setInfoMessage('') }, 5000);
    })
    EventBus.$on('successMessage', message => {
      this.setSuccessMessage(message)
      this.resetTimer()
      this.timer = setTimeout(()=>{ this.setSuccessMessage('') }, 5000);
    })

    axios.interceptors.response.use(function (response) {
      if(
          response.request.responseURL !== response.config.url &&
          response.request.responseURL.endsWith("/signIn")
      ) {
        localStorage.removeItem("user-token");
        this.$router.push('/account/signIn')
      }
      return response
    }, function (err) {
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
