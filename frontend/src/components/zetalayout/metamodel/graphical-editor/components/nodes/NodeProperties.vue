<template>
    <div class="full-control" v-if="node">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <div class="list">
            <md-list :md-expand-single="true">
                <md-list-item md-expand>
                    <span class="md-list-item-text">Meta-Information</span>
                    <md-list slot="md-expand">
                        <md-list-item class="md-inset">
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="node.name"/>
                            </md-field>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-field>
                                <label>Description</label>
                                <md-input v-model="node.description"/>
                            </md-field>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <input type="checkbox" id="checkbox" v-model="node.abstractness">
                            <label for="checkbox" class="abstractLable">Is Abstract?</label>
                        </md-list-item>
                    </md-list>
                </md-list-item>

                <md-list-item md-expand>
                    <span class="md-list-item-text">Attributes</span>
                    <md-list slot="md-expand">
                        <md-list-item
                                v-if="node.attributes"
                                v-for="(attribute, index) in node.attributes"
                                :key="`${node.name}-properties-attributes-${index}`"
                                class="md-inset"
                        >
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="attribute.name"/>
                            </md-field>
                            <md-button class="md-icon-button md-dense md-primary" @click="$emit('delete-attribute', node, attribute.name)">
                                <md-icon class="fa fa-trash" />
                            </md-button>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-button class="md-raised md-primary" @click="$emit('add-attribute', node, 'default')">Add Attribute</md-button>
                        </md-list-item>
                    </md-list>
                </md-list-item>

                <md-list-item md-expand>
                    <span class="md-list-item-text">Operations</span>
                    <md-list slot="md-expand">
                        <md-list-item
                                v-if="node.methods"
                                v-for="(method, index) in node.methods"
                                :key="`${node.name}-properties-operations-${index}`"
                                class="md-inset"
                        >
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="method.name"/>
                            </md-field>
                            <md-button class="md-icon-button md-dense md-primary" @click="$emit('delete-operation', node, method.name)">
                                <md-icon
                                        class="fa fa-trash"
                                />
                            </md-button>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-button class="md-raised md-primary" @click="$emit('add-operation', node, 'default')">Add Operation</md-button>
                        </md-list-item>
                    </md-list>
                </md-list-item>
            </md-list>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'NodeProperties',
        data: function () {
            return {}
        },
        watch: {
            node: function (newVal, oldVal) { // watch it
                //console.log('Prop changed: ', newVal, ' | was: ', oldVal)
            }
        },
        props: {
            node: {
                validator: prop => typeof prop === 'object' || prop === null,
                required: true
            }
        }
    }
</script>

<style scoped>
    .abstractLable {
        width: 600px;
    }

    .full-control {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap-reverse;
    }

    .list {
        width: 100%;
    }

    .full-control > .md-list {
        width: 100%;
        max-width: 100%;
        height: 400px;
        display: inline-block;
        overflow: auto;
        border: 1px solid;
        vertical-align: top;
    }
</style>
