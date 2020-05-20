<template>
    <g v-if="tag" class="vue-node-style-node uml-node">
        <svg width="5" height="10">
            <line fill="white"  width="100%" height="100%" />
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
        props: ['tag','layout'],
        created(){
            console.log("created" + this.tag)
        }
    }
</script>

<style scoped>

</style>
