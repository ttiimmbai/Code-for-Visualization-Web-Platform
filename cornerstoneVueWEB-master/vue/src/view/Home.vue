<template>

  <div id="main" class="app-container" style="border-left: 0px">
<!--    <el-button-group>-->
<!--    <el-button ref="toBlackFocus" type="primary" icon="el-icon-edit" @click="toggleTheme('black')">黑色</el-button>-->
<!--    <el-button ref="toWhiteFocus" type="primary" icon="el-icon-share" @click="toggleTheme('white')">白色</el-button>-->
<!--      </el-button-group>-->
    <!--查询表单-->
<!--<router-link to="/three">跳转</router-link>-->
    <el-switch
      v-model=value
      active-text="黑色"
      inactive-text="白色"
      inactive-color="#ffffff"
      @change="toggleTheme()"
    >

    </el-switch>
    <el-form :inline="true" class="demo-form-inline">

      <el-form-item>

        <el-input v-model="patQuery.patientname" placeholder="患者名" size="small" />

      </el-form-item>

      <el-form-item>

        <el-input v-model="patQuery.patientid" placeholder="患者id" size="small"/>

      </el-form-item>
      <el-form-item>

        <el-input v-model="patQuery.hosid" placeholder="医院id" size="small"/>

      </el-form-item>

      <el-form-item>

        <el-input v-model="patQuery.position" placeholder="检查部位" size="small"/>

      </el-form-item>


      <el-form-item>

        <el-select v-model="patQuery.devtype" clearable placeholder="设备类型" size="small">

          <el-option :value="1" label="CT"/>

          <el-option :value="2" label="PET/CT"/>
          <el-option :value="3" label="MRI"/>
          <el-option :value="4" label="MRA"/>

        </el-select>

      </el-form-item>
      <el-form-item>

        <el-select v-model="patQuery.status" clearable placeholder="是否完成三维重建" size="small">

          <el-option :value="100" label="已完成三维重建"/>

          <el-option :value="0" label="未开始三维重建"/>

          <el-option :value="25" label="图像处理中"/>

          <el-option :value="50" label="图像分割中"/>

          <el-option :value="75" label="三维重建中"/>

        </el-select>

      </el-form-item>

<br/>

      <el-form-item >

        <el-date-picker

          v-model="patQuery.begin"

          type="datetime"

          placeholder="选择开始时间"

          value-format="yyyy-MM-dd HH:mm:ss"

          default-time="00:00:00"
          size="small"

        />

      </el-form-item>

      <el-form-item>

        <el-date-picker

          v-model="patQuery.end"

          type="datetime"

          placeholder="选择截止时间"

          value-format="yyyy-MM-dd HH:mm:ss"

          default-time="00:00:00"
          size="small"
        />

      </el-form-item>


      <el-button type="primary" icon="el-icon-search" @click="getList()" size="small">查询</el-button>

      <el-button type="default" icon="el-icon-error" @click="resetData()" size="small">清空</el-button>


    </el-form>

    <!-- 表格 -->

    <el-table
      :data="list"
      style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left">
            <el-form-item label="患者名称:"  class="labellist">
            {{ props.row.patientname }}
            </el-form-item>
<!--            <el-form-item label="描述:" class="labellist">-->
<!--            {{ props.row.description }}-->
<!--            </el-form-item>-->

            <el-form-item label="进度:" class="labellist">&nbsp;
             <el-progress class="labellist" v-bind:percentage='props.row.status' :format="format" ></el-progress>
            </el-form-item>

            <el-form-item class="labellist">
             <el-button type="primary" size="mini" icon="el-icon-view" @click="viewDicom(props.row.id)">查看影像</el-button>
            </el-form-item>
            <el-form-item class="labellist">
              <el-button type="primary" size="mini" icon="el-icon-view" @click="viewMPR(props.row.status,props.row.id,props.row.devtype)">多平面重建</el-button>
            </el-form-item>
            <el-form-item class="labellist">

                <el-button type="primary" size="mini" icon="el-icon-view"
                           :loading="props.row.status===100 ? false:props.row.status===0 ? false:true"
                           @click="restructing(props.row.status,props.row.id)">
                  {{props.row.status===100 ? "查看三维模型" :props.row.status===75 ? "三维重建中":props.row.status===50 ? "分割中":props.row.status===0 ? "开始三维重建":"图像处理中"}}
                </el-button>

            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column
        label="序号"
        width="70"
        align="center">

        <template slot-scope="scope">
          {{ (current - 1) * limit + scope.$index + 1 }}
        </template>

      </el-table-column>


      <el-table-column prop="patientname" label="名称" width="80" />

      <el-table-column label="设备类型" width="80" >
        <template slot-scope="scope">

          {{ scope.row.devtype==="1"?'CT':scope.row.devtype==="2"?'PET/CT':scope.row.devtype==="3"?'MRI':'MRA' }}
        </template>
      </el-table-column>



      <el-table-column prop="hosid" label="医院号" />

      <el-table-column prop="position" label="检查部位" />

      <el-table-column prop="gmtDate" label="添加时间" width="180"/>


      <el-table-column prop="seriesnum" label="序列号" width="120" />

    </el-table>
    <!-- 分页 -->

    <el-pagination

      :current-page="current"

      :page-size="limit"

      :total="total"

      style="padding: 30px 0; text-align: center;"

      layout="total, prev, pager, next, jumper"

      @current-change="getList"

    />

  </div>

</template>

<script>

  import pat from '../api/pat'
  import dicom from '../api/dicom'
  import '../assets/theme/black/theme/index.css'
    export default {
      data(){
        return{
          list:null,//查询之后返回的结果
          current:1,//当前页
          limit:10,//每页记录数
          total:0,//总记录数
          patQuery:{},//条件对象
          percentage:50,//
          root:null,//返回三维模型路径
          theme:'black',
          value:true
        }
      },

      methods:{


        toggleTheme(){
          if (this.value==true){
            this.theme='black'

            location.reload();

          }else {
            this.theme='white'
            document.getElementById("main").setAttribute("style","background: #ffffff")
           //  document.getElementById("labellist1").setAttribute("style","background: #B0E3EA")
           //  document.getElementById("labellist2").setAttribute("style","background: #B0E3EA")
           //  document.getElementById("labellist3").setAttribute("style","background: #B0E3EA")
           // // document.getElementById("labellist4").setAttribute("style","background: #B0E3EA")
           //  document.getElementById("labellist5").setAttribute("style","background: #B0E3EA")
           //  document.getElementById("labellist6").setAttribute("style","background: #B0E3EA")
           //  document.getElementById("labellist7").setAttribute("style","background: #B0E3EA")

          }
          console.log('主题色'+this.theme)
          import(`../assets/theme/${this.theme}/theme/index.css`);
          console.log(this.value)
        },


        viewDicom(id){
          window.location.href="src/test3_html/annotest.html?url=@@localhost&&id="+id;
        },
        viewMPR(status,id,devtype){
          if(status==100) {
            if (devtype==3){
              window.location.href = "src/test3_html/mri.html?url=@@localhost&&id=" + id;
            }else {
              window.location.href = "src/test3_html/threeD.html?url=@@localhost&&id=" + id;
            }

          }else {
            if (devtype==3){
              window.location.href = "src/test3_html/indexmri.html?url=@@localhost&&id=" + id;
            }else {
              window.location.href = "src/test3_html/index.html?url=@@localhost&&id=" + id;
            }

          }
        },
        restructing(status,id){

          if(status==100){
            dicom.return3D(id).then(
              response=>{
                this.root=response.data.root;
                console.log(this.root[0]);
                window.location.href="http://localhost:3000/?load="+this.root[0];
              }
            )

            //
          }else {
            dicom.restruct(id).then(
              response=>{
                this.$message({
                  type:'success',
                  message:'正在三维重建中'
                })
                this.getList()
              }).catch(error=>{
                console.log(error)
            })
          }

        },
        getList(current = 1){
          this.current=current
          pat.getPatListPage(this.current,this.limit,this.patQuery)
            .then(response=>{
              //response接口返回数据
              // console.log(response)
              this.list=response.data.rows
              this.total=response.data.total
              console.log(this.list)
              console.log(this.total)
            })//请求成功
            .catch(error =>{
              console.log(error)
            })//请求失败
        },
        format(percentage){
            if (percentage===0){

              return '图像未处理'
            }
            if (percentage===50){

            return '图像已解析'
            }
            if (percentage===100){

            return '三维重建已完成'
          }
        },


        resetData(){
          this.patQuery={}
          this.getList()
        },
      },
      created(){
        this.getList()

        // document.getElementById("main").setAttribute("style","background: #000000")
      },

    }
</script>

<style scoped>
body{
  margin: 0px;
  padding: 0px;
}
  div {
    /*background: #BAECF4;*/
    margin: 0;
    border: 0px;
    padding: 0px;
  }
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
  /*.labellist{*/
  /*  background: #040B34;*/
  /*}*/
</style>
