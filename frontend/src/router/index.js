import VueRouter from 'vue-router'

import DiagramsOverview from '../components/overview/WebpageDiagramsOverview'
import MetamodelCodeEditor from '../components/metamodel/CodeEditor'
import MetamodelGraphicalEditor from '../components/metamodel/GraphicalEditor'

const router = new VueRouter({
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
})

export default router
