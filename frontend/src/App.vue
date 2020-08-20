<template>
  <div id="app">
    <nav class="navbar navbar-default" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-overview">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/">
            <img src="./assets/zeta_logo.png" class="navbar-logo">
          </a>
        </div>

        <div class="collapse navbar-collapse" id="navbar-collapse-overview">
          <ul class="nav navbar-nav">
            <li v-for="p in project" v-bind:key="p.id"><a class="navbar-breadcrumb" :href="'/overview/' + p.id">{{ p.name }}</a></li>
          </ul>

          <ul class="nav navbar-nav navbar-right">
            <li v-if="user" class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                <span>{{ user.fullName }}</span>
                <span class="caret"></span>
              </a>
              <ul class="dropdown-menu navbar-right" role="menu">
                <li><a href="@routes.ScalaRoutes.getPasswordChange()">Change Password</a></li>
                <li><a href="@routes.ScalaRoutes.getSignout">Logout</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <router-view />

  </div>
</template>

<script>

export default {
  name: 'App',
  components: {
    //Webpage
  },
  data() {
    return {
      user: {
        id: "123",
        firstName: "test",
        lastName: "test2",
        email: "test@test",
        activated: true,
        fullName: "test test2"
      },
      project: [{
        id: "520ec611-1dbd-4a93-bf6c-2b316cb67f0b",
        name: "testproject",
        concept: "Concept",
        diagram: "diagram",
        shape: "shape",
        style: "style",
        validator: "None"
      }]
    }
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
  }

  .navbar-default {
    background-color: #f5f5f5;
    border-color: #f5f5f5;
    -webkit-border-radius: 0;
    -moz-border-radius: 0;
    border-radius: 0;
    -webkit-box-shadow: 0 2px 3px rgba(0, 0, 0, 0.15);
    box-shadow: 0 2px 3px 0 rgba(0,0,0,0.16), 0 0 0 1px rgba(0,0,0,0.08);
  }

  .navbar-default .dropdown-menu {
    border: 1px solid transparent;
    -webkit-box-shadow: 0 2px 3px rgba(0, 0, 0, 0.15);
    box-shadow: 0 2px 3px rgba(0, 0, 0, 0.15);
  }

  .panel-default {
    border: none;
    -webkit-box-shadow: 0 2px 3px 0 rgba(0,0,0,0.16), 0 0 0 1px rgba(0,0,0,0.08);
    box-shadow: 0 2px 3px 0 rgba(0,0,0,0.16), 0 0 0 1px rgba(0,0,0,0.08);
  }

  .form-control {
    -webkit-box-shadow: none;
    box-shadow: none;
  }

  .list-group-item.active, .list-group-item.active:hover, .list-group-item.active:focus {
    background-color: #5bc0de;
    border-color: #46b8da;
  }

  .navbar-default {
    z-index: 999;
  }

  .navbar-default .navbar-nav > li > a {
    color: #444;
    font-weight: bold;
  }

  .navbar-default .navbar-nav > li:hover > a {
    color: #e30a5e;
  }

  .navbar-breadcrumb::before {
    content: "\E258";
    font-family: 'Glyphicons Halflings';
    position: absolute;
    left: -5px;
    font-size: 0.9em;
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

  .navbar-logo {
    max-height: 40px;
    margin-top: -11px;
  }

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
    font-size: 35px;
    padding: 20px 0 0 0;
    color: darkslategray;
    cursor: pointer;
  }

  /* hide 'upload project' button */
  #file{
    display: none;
  }

  /* Thumbnail */
  .thumbnail{
    width: 80px;
    height: 80px;
    padding: 2px;
    border: 2px solid lightgray;
    border-radius: 3px;
    float: left;
  }

  .size{
    font-size:12px;
  }
</style>
