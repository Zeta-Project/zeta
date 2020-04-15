<template>
    <g v-if="tag" class="vue-node-style-node uml-node">
        <rect fill="#FFFFFF" stroke="#C0C0C0" width="285" height="100"></rect>
        <g :style="{ fontSize: zoom >= 1 ? '10px' : '15px', fontFamily: 'Roboto,sans-serif', fontWeight: 300, backgroundColor: tag.abstractness && 'red',  fill: '#444' }">
            <text :transform="zoom >= 1 ? 'translate(100 25)' : 'translate(75 40)'" :style="{ fontSize: zoom >= 1 ? '16px' : '26px', fill: '#336699' }">{{tag.name}}</text>
            <!--<text :transform="zoom >= 1 ? 'translate(100 57)' : 'translate(75 90)'" style="text-transform: uppercase; font-weight: 400">{{positionSecondLine}}</text>
            <text v-show="zoom >= 1" transform="translate(100 72)">{{tag.email}}</text>
            <text v-show="zoom >= 1" transform="translate(100 88)">{{tag.phone}}</text>
            <text v-show="zoom >= 1" transform="translate(170 88)">{{tag.fax}}</text>
            <text :transform="zoom >= 1 ? 'translate(100 50)' : 'translate(75 65)'" style="font-weight: 400">Attributes:</text>
            <text
                    v-for="(attribute, index) in tag.attributes"
                    :key="`node-${tag.name}-attribute-property-${index + 1}`"
                    :transform="zoom >= 1 ? `translate(${100} ${(index + 2)*25})` : `translate(${75} ${(index + 2)* 40})`"
                    style="font-weight: 200"
            >
                {{attribute.name}}
            </text>
            <text :transform="zoom >= 1 ? 'translate(100 100)' : 'translate(75 100)'" style="font-weight: 400">Operations:</text>
            <text
                    v-for="(method, index) in tag.methods"
                    :key="`node-${tag.name}-operation-property-${index + 1}`"
                    :transform="zoom >= 1 ? `translate(${100} ${(index + 2)*25+50})` : `translate(${75} ${(index + 2)* 40+50})`"
                    style="font-weight: 200"
            >
                {{method.name}}
            </text>-->
        </g>
    </g>
</template>

<script>
    const statusColors = {
        present: '#55B757',
        busy: '#E7527C',
        travel: '#9945E9',
        unavailable: '#8D8F91'
    }

    export default {
        name: 'node',
        data: function () {
            return {
                zoom: 1,
                focused: false
            }
        },
        // the node tag is passed as a prop
        props: ['tag'],
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
            }
        }
    }
</script>

<style scoped>

</style>
