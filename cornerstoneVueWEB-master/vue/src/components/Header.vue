<template>
  <div style="height: 50px; line-height: 0px; border-bottom: 0px solid #ccc; display: flex; background: #000000">
    <div style="width: 200px; padding-left: 30px; font-weight: bold;color: #ffffff;margin-top: 20px">Web影像工作站</div>
    <div style="flex: 1"></div>
    <div style="width: 100px;margin-top: 20px;margin-right: 100px">
      <el-dropdown>
  <span class="el-dropdown-link" style="color: #ffffff">
    张三<i class="el-icon-arrow-down el-icon--right"></i>
  </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人信息</el-dropdown-item>
            <el-dropdown-item>退出系统</el-dropdown-item>

          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
  export default {
    name: "Header"
  }
</script>

<style scoped>

  .el-dropdown-link {
    cursor: pointer;
    color: #fafafa;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }


</style>
