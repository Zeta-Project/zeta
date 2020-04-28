<template>
    <g v-if="tag" class="vue-node-style-node uml-node">
        <rect fill="#FFFFFF" x="-1" y="-1" stroke="black" width="202" :height="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)"/>

        <g :style="{ fontSize: zoom >= 1 ? '10px' : '15px', fontFamily: 'Roboto,sans-serif', fontWeight: 300, backgroundColor: tag.abstractness && 'red',  fill: 'rgb(147, 176, 255)' }" >           
            <rect x="0" y="0" width="200" :height="30" />
            <text x="8" y="20" :style="{ fontSize: zoom >= 1 ? '16px' : '26px', fill: 'black' }" >{{tag.name}}</text>
            
            <rect x="0" y="50" width="200" :height=" 30 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" />
            <polygon v-on:click="attributes_open =  change_status(attributes_open)" points="8,57 8,69 16,63" style="fill:black"  :transform="attributes_open ? 'rotate(90,12,63)' : 'rotate(0)' "/>
            <text x="20" y="70" :style="{ fontSize: zoom >= 1 ? '16px' : '26px', fill: 'black' }" >Attributes:</text>
            <g v-if="attributes_open">
                <text x="20" y="50" v-for="(attribute, index) in tag.attributes" :key="attribute.name" 
                    :style="{ fontSize: zoom >= 1 ? '16px' : '26px', fill: 'black' }"
                    :transform="zoom >= 1 ? `translate(${0} ${(index + 2)*25})` : `translate(${0} ${(index + 2)* 40})`">
                    {{attribute.name}}
                </text>
            </g>

            <!--rect x="0" y="50" width="200" :height=" 30 + 30 * (operations_open ? Object.keys(tag.Operations).length : 0)" />
            <polygon v-on:click="operations_open =  change_status(operations_open)" points="8,57 8,69 16,63" style="fill:black"  :transform="operations_open ? 'rotate(90,12,63)' : 'rotate(0)' "/>
            <text x="20" y="70" :style="{ fontSize: zoom >= 1 ? '16px' : '26px', fill: 'black' }" >Operations::</text>
            <g v-if="operations_open">
                <text x="20" y="50" v-for="(Operations, index) in tag.operation" :key="Operations.name" 
                    :style="{ fontSize: zoom >= 1 ? '16px' : '26px', fill: 'black' }"
                    :transform="zoom >= 1 ? `translate(${0} ${(index + 2)*25})` : `translate(${0} ${(index + 2)* 40})`">
                    {{Operations.name}}
                </text>
            </g>
            
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
                attributes_open : false ,
                operations_open : false ,
                zoom: 1,
                focused: false,
                }
        },
        // the node tag is passed as a prop
        props: ['tag'],

        created(){
            console.log("created" + this.tag)
        },

        watch: {
            //counter: function (val) {
            //console.log("counter: " + counter )
            //}
        },

        //console.log(tag.attributes),
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
