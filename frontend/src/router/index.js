import VueRouter from 'vue-router'

import DiagramsOverview from '../components/zetalayout/overview/WebpageDiagramsOverview'
import MetamodelCodeEditor from '../components/zetalayout/metamodel/CodeEditor'
import MetamodelGraphicalEditor from '../components/zetalayout/metamodel/GraphicalEditor'
import ZetaLayout from '../components/zetalayout/ZetaLayout'
import AccountLayout from '../components/accountlayout/AccountLayout'


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
            children: [
                {
                    path: 'signIn',
                    component: null
                },
                {
                    path: 'signUp',
                    component: null
                }
            ]
        }
    ]
})

export default router
