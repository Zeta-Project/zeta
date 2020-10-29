<template>
    <div class="demo-content">
      <GraphEditor ref="GraphComponent" v-on:on-toggle-dnd="toggleDnd"></GraphEditor>
    </div>
</template>

<script>
import GraphEditor from './graphical-editor/components/graphEditor/GraphEditor'
import store from '../../../store/index'


function refreshGsdlProject(routeTo, next) {
  store.dispatch('refreshGraph',routeTo.params.id      )
      .then(() => {
        next()
      })
}

export default {
  name: 'MetamodelGraphicalEditor',
  components: {
    GraphEditor,
  },
  data: function () {
    return {
      isDndExpanded: false
    }
  },
  methods: {
    toggleDnd(isDndExpanded) {
      this.isDndExpanded = isDndExpanded;
    }
  },
  beforeRouteEnter(routeTo, routeFrom, next) {
    refreshGsdlProject(routeTo, next)
  },
  beforeRouteUpdate(routeTo, routeFrom, next) {
    refreshGsdlProject(routeTo, next)
  },
}
</script>

<style>
  html,
  body {
    position: fixed;
    margin: 0;
    padding: 0;
    width: 100%;
    height: 100%;
    background-color: white;
    touch-action: none;
    overscroll-behavior-y: contain;
    overflow: hidden;
    font-family: Tahoma, Verdana, sans-serif;
    font-size: 12px;
    color: #333333;
  }
</style>
<style scoped>
  .demo-content {
  }

  .demo-header {
    position: absolute;
    top: 0;
    height: 60px;
    left: 0;
    right: 0;
    background-color: #336699;
    color: white;
  }

  .demo-y-logo {
    width: 60px;
    height: 60px;
    display: inline-block;
    padding: 5px;
    box-sizing: border-box;
    border-color: transparent;
  }

  .demo-header a {
    display: inline-block;
    height: 60px;
    text-decoration: none;
    color: white;
    line-height: 60px;
    padding: 0 5px;
    vertical-align: top;
    font-size: 1.5em;
  }

  .demo-title {
    display: inline-block;
    padding: 0 5px 0 30px;
    height: 60px;
    line-height: 60px;
    color: white;
    cursor: default;
    font-size: 1.5em;
    letter-spacing: 1px;
    background-size: 20px 20px;
    vertical-align: top;
  }

  @media screen and (max-height: 500px) {
    .demo-header {
      height: 30px;
      font-size: 10px;
    }

    .toolbar {
      top: 30px;
      height: 30px;
      line-height: 30px;
    }

    .demo-header .demo-y-logo {
      height: 30px;
      line-height: 30px;
    }

    .demo-header a {
      height: 30px;
      line-height: 30px;
    }

    .demo-title {
      height: 30px;
      line-height: 30px;
    }
  }
</style>
