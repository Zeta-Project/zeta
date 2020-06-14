<template>
    <div class="full-control" v-if="edge">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <div class="list">
            <md-list :md-expand-single="false">
                <md-list-item md-expand>
                    <span class="md-list-item-text">Meta-Information</span>
                    <md-list slot="md-expand">
                        <md-list-item class="md-inset">
                            <md-field
                                    v-if="edge.labels"
                                    v-for="(label, index) in edge.labels"
                                    :key="`${edge.name}-labels-${index}`"
                            >
                                <label>Name</label>
                                <md-input :value="label.text" @input="name => $emit('on-edge-name-change', edge, name)"/>
                            </md-field>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-field>
                                <label>Description</label>
                                <md-input v-model="edge.tag.description" />
                            </md-field>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-checkbox v-model="edge.tag.sourceDeletionDeletesTarget" value="1">sourceDeletionDeletesTarget</md-checkbox>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-checkbox v-model="edge.tag.targetDeletionDeletesSource" value="1">targetDeletionDeletesSource</md-checkbox>
                        </md-list-item>
                    </md-list>
                </md-list-item>

                <md-list-item md-expand>
                    <span class="md-list-item-text">Attributes</span>
                    <md-list slot="md-expand">
                        <md-list-item
                                v-if="edge.tag.attributes"
                                v-for="(attribute, index) in edge.tag.attributes"
                                :key="`${edge.tag.name}-properties-attributes-${index}`"
                                class="md-inset"
                        >
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="attribute.name"/>
                            </md-field>
                            <md-button class="md-icon-button md-dense md-primary" @click="$emit('delete-attribute', edge.tag, attribute.name)">
                                <md-icon class="fa fa-trash" />
                            </md-button>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-button class="md-raised md-primary" @click="$emit('add-attribute', edge.tag, 'default')">Add Attribute</md-button>
                        </md-list-item>
                    </md-list>
                </md-list-item>

                <md-list-item md-expand>
                    <span class="md-list-item-text">Operations</span>
                    <md-list slot="md-expand">
                        <md-list-item
                                v-if="edge.tag.operations"
                                v-for="(operation, index) in edge.tag.operations"
                                :key="`${edge.tag.name}-properties-operations-${index}`"
                                class="md-inset"
                        >
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="operation.name" />
                            </md-field>
                            <md-button class="md-icon-button md-dense md-primary" @click="$emit('delete-operation', edge.tag, operation.name)">
                                <md-icon
                                        class="fa fa-trash"
                                />
                            </md-button>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-button class="md-raised md-primary" @click="$emit('add-operation', edge.tag, 'default')">Add Operation</md-button>
                        </md-list-item>
                    </md-list>
                </md-list-item>
            </md-list>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'EdgeProperties',
        data: function () {
            return {}
        },
        watch: {
            edge: function (newVal, oldVal) { // watch it
                if(newVal.name !== oldVal.name){
                    console.log("name changed");
                }
                console.log('Prop changed: ', newVal, ' | was: ', oldVal)
            }
        },
        props: {
            edge: {
                validator: prop => typeof prop === 'object' || prop === null,
                required: true
            }
        }
    }
</script>

<style scoped>
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

    .control {
        min-width: 250px;
        display: flex;
        flex-direction: column;
        padding: 16px;
    }
</style>
