<template>
  <div class="account">
    <main class="container">
      <div class="starter-template row">
        <router-view/>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'AccountLayout',
  components: {
  }
}
</script>

<style>
.account {
  background-color: #f5f7f9;
  width: 100%;
  height: 100%;
}

.starter-template {
  padding: 40px 15px;
}
</style>
