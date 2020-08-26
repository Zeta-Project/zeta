import VueRouter from 'vue-router'

import DiagramsOverview from '../components/zetalayout/overview/WebpageDiagramsOverview'
import MetamodelCodeEditor from '../components/zetalayout/metamodel/CodeEditor'
import MetamodelGraphicalEditor from '../components/zetalayout/metamodel/GraphicalEditor'
import ZetaLayout from '../components/zetalayout/ZetaLayout'
import AccountLayout from '../components/accountlayout/AccountLayout'
import SignIn from '../components/accountlayout/signin/SignIn'
import SignUp from '../components/accountlayout/signup/signUp'

import store from '../store'

const ifNotAuthenticated = (to, from, next) => {
    if (!store.getters.isAuthenticated) {
        next();
        return;
    }
    next("/");
};

const ifAuthenticated = (to, from, next) => {
    if (store.getters.isAuthenticated) {
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
                    path: 'codeEditor/editor/:id/:dslType',
                    component: MetamodelCodeEditor
                },
                {
                    path: 'metamodel/editor/:id',
                    component: MetamodelGraphicalEditor
                }
            ]
        },
        {
            path: '/account',
            component: AccountLayout,
            beforeEnter: ifNotAuthenticated,
            children: [
                {
                    path: 'signIn',
                    component: SignIn
                },
                {
                    path: 'signUp',
                    component: SignUp
                }
            ]
        }
    ]
})

export default router
