import VueRouter from 'vue-router'

import DiagramsOverview from '../components/zetalayout/overview/WebpageDiagramsOverview'
import MetamodelCodeEditor from '../components/zetalayout/metamodel/CodeEditor'
import MetamodelGraphicalEditor from '../components/zetalayout/metamodel/GraphicalEditor'
import ZetaLayout from '../components/zetalayout/ZetaLayout'

/*const router = new VueRouter({
    mode: 'history',
    routes: [
        {
            path: '/',
            redirect: '/overview'
        },
        {
            path: '/overview/:id?',
            component: DiagramsOverview
        },
        {
            path: '/codeEditor/editor/:id/:dslType',
            component: MetamodelCodeEditor
        },
        {
            path: '/metamodel/editor/:id',
            component: MetamodelGraphicalEditor
        }
    ]
})*/

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
            component: DiagramsOverview,
            children: [

            ]
        }
    ]
})

export default router
