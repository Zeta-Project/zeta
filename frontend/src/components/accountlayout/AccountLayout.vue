<template>
  <div>
    <div class="starter-template row">
      @request.flash.get("error").map { msg =>
      <div class="col-md-6 col-md-offset-3 alert alert-danger">
        <a href="#" class="close" data-dismiss="alert">&times;</a>
        <strong>@messages("error")</strong> @msg
      </div>
      }
      @request.flash.get("info").map { msg =>
      <div class="col-md-6 col-md-offset-3 alert alert-info">
        <a href="#" class="close" data-dismiss="alert">&times;</a>
        <strong>@messages("info")</strong> @msg
      </div>
      }
      @request.flash.get("success").map { msg =>
      <div class="col-md-6 col-md-offset-3 alert alert-success">
        <a href="#" class="close" data-dismiss="alert">&times;</a>
        <strong>@messages("success")</strong> @msg
      </div>
      }
      <router-view @messageEvent="setMessage"></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AccountLayout',
  components: {
  },
  data() {
    return {
      message: ''
    }
  },
  methods: {
    setMessage(message) {
      this.message = message
    }
  }
}
</script>
