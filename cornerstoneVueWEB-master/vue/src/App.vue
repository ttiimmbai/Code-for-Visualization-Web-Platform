<template>
  <div >
  <div>
    <!--  头部-->
    <Header/>
  </div>
  <!--  主体-->
  <div>
    <!--    侧边栏-->

    <!--    内容区-->
    <router-view style="flex: 1;height: 800px"/>

  </div>
  </div>
</template>

<style>

</style>
<script>
  import Header from "./components/Header";
  import Aside from "./components/Aside";

  export default {
    name:"Layout",
    components:{

      Header
    }
  }
</script>
