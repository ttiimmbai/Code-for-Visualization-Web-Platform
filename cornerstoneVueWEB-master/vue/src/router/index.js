import Vue from 'vue'
import Router from 'vue-router'
import DefaultRoute from './DefaultRoute'
import App from '../App'
import Home from '../view/Home'
// import Three from '../view/three'


Vue.use(Router)

export default new Router({
  mode:'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path:'/dicom/:id',
      name:'dicom',
      component: DefaultRoute
    },
    // {
    //   path:'/three',
    //   name:'dicom',
    //   component: Three
    // },

  ]
})
