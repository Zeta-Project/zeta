import { createRouter, createWebHistory } from 'vue-router'

import DiagramsOverview from '../components/overview/WebpageDiagramsOverview'
import MetamodelCodeEditor from '../components/metamodel/CodeEditor'

const routerHistory = createWebHistory()

const router = createRouter({
    history: routerHistory,
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
        }
    ]
})

export default router
