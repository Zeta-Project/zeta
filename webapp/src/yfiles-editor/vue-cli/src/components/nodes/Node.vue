<template>
    <g v-if="tag" class="vue-node-style-node uml-node">
        <svg :width="layout.width" :height="layout.height">
            <rect fill="white"  width="100%" height="100%" />

            <g :style="{ fontSize: zoom >= 1 ? '10px' : '15px', fontFamily: 'Roboto,sans-serif', fontWeight: 300, backgroundColor: tag.abstractness && 'red',  fill: 'rgb(147, 176, 255)' }" >           
                <rect x="0%" y="0%" width="100%" :height="40" />
                <text x="50%" y="25" :style="{ fontSize:'16px', fill: 'black' }" text-anchor="middle" >{{tag.name}}</text>
                
                <g>
                    <rect x="o" y="50" width="100%" :height=" 30 " />
                    <g>
                        <image v-if="attributes_open==false" x="" y="50" v-on:click="attributes_open=change_status(attributes_open)" xlink:href="../../assets/triangle.svg" />
                        <image v-if="attributes_open==true"  x="" y="50" v-on:click="attributes_open=change_status(attributes_open)" xlink:href="../../assets/triangle_90deg_rotated.svg" />
                    </g>
                    <text x="20" y="70":style="{ fontSize:'16px', fill: 'black' }" >Attributes:</text>

                    <g v-if="attributes_open">
                        <text x="20" y="50" v-for="(attribute, index) in tag.attributes" :key="attribute.name" 
                            :style="{ fontSize:'16px', fill: 'black' }"
                            :transform="zoom >= 1 ? `translate(${0} ${(index + 2)*25})` : `translate(${0} ${(index + 2)* 40})`">
                            {{attribute.name}}
                        </text>
                    </g>
                </g>

                <g>
                    <rect x="1" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" width="100%" :height="30" />
                
                    <g v-on:click="operations_open=change_status(operations_open)">
                        <image v-if="operations_open==false" x="" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" xlink:href="../../assets/triangle.svg" />
                        <image v-if="operations_open==true"  x="" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" xlink:href="../../assets/triangle_90deg_rotated.svg" />
                    </g>
                    <text x="20" :y="102 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" :style="{  fontSize: '16px', fill: 'black' }" >Operation:</text>
                    <g v-if="operations_open">
                        <text x="20" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" v-for="(method, index) in tag.methods" :key="method.name" 
                            :style="{fontSize: '16px', fill: 'black' }"
                            :transform="zoom >= 1 ? `translate(${0} ${(index + 2)*25})` : `translate(${0} ${(index + 2)* 40})`">
                            {{method.name}}
                            {{style}}
                        </text>
                    </g>
                </g>
            </g>

            <rect fill="none" x="0" y="0" stroke="black" stroke-width="3" width="100%" height="100%" />

        </svg>
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
                width : 150,
                zoom: 1,
                focused: false,
                offset: 97,
                }
        },
        // the node tag is passed as a prop
        props: ['tag','layout'],

        created(){
            console.log("created" + this.tag)
        },

    watch: {
        tag: function () {
           this.tag
        },

    },
        //console.log(tag.attributes),
        methods: {
            change_status(status) {
                if (status) {
                    return false;
                } 
                return true;
            },
            size_element(element){

            },

            total_size(tag) {

            }
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
