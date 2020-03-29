<template>
    <div class="full-control" v-if="tag">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <div class="list">
            <md-list :md-expand-single="expandSingle">
                <md-list-item md-expand :md-expanded.sync="expandNews">
                    <span class="md-list-item-text">Meta-Information</span>
                    <md-list slot="md-expand">
                        <md-list-item class="md-inset">
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="tag.name"></md-input>
                            </md-field>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-field>
                                <label>Description</label>
                                <md-input v-model="tag.description"></md-input>
                            </md-field>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-checkbox v-model="tag.stereotype" value="1">Is Abstract?</md-checkbox>
                        </md-list-item>
                    </md-list>
                </md-list-item>

                <md-list-item md-expand>
                    <span class="md-list-item-text">Attributes</span>
                    <md-list slot="md-expand">
                        <md-list-item
                                v-if="tag.attributes"
                                v-for="(attribute, index) in tag.attributes"
                                :key="`${tag.name}-properties-attributes-${index}`"
                                class="md-inset"
                        >
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="attribute.name"></md-input>
                            </md-field>
                            <md-button class="md-icon-button md-dense md-primary" @click="$emit('delete-attribute', attribute.name)">
                                <md-icon
                                        class="fa fa-trash"
                                />
                            </md-button>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-button class="md-raised md-primary" @click="$emit('add-attribute', 'default')">Add Attribute</md-button>
                        </md-list-item>
                    </md-list>
                </md-list-item>

                <md-list-item md-expand>
                    <span class="md-list-item-text">Operations</span>
                    <md-list slot="md-expand">
                        <md-list-item
                                v-if="tag.methods"
                                v-for="(method, index) in tag.methods"
                                :key="`${tag.name}-properties-operations-${index}`"
                                class="md-inset"
                        >
                            <md-field>
                                <label>Name</label>
                                <md-input v-model="method.name"></md-input>
                            </md-field>
                            <md-button class="md-icon-button md-dense md-primary" @click="$emit('delete-operation', method.name)">
                                <md-icon
                                        class="fa fa-trash"
                                />
                            </md-button>
                        </md-list-item>
                        <md-list-item class="md-inset">
                            <md-button class="md-raised md-primary" @click="$emit('add-operation', 'default')">Add Operation</md-button>
                        </md-list-item>
                    </md-list>
                </md-list-item>
            </md-list>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'PropertyPanel',
        data: function () {
            return {
                expandNews: false,
                expandSingle: false
            }
        },
        watch: {
            tag: function (newVal, oldVal) { // watch it
                //console.log('Prop changed: ', newVal, ' | was: ', oldVal)
            }
        },
        props: {
            tag: {
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
