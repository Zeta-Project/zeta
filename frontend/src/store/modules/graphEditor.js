import axios from 'axios'
import {
    // DEFINITION_REQUEST,
    // DEFINITION_REQUEST_SUCCESS, DIAGRAM_REQUEST,
    GSDL_REQUEST,
    GSDL_REQUEST_SUCCESS,
    // SHAPE_REQUEST,
    SHAPE_REQUEST_SUCCESS,
    // STYLE_REQUEST,
    STYLE_REQUEST_SUCCESS
} from "../actions/graphEditor";


let state = {
    gsdlProject: {
        id: "520ec611-1dbd-4a93-bf6c-2b316cb67f0b",
        name: "testproject",
        concept: "Concept",
        diagram: "diagram",
        shape: "shape",
        style: "style",
        validator: "None"
    },
    dslType: '',
    // definition: {},
    // diagram: {},
    shape: {},
    style: {}
}

const actions = {
    refreshGraph: ({dispatch, commit}, id) => {
        return dispatch('refreshGsdl', id)
            .then(
                () => dispatch('refreshShape', id)
                    .then(
                        () => dispatch('refreshStyle', id)
                    )
            )

    },

    refreshGsdl: ({commit}, id) => {

        return axios.get(
            "http://localhost:9000/rest/v1/meta-models/" + id,
            {withCredentials: true}
        ).then(
            (response) => {
                commit(GSDL_REQUEST_SUCCESS, response.data)
            }
            // (error) => EventBus.$emit("errorMessage", "Could not load selected metamodel: " + error)
        )
    },
    // [DEFINITION_REQUEST]: ({commit}, id) => {
    //     axios.get(
    //         "http://localhost:9000/rest/v1/meta-models/" + id + "/definition",
    //         {withCredentials: true}
    //     ).then(
    //         (response) => {
    //             commit(DEFINITION_REQUEST_SUCCESS, response)
    //         }
    //     )
    // },
    refreshShape: ({commit}, id) => {
        axios.get(
            "http://localhost:9000/rest/v1/meta-models/" + id + "/shape",
            {withCredentials: true}
        ).then(
            (response) => {
                commit(SHAPE_REQUEST_SUCCESS, response.data)
            }
        )
    },
    refreshStyle: ({commit}, id) => {
        axios.get(
            "http://localhost:9000/rest/v1/meta-models/" + id + "/style",
            {withCredentials: true}
        ).then(
            (response) => {
                commit(STYLE_REQUEST_SUCCESS, response.data)
            }
        )
    },
    // [DIAGRAM_REQUEST]: ({commit, id}) => {
    //     axios.get(
    //         "http://localhost:9000/rest/v1/meta-models/" + id + "/diagram",
    //         {withCredentials: true}
    //     ).then(
    //         (response) => {
    //             commit(DIAGRAM_REQUEST_SUCCESS, response)
    //         },
    //     )
    // },


    // "http://localhost:9000" + "/rest/v1/meta-models/" + metaModelId + "/definition"
}

const mutations = {
    [GSDL_REQUEST_SUCCESS]: (state, resp) => {
        state.gsdlProject = resp;
    },
    // [DEFINITION_REQUEST_SUCCESS]: (state, resp) => {
    //     state.definition = resp;
    // },
    [SHAPE_REQUEST_SUCCESS]: (state, resp) => {
        state.shape = resp;
    },
    [STYLE_REQUEST_SUCCESS]: (state, resp) => {
        state.style = resp;
    },

}


export default {
    state,
    // getters,
    actions,
    mutations
};