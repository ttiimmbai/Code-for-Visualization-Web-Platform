<template>
  <div style="border-top-color: #1a1818;margin: 0px;border-right: #1a1818">
    <el-menu style="width: 200px; min-height: calc(100vh - 50px)"
             default-active="2"
             class="el-menu-vertical-demo"
    >
      <el-sub-menu index="1">
        <template #title>
          <i class="el-icon-location"></i>
          <span>导航一</span>
        </template>
        <el-menu-item index="1-1">选项1</el-menu-item>
      </el-sub-menu>

    </el-menu>
  </div>
</template>

<script>
  export default {
    name: "Aside"
  }
</script>

<style scoped>

</style>
