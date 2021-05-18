<template>
    <div v-if="node">
        <node-properties :node="node"/>
    </div>
    <div v-else-if="edge">
        <edge-properties :edge="edge"
                         @on-edge-label-change="(edge, labelId, value) => $emit('on-edge-label-change', edge, labelId, value)"/>
    </div>
    <div v-else>
        Error! Unknown item selected
    </div>
</template>

<script>
    import NodeProperties from "../nodes/NodeProperties.vue";
    import EdgeProperties from "../edges/EdgeProperties.vue";

    export default {
        name: 'PropertyPanel',
        components: {
            NodeProperties,
            EdgeProperties
        },
        data: function () {
            return {}
        },
        props: {
            node: {
                validator: prop => typeof prop === 'object' || prop === null,
                required: true
            },
            edge: {
                validator: prop => typeof prop === 'object' || prop === null,
                required: true
            }
        }
    }
</script>

<style scoped/>
