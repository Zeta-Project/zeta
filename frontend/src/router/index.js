import VueRouter from 'vue-router'

import DiagramsOverview from '@/components/zetalayout/overview/WebpageDiagramsOverview'
import ZetaLayout from '@/components/zetalayout/ZetaLayout'
import AccountLayout from '@/components/accountlayout/AccountLayout'
import SignIn from '@/components/accountlayout/signin/SignIn'
import SignUp from '@/components/accountlayout/signup/signUp'
import PasswordForgot from "@/components/accountlayout/password/forgot/PasswordForgot";
import PasswordChange from "@/components/accountlayout/password/change/PasswordChange";
import ModelEditor from "@/components/zetalayout/model/model-editor/GraphicalEditor"


const isAuthenticated = function() {
    return !!(localStorage.getItem("user-token") || "")
}

const ifNotAuthenticated = (to, from, next) => {
    if (!isAuthenticated()) {
        next();
        return;
    }
    next("/");
};

const ifAuthenticated = (to, from, next) => {
    if (isAuthenticated()) {
        next();
        return;
    }
    next("/account/signIn");
};


const router = new VueRouter({
    mode: 'history',
    routes: [
        {
            path: '/',
            redirect: '/zeta/overview'
        },
        {
            path: '/zeta',
            component: ZetaLayout,
            beforeEnter: ifAuthenticated,
            children: [
                {
                    path: 'overview/:id?',
                    component: DiagramsOverview
                },
                {
                    path: 'model/editor/:id',
                    component: ModelEditor
                }
            ]
        },
        {
            path: '/account',
            component: AccountLayout,
            children: [
                {
                    path: 'signIn',
                    component: SignIn,
                    beforeEnter: ifNotAuthenticated
                },
                {
                    path: 'signUp',
                    component: SignUp,
                    beforeEnter: ifNotAuthenticated
                },
                {
                    path: 'password/forgot',
                    component: PasswordForgot,
                    beforeEnter: ifNotAuthenticated
                },
                {
                    path: 'password/change',
                    component: PasswordChange,
                    beforeEnter: ifAuthenticated
                }
            ]
        }
    ]
})

export default router
