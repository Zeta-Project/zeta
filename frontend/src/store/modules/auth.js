import {
    AUTH_REQUEST,
    AUTH_ERROR,
    AUTH_SUCCESS,
    AUTH_LOGOUT
} from "../actions/auth";
import { USER_REQUEST } from "../actions/user";
import axios from 'axios'

let state = {
    token: localStorage.getItem("user-token") || "",
    status: "",
    hasLoadedOnce: false
};

const getters = {
    isAuthenticated: () => !!state.token,
    authStatus: state => state.status
};

const actions = {
    [AUTH_REQUEST]: ({ commit, dispatch }, user) => {

        return new Promise((resolve, reject) => {
            commit(AUTH_REQUEST);

            axios.post(
                "http://localhost:9000/signIn",
                {
                    email: user.username,
                    password: user.password,
                    rememberMe: user.rememberMe
                },
                {withCredentials: true}
            ).then(
                (response) => {
                    localStorage.setItem("user-token", "response.token");
                    commit(AUTH_SUCCESS, response);
                    dispatch(USER_REQUEST);
                    resolve(response);
                },
                (error) => {
                    commit(AUTH_ERROR, error);
                    localStorage.removeItem("user-token");
                    reject(error);
                }
            )
        })
    },
    [AUTH_LOGOUT]: ({ commit }) => {
        return new Promise((resolve, reject) => {
            localStorage.removeItem("user-token");
            commit(AUTH_LOGOUT);
            axios.get("http://localhost:9000/signOut", {withCredentials: true}).then(
                (response) => {
                    resolve()
                },
                (error) => {
                    reject(error)
                }
            )
        });
    }
};

const mutations = {
    [AUTH_REQUEST]: state => {
        state.status = "loading";
    },
    [AUTH_SUCCESS]: (state, resp) => {
        state.status = "success";
        state.token = resp.token;
        state.hasLoadedOnce = true;
    },
    [AUTH_ERROR]: state => {
        state.status = "error";
        state.hasLoadedOnce = true;
    },
    [AUTH_LOGOUT]: state => {
        state.token = "";
    }
};

export default {
    state,
    getters,
    actions,
    mutations
};
