<template>
    <div>
        <h1 class="property-panel-header">Property Panel</h1>
        <div class="property-panel-content">
          <md-field>
            <label>Type here!</label>
            <md-input></md-input>
          </md-field>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'PropertyPanel',
        data: function () {
            return {

            }
        },
        watch: {
            item: function(newVal, oldVal) { // watch it
                console.log('Prop changed: ', newVal, ' | was: ', oldVal)
            }
        },
        methods: {},
        props: {
            item: {
                validator: prop => typeof prop === 'object' || prop === null,
                required: true,
            },
            isOpen: {
                type: Boolean,
                required: true
            },
        }
    }
</script>

<style scoped>
    .demo-toolbar > * {
        vertical-align: middle;
    }

    .demo-toolbar button {
        line-height: normal;
        height: 24px;
    }

    .demo-toolbar button,
    .demo-toolbar > label {
        display: inline-block;
        outline: none;
        border: none;
        background-repeat: no-repeat;
        background-position: 50% 50%;
        background-color: transparent;
        height: 24px;
        width: 24px;
        line-height: 24px;
        box-sizing: border-box;
        padding: 0;
        cursor: pointer;
    }

    .demo-toolbar button:hover,
    .demo-toolbar > label:hover {
        background-color: #dedede;
    }

    .demo-toggle-button {
        display: none !important;
    }

    .demo-toggle-button:checked + label {
        background-color: #dedede;
    }

    .demo-toggle-button:checked:hover + label {
        background-color: #b2b2b2;
    }

    .demo-toggle-button:disabled + label {
        opacity: 0.5;
        cursor: default;
        background-color: transparent;
    }

    .demo-toggle-button.labeled + label {
        background-position-x: left;
        width: inherit;
        padding: 0 2px;
        line-height: 24px;
    }

    .demo-toolbar button:active,
    .demo-toolbar > label:active,
    .demo-toolbar .demo-toggle-button:checked:active + label {
        background-color: #b2b2b2;
    }

    .demo-toolbar button:disabled,
    .demo-toolbar > .demo-toggle-button:disabled + label {
        opacity: 0.5;
        cursor: default;
        background-color: transparent;
    }

    .demo-separator {
        height: 20px;
        width: 1px;
        background: #999;
        display: inline-block;
        vertical-align: middle;
        margin: 0 2px;
    }

    .demo-icon-yIconReload {
        background-image: url('../../assets/reload-16.svg');
    }
</style>
