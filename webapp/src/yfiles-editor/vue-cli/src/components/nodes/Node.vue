<template>
    <g v-if="tag" class="vue-node-style-node uml-node">
        <svg :width="layout.width" :height="layout.height">
            <rect fill="white"  width="100%" height="100%" />

            <g :style="{ fontSize: '10px', fontColor: 'white', fontFamily: 'Roboto,sans-serif', fontWeight: 300, fill: (tag.abstractness ? 'rgb(220, 20, 60)' :'rgb(96, 125, 139)' )}" >
                <rect x="0%" y="0%" width="100%" :height="45" />
                <text x="50%" y="25" :style="{ fontSize:'16px', fill: 'black' }" text-anchor="middle" >{{tag.name}}</text>
                
                <g>
                    <rect x="0" y="50" width="100%" :height=" 30 " />
                    <g>
                        <image v-if="!attributes_open" x="0" y="50" v-on:click="attributes_open=change_status(attributes_open)" xlink:href="../../assets/triangle.svg" />
                        <image v-if="attributes_open"  x="0" y="50" v-on:click="attributes_open=change_status(attributes_open)" xlink:href="../../assets/triangle_90deg_rotated.svg" />
                    </g>
                    <text x="20" y="70" :style="{ fontSize:'16px', fill: 'black' }" >Attributes:</text>
                    <image :x="layout.width - 22" y="55" width="18" xlink:href="../../assets/add-sign.svg" v-on:click="()=>methods.addAttributeToNode(tag, 'default')"/>
                    <g v-if="attributes_open">
                        <g v-for="(attribute, index) in tag.attributes" :key="attribute.name">
                            <foreignObject x="20" y="38"  
                                :transform="`translate(${0} ${(index + 2)*25})`"
                                width="100%" height=" 50 ">
                                <VueInlineTextEditor 
                                @change-input-mode="(newInputMode) => methods.changeInputMode(newInputMode)"                        
                                :inputMode="inputMode"                                
                                :value.sync="attribute.name" />
                            </foreignObject>                            
                            <image :x="layout.width - 22" 
                                y="45" 
                                :transform="`translate(${0} ${(index + 2)*25})`"
                                width="18" 
                                xlink:href="../../assets/delete-sign.svg" 
                                v-on:click="()=>methods.deleteAttributeFromNode(tag, attribute.name)"/>
                        </g>
                    </g>
                </g>

                <g>
                    <rect x="1" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" width="100%" :height="30" />              
                    <g v-on:click="operations_open=change_status(operations_open)">
                        <image v-if="!operations_open" x="0" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" xlink:href="../../assets/triangle.svg" />
                        <image v-if="operations_open"  x="0" :y="82 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" xlink:href="../../assets/triangle_90deg_rotated.svg" />
                    </g>
                    <text x="20" :y="102 + 30 * (attributes_open ? Object.keys(tag.attributes).length : 0)" :style="{  fontSize: '16px', fill: 'black' }" >Operation:</text>
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

import VueInlineTextEditor from "../nodes/VueInlineTextEditor";
import {changeInputMode} from "../../uml/nodes/styles/VuejsNodeStyle"

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
            //console.log("created" + this.tag)
        },

    watch: {
        attributes_open: function() {
            console.log("content")
            console.log(this.inputMode)
           // console.log(this.methods.addAttributeToNode)
        },
        inputMode: function(){
            console.log(this.inputMode)
        },

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
