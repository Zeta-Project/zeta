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
    <v-content>
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


.zeta {
  background-color: white;
  width: 100%;
  height: 100%;
}

.panel-default {
  border: none;
  -webkit-box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.16), 0 0 0 1px rgba(0, 0, 0, 0.08);
  box-shadow: 0 2px 3px 0 rgba(0, 0, 0, 0.16), 0 0 0 1px rgba(0, 0, 0, 0.08);
}

.form-control {
  -webkit-box-shadow: none;
  box-shadow: none;
}

.list-group-item.active, .list-group-item.active:hover, .list-group-item.active:focus {
  background-color: #5bc0de;
  border-color: #46b8da;
}

.modal-header.modal-header-primary {
  color: #fff;
  background-color: #337ab7;
  border-radius: 5px 5px 0 0;
}

.modal-header.modal-header-info {
  color: #fff;
  background-color: #5bc0de;
  border-radius: 6px 6px 0 0;
}

.modal-header.modal-header-info .close:hover,
.modal-header.modal-header-info .close,
.modal-header.modal-header-primary .close:hover,
.modal-header.modal-header-primary .close {
  color: #fff;
  opacity: 0.8;
}

/* end bootstrap theme corrects */

.bottom-link {
  position: absolute;
  bottom: 15px;
}

.bottom-link.right {
  right: 15px;
}

.tile {
  width: 12em;
  height: 10em;
  margin-top: 1em;
  margin-right: 1em;
  position: relative;
}

.tileBorder {
  width: 100%;
  height: 100%;
  position: absolute;
  border: 0.1em solid #ededed;
}

.backgroundImage {
  width: 100%;
  height: 100%;
  position: absolute;
  z-index: 0;
}

.transparentBackground {
  width: 100%;
  height: 20%;
  position: absolute;
  margin-top: 67%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.tileBorder:hover .transparentBackground {
  background-color: rgba(0, 100, 255, 0.7);
}

.linkText {
  width: 100%;
  height: 100%;
  position: absolute;
  color: white;
  padding-top: 69%;
  padding-left: 5%;
  overflow: hidden;
  text-overflow: ellipsis;
  z-index: 2;
}

.ecoreLink {
  width: 100%;
  height: 100%;
  position: absolute;
  z-index: 3;
}

.btn-file {
  position: relative;
  overflow: hidden;
}

.btn-file input[type=file] {
  position: absolute;
  top: 0;
  right: 0;
  min-width: 100%;
  min-height: 100%;
  font-size: 100px;
  text-align: right;
  filter: alpha(opacity=0);
  opacity: 0;
  outline: none;
  background: white;
  cursor: inherit;
  display: block;
}

.btn-uuid {
  min-width: 21em;
}

paper-button {
  font-size: 14px;
  width: 150px;
  background: darkgray;
  color: black;
}

paper-button:hover {
  background: lightblue;
}

paper-button::shadow #ripple {
  color: #2a56c6;
}

.delete-list-item {
  float: right;
  z-index: 2;
  display: none;
  padding: 5px;
  position: relative;
}

.list-item-container:hover > .delete-list-item {
  display: block;
}

.validate-list-item {
  float: right;
  z-index: 2;
  display: none;
  padding: 5px;
  position: relative;
}

.list-item-container:hover > .validate-list-item {
  display: block;
}

.overlay-container {
  position: relative;
}

.overlay {
  z-index: 10;
  height: 100%;
  width: 100%;
  background-color: white;
  opacity: 0.85;
  display: flex;
  border-radius: 4px;
  position: absolute;
}

.overlay:before {
  font-family: 'Glyphicons Halflings';
  content: "\E033";
  position: relative;
  display: flex;
  font-style: normal;
  font-weight: normal;
  align-items: center;
  width: 100%;
  justify-content: center;
  font-size: 40px;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.no-margin {
  margin: 0;
}

#edit-project .panel-body:hover {
  background-color: #337ab7;
  color: #fff;
}

.alert {
  margin-top: 10px;
}

.validatorDropdown {
  display: inline-block;
}

/* drag & drop area */
.upload-area {
  height: 180px;
  border: 1px dashed #ccc;
  border-radius: 3px;
  text-align: center;
  overflow: auto;
  padding: 20px 0 0 0;
  color: darkslategray;
  cursor: pointer;
}

/* Thumbnail */
.thumbnail {
  width: 80px;
  height: 80px;
  padding: 2px;
  border: 2px solid lightgray;
  border-radius: 3px;
  float: left;
}

.size {
  font-size: 12px;
}
</style>