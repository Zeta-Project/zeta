<template>
    <g v-if="tag" class="vue-edge-style-edge uml-edge">
        <svg width="100%" height="100%">
            <path :d="path" fill="none" stroke-width="5" stroke-linejoin="round" stroke="black"></path>
<!--            <line stroke="black" stroke-width="2" :x1="tag.source.layout.x" :y1="tag.source.layout.y" :x2="tag.target.layout.x" :y2="tag.target.layout.y"/>-->
<!--            <line stroke="black" stroke-width="2"/>-->
        </svg>
    </g>
</template>

<script>
    import {DashStyle, ArrowType} from 'yfiles'

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
        props: ['tag','layout', 'cache'],
        computed: {
            path: function() {
                console.log(this.cache.path)
                const p =  this.cache.path.createSvgPath()

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
                console.log(p)
                return p.getAttribute('d')
            }
        }
    }
</script>

<style scoped>

</style>
