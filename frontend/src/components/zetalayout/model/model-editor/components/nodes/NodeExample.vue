<template>
  <g v-if="tag" class="vue-node-style-node uml-node">
    <svg :width="layout.width" :height="layout.height">
      <rect fill="white"  width="100%" height="100%" />

      <g :style="{ fontSize: '10px', fontColor: 'white', fontFamily: 'Roboto,sans-serif', fontWeight: 300, fill: (tag.abstractness ? 'red' :'orange' )}" >
        <rect x="0%" y="0%" width="100%" :height="45" />
        <text x="50%" y="25" :style="{ fontSize:'16px', fill: 'black' }" text-anchor="middle" >{{tag.name}}</text>


        <g>
          <rect x="1" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" width="100%" :height="30" />
          <g v-on:click="operations_open=change_status(operations_open)">
            <image v-if="!operations_open" x="0" :y="82 + 30" xlink:href="../../assets/triangle.svg" />
            <image v-if="operations_open"  x="0" :y="82 + 30" xlink:href="../../assets/triangle_90deg_rotated.svg" />
          </g>
          <text x="20" :y="102 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" :style="{  fontSize: '16px', fill: 'black' }" >Properties:</text>
          <image :x="layout.width - 22" :y="87 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" width="18" xlink:href="../../assets/add-sign.svg" v-on:click="()=>methods.addOperationToNode(tag, 'default')"/>
          <g v-if="operations_open">
            <g v-for="(method, index) in tag.methods" :key="method.name" >
              <foreignObject x="20" :y="68 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)"
                             :transform="`translate(${0} ${(index + 2)*25})`"
                             width="100%" height=" 50 ">
                <VueInlineTextEditor
                    @change-input-mode="(newInputMode) => methods.changeInputMode(newInputMode)"
                    :inputMode="inputMode"
                    :value.sync="method.name"  />
              </foreignObject>
              <image :x="layout.width - 22"
                     :y="75 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)"
                     :transform="`translate(${0} ${(index + 2)*25})`"
                     width="18"
                     xlink:href="../../assets/delete-sign.svg"
                     v-on:click="()=>methods.deleteOperationFromNode(tag, method.name)"/>
            </g>
          </g>
        </g>
      </g>

      <rect fill="none" x="0" y="0" stroke="black" stroke-width="3" width="100%" height="100%" />
    </svg>
  </g>



</template>

<script>

import VueInlineTextEditor from "./VueInlineTextEditor.vue";
import {changeInputMode} from "../../model/nodes/styles/VuejsNodeStyle"

const statusColors = {
  present: '#55B757',
  busy: '#E7527C',
  travel: '#9945E9',
  unavailable: '#8D8F91'
}

export default {
  name: 'node',
  components: {
    VueInlineTextEditor,
  },
  data: function () {
    return {
      attributes_open : false ,
      operations_open : false ,
      width : 150,
      zoom: 1,
      focused: false,
      offset: 97,
    }
  },
  // the node tag is passed as a prop
  props: ['tag','layout','node','methods','inputMode'],

  created(){
  },

  watch: {
    tag: function () {
      this.tag
    },

  },

  methods: {
    change_status(status) {
      if (status) {
        return false;
      }
      return true;
    },
  },


  computed: {
    statusColor() {
      return statusColors[this.tag.status]
    },
    positionFirstLine() {
      const words = this.tag.position ? this.tag.position.split(' ') : []
      while (words.join(' ').length > 20) {
        words.pop()
      }
      return words.join(' ')
    },
    positionSecondLine() {
      const words = this.tag.position ? this.tag.position.split(' ') : []
      const secondLine = []
      while (words.join(' ').length > 20) {
        secondLine.unshift(words.pop())
      }
      return secondLine.join(' ')
    },
  }
}
</script>

<style scoped>

</style>
