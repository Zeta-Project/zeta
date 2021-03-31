<template>
    <div v-if="node">
        <node-properties :node="node"/>
    </div>
    <div v-else-if="edge">
        <edge-properties :edge="edge"
                         @add-attribute="(e, attributeName) => $emit('add-attribute-to-edge', e, attributeName)"
                         @add-operation="(e, operationName) => $emit('add-operation-to-edge', e, operationName)"
                         @delete-attribute="(e, attributeName) => $emit('delete-attribute-from-edge', e, attributeName)"
                         @delete-operation="(e, operationName) => $emit('delete-operation-from-edge', e, operationName)"
                         @on-edge-name-change="(e, name) => $emit('on-edge-name-change', e, name)"
                         @on-edge-style-change="e => $emit('on-edge-style-change', e)"
        />
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
