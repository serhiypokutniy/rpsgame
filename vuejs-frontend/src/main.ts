import Vue from 'vue'
import App from './App.vue'
import {BootstrapVue, IconsPlugin} from "bootstrap-vue";
import VueCookies from "vue-cookies"

import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

Vue.config.productionTip = false
Vue.use(BootstrapVue);
Vue.use(IconsPlugin)
Vue.use(VueCookies, { expires: '7d'})

new Vue({
  render: h => h(App),
}).$mount('#app')
