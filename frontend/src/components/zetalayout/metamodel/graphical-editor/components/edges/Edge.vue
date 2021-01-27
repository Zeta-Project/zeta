<template>
  <g v-if="tag" class="vue-edge-style-edge uml-edge">
    <path :d="path" fill="none" stroke-width="2" stroke-linejoin="round" stroke="black"/>
    <image :x="arrowPosition[0]-15" :y="arrowPosition[1]-15" xlink:href="../../assets/association-arrow.svg"
         :transform="`rotate(${arrowRotation},${arrowPosition[0]},${arrowPosition[1]})`"/>

    <!--

    ###############
    this has to be connected to the actual arrow type
    ###############

    <image :x="arrowPosition[0]-25" :y="arrowPosition[1]-15" xlink:href="../../assets/composition-arrow.svg" :transform="`rotate(${arrowRotation},${arrowPosition[0]},${arrowPosition[1]})`" />
    <image :x="arrowPosition[0]-25" :y="arrowPosition[1]-15" xlink:href="../../assets/aggregation-arrow.svg" :transform="`rotate(${arrowRotation},${arrowPosition[0]},${arrowPosition[1]})`" />
    -->

  </g>
</template>

<script>
import {DashStyle, ArrowType, ILabel} from 'yfiles'

export default {
  name: 'edge',
  data: function () {
    return {
      smoothing: 0,
      dashStyle: DashStyle.SOLID,
      sourceArrow: ArrowType.NONE,
      targetArrow: ArrowType.DEFAULT,
      thickness: 1,
      sourceArrowScale: 1,
      targetArrowScale: 1
    }
  },
  props: ['tag', 'layout', 'cache'],
  computed: {
    path: function () {
      //console.log(this.cache.path)
      const p = this.cache.path.createSvgPath()

      p.setAttribute('fill', 'none')
      p.setAttribute('stroke-width', '3')
      p.setAttribute('stroke-linejoin', 'round')

      if (this.cache.selected) {
        // Fill for selected state
        // LinearGradient.applyToElement(context, path)
        p.setAttribute('stroke', this.cache.color)
      } else {
        // Fill for non-selected state
        p.setAttribute('stroke', this.cache.color)
      }

      // add the arrows to the container
      // super.addArrows(context, container, edge, cache.path, cache.arrows, cache.arrows)
      //console.log(p.getAttribute('d'))
      return p.getAttribute('d')
    },
    arrowRotation: function () {
      const pathSVGArray = this.path.split(" ");
      const lastPoint = pathSVGArray[pathSVGArray.length - 1].split(",").map(x => +x);
      const penultimatePoint = pathSVGArray[pathSVGArray.length - 3].split(",").map(x => +x);
      let rotation = 0;
      if (lastPoint[1] < penultimatePoint[1]) {
        //arrow up
        rotation = 270
      } else if (lastPoint[0] < penultimatePoint[0]) {
        //arrow right
        rotation = 180
      } else if (lastPoint[1] > penultimatePoint[1]) {
        //arrow down
        rotation = 90
      }
      //console.log(penultimatePoint)
      //console.log(lastPoint)
      //console.log(0 == rotation.localeCompare('down'))
      return rotation //should return the actual transfromation
    },

    arrowPosition: function () {
      const p = this.cache.path.createSvgPath()
      const pathSVG = p.getAttribute('d');
      const pathSVGArray = pathSVG.split(" ");
      const lastPoint = pathSVGArray[pathSVGArray.length - 1].split(",").map(x => +x);

      return lastPoint
    }
  }
}
</script>

<style scoped>

</style>
