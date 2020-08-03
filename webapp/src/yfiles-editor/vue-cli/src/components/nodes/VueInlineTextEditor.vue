<!--
MIT License

Copyright (c) 2018 CauseLabs

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

https://github.com/causelabs/vue-inline-text-editor
-->

<template>
    <div class="inline-editor" :class="classes">
        <div v-if="editingValue !== null" class="content-field">
            <input
                type="text"
                v-model="editingValue"
                @change="emitChange"
                @blur="emitBlur"
                @keyup.enter="updateValue"
                @keyup.esc="cancelEdit"
                greetings
                :placeholder="placeholder"
                :required="required"
                ref="input"
            >
        </div>
        <div  @click.stop="clickHandler" class="value-display" v-if="currentlyEditing == false">
            <span>{{ formattedValue }}
            </span>
        </div>
    </div>
</template>

<script>

export default {
    name: 'VueInlineTextEditor',
    props: {
        autofocus: {
            type: Boolean,
            default: true
        },
        closeOnBlur: {
            type: Boolean,
            default: false
        },
        disabled: {
            type: Boolean,
            default: false
        },
        hoverEffects: {
            type: Boolean,
            default: false
        },
        maxLength: {
            type: Number,
            default: null
        },
        minLength: {
            type: Number,
            default: null
        },
        placeholder: {
            type: String,
            default: null
        },
        required: {
            type: Boolean,
            default: false
        },
        type: {
            type: String,
            default: 'text',
            validator (value) {
                return ['text', 'number', 'currency', 'percentage'].indexOf(value) > -1
            }
        },
        inputMode:{
            required: true
        },
        value: {
            required: true
        }
    },
    data () {
        return {
            editingValue: null,
            internalValue: this.value,
            currentlyEditing: false,
            savedInputMode: null //JSON.parse(JSON.stringify(this.inputMode)) //deep copy of original input mode
        }
    },
    computed: {
        classes () {
            let classNames = []
            if (this.hoverEffects) {
                classNames.push('hover-effects')
            }
            if (this.editingValue !== null) {
                classNames.push('editing')
            }
            if (this.disabled) {
                classNames.push('disabled')
            }
            classNames.push('type-' + this.type)
            return classNames.join(' ')
        },

        formattedValue () {
            if ((null === this.internalValue) || ('' === this.internalValue)) {
                return this.placeholder
            }
            return this.internalValue
        }
    },
    watch: {
        internalValue (newValue) {
           // console.log(newValue)
            this.$emit('update:value', newValue)
        },
        selectValue (newValue) {
            console.log(newValue)
            this.internalSelectValue = newValue
        },
        value (newValue) {
            console.log(newValue)
            this.internalValue = newValue
        },
    },
    mounted () {
        // If this field is required, but is empty, open the editor
        if (this.required) {
            if ((this.internalValue === '') || (this.internalValue === null)) {
                this.editingValue = ''
                this.showSelect = true
            }
        }
    },
    methods: {
        cancelEdit () {
            this.internalSelectValue = this.originalSelectValue
            this.closeEditor()
        },
        closeEditor () {
            this.unlockInputMode();
            this.currentlyEditing = false;
            this.editingValue = null
            this.$emit('close')
            this.originalSelectValue = null
            console.log("close", this.inputMode)
        },
        clickHandler(){          
            this.currentlyEditing = true;
            this.lockInputMode();
            console.log("click input", this.inputMode)
            console.log("click saved", this.savedInputMode)
            this.editValue()
        },
        
        lockInputMode(){
            //        console.log("click saved")
            this.savedInputMode = this.inputMode //JSON.parse(JSON.stringify(this.inputMode)) //deep copy of original input mode
            this.$emit('change-input-mode', null);
            //this.inputMode = null;
        },

        unlockInputMode(){
            this.$emit('change-input-mode', this.savedInputMode);
//            this.$emit('inputMode', this.savedInputMode);
//            this.inputMode = this.savedInputMode;
        },

        editValue () {
            if (this.disabled) {
                return
            }
            if (this.internalValue === null) {
                // Clicking into an empty editor, set to an empty string
                this.editingValue = ''
            } else {
                this.editingValue = this.internalValue
            }
            this.filterValue()
            this.originalSelectValue = this.internalSelectValue
            // Set the focus to the input
            window.setTimeout(() => {
                this.showSelect = true
                this.focus()
            }, 10)
            this.$emit('open')
        },
        emitBlur (e) {
            this.$emit('blur', e)
            if (this.closeOnBlur === true) {
                this.updateValue()
            }
        },
        emitChange (e) {
            this.$emit('change', e)
        },
        filterValue () {
            if (this.editingValue === null) {
                return
            }
            if (['number', 'currency', 'percentage'].indexOf(this.type) > -1) {
                this.editingValue = this.editingValue.toString().replace(/[^0-9.]/g, '')
            }
        },
        focus () {
            try {
                this.$nextTick(() => {
                    if (this.$refs && this.$refs.input) {
                        this.$refs.input.focus()
                    }
                })
            } catch (ignore) {
                // ignore
            }
        },

        greetings () {
            alert("I am an alert box!");
        },

        updateValue () {
            let isChanged = false
            if (this.internalValue !== this.editingValue) {
                this.internalValue = this.editingValue
                isChanged = true
            }
            if (isChanged) {
                this.$nextTick(() => {
                    this.$emit('update')
                })
            }
            this.closeEditor()
        },


    }
}
</script>

<style lang="css" scoped>


.inline-editor{
    display: inline-block;
    cursor: text;
}

.value-display{
    font-size: 15px;
}
.content-field{
    background-color: white;
    font-size: 15px;
}
</style>