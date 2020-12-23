<template>
  <div class="zeta">
    <v-app-bar app>
      <v-toolbar-title>
        <router-link to="/">
          <img class="v-navbar-logo" src="../../assets/zeta_logo.png" contain/>
        </router-link>
      </v-toolbar-title>
      <v-breadcrumbs v-if="gdslProject && $vuetify.breakpoint.smAndUp">
        <v-icon>mdi-chevron-right</v-icon>
        <v-breadcrumbs-item>
          <router-link tag="span" class="navbar-breadcrumb" :to="'/zeta/overview/' + gdslProject.id">
            {{ gdslProject.name }}
          </router-link>
        </v-breadcrumbs-item>
      </v-breadcrumbs>
      <v-spacer/>
      <v-toolbar-items class="hidden-xs-only">
        <v-menu offset-y v-if="user">
          <template v-slot:activator="{ on }">
            <v-btn text v-on="on">
              {{ user.firstName }} {{ user.lastName }}
              <v-icon>mdi-chevron-down</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item v-for="(item, index) in userMenu" :key="index" @click="item.method">
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-toolbar-items>
      <span class="hidden-sm-and-up">
        <v-app-bar-nav-icon @click="sidebar = !sidebar"/>
      </span>
    </v-app-bar>
    <v-navigation-drawer v-model="sidebar" app>
      <v-list>
        <v-list-item v-for="(item, index) in userMenu" :key="index" @click="item.method">
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-content style="height: 100%">
      <router-view/>
    </v-content>
  </div>
</template>

<script>
import {AUTH_LOGOUT} from "@/store/actions/auth";
import axios from 'axios'
import {EventBus} from "@/eventbus/eventbus";

export default {
  name: 'ZetaLayout',
  components: {},
  data() {
    return {
      user: {
        id: "123",
        firstName: "test",
        lastName: "test2",
        email: "test@test",
        activated: true
      },
      gdslProject: {
        id: "",
        name: "",
        concept: "",
        diagram: "",
        shape: "",
        style: "",
        validator: ""
      },
      userMenu: [
        {title: "Change Password", method: this.changePassword},
        {title: "Logout", method: this.logout}
      ],
      sidebar: false
    }
  },
  methods: {
    changePassword: function () {
      this.$router.push('/account/password/change')
    },
    logout: function () {
      this.$store.dispatch(AUTH_LOGOUT).then(() => {
        this.$router.push('/account/signIn')
      })
    }
  },
  mounted() {
    EventBus.$on('gdslProjectSelected', gdslProject => {
      this.gdslProject = gdslProject
    })
    EventBus.$on('gdslProjectUnselected', () => {
      this.gdslProject = null
    })


    axios.get("http://localhost:9000/overview", {withCredentials: true}).then(
        (response) => {
          if (response.data.csrf) this.logout()

          this.user = response.data.user;
        },
        (error) => console.log(error)
    )
  },
  created() {
    this.gdslProject = null
  }
}
</script>

<style>
.v-navbar-logo {
  max-width: 40px;
  max-height: 40px;
}
</style>