<template>
  <!-- WRAPPER -->
  <div
    class="image-canvas-wrapper"
    oncontextmenu="return false"
    unselectable='on'
    onselectstart='return false;'
    onmousedown='return false;'
  >
    <div>
      <el-dropdown split-button v-bind:type=type1 @click="handleClick1" @command="handleCommand1">
        <i class="el-icon-thumb"></i> {{operation}}
      <el-dropdown-menu slot="dropdown" @command="handleCommand1">
        <el-dropdown-item command="放大镜">放大镜</el-dropdown-item>
        <el-dropdown-item command="旋转">旋转</el-dropdown-item>

      </el-dropdown-menu>
    </el-dropdown>

      <el-dropdown split-button v-bind:type=type2 @click="handleClick2" @command="handleCommand2">
        <i class="el-icon-edit"></i> {{annotation}}
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="长度测量">长度测量</el-dropdown-item>
          <el-dropdown-item command="箭头标注">箭头标注</el-dropdown-item>
          <el-dropdown-item command="自由标注">自由标注</el-dropdown-item>
          <el-dropdown-item command="矩形标注">矩形标注</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    <el-button type="primary" icon="el-icon-refresh" @click="deleteAnnotation">清除标注</el-button>
      <el-button type="primary" icon="el-icon-sunny" @click="invert">负像</el-button>
      <el-button type="primary" @click="hflip">左右镜像</el-button>
      <el-button type="primary" @click="vflip">上下镜像</el-button>
      <el-button v-bind:type=type3 @click="playClip">{{play}}</el-button>

<!--      <el-popover-->
<!--        placement="right"-->
<!--        width="400"-->
<!--        trigger="click">-->
<!--       长:-->
<!--        <el-select v-model="value1" placeholder="请选择">-->
<!--        <el-option-->
<!--          v-for="item in len"-->
<!--          :key="item.value"-->
<!--          :label="item.label"-->
<!--          :value="item.value">-->
<!--        </el-option>-->
<!--      </el-select><br/>-->
<!--       宽:   <el-select v-model="value2" placeholder="请选择">-->
<!--        <el-option-->
<!--          v-for="item in wid"-->
<!--          :key="item.value"-->
<!--          :label="item.label"-->
<!--          :value="item.value">-->
<!--        </el-option>-->
<!--      </el-select>-->
<!--        <el-button type="primary" @click="loadSeries">确认</el-button>-->


<!--<el-button type="primary" slot="reference">序列窗格</el-button>-->
<!--      </el-popover>-->




    </div>
    <!-- DICOM CANVAS -->
    <div
      ref="canvas"
      class="image-canvas"
      oncontextmenu="return false"
    ></div>



  </div>

</template>

<script>
// External Dependencies
import $ from "jquery";
import Hammer from "hammerjs";
import * as cornerstoneMath from "cornerstone-math";

// Cornerstone Libraries
import * as cornerstone from "cornerstone-core";
import * as cornerstoneTools from "cornerstone-tools";
import * as cornerstoneWebImageLoader from "cornerstone-web-image-loader";

import dicom from "../api/dicom"

// Specify external dependencies
// TODO: Cornerstone really should show a better error message when these aren't set
// It's worth noting that you only need these when the cornerstone libraries are loaded as modules.
// I'm 90% sure, when loaded with script tags, everything assumes global scope
cornerstone.external.$ = $;
cornerstoneTools.external.$ = $;
cornerstoneTools.external.Hammer = Hammer;
cornerstoneTools.external.cornerstone = cornerstone;
cornerstoneTools.external.cornerstoneMath = cornerstoneMath;
cornerstoneWebImageLoader.external.$ = $;
cornerstoneWebImageLoader.external.cornerstone = cornerstone;
cornerstoneWebImageLoader.external.cornerstoneMath = cornerstoneMath;

// Here is how you register an image loader w/ Cornerstone
// Under the hood, this image loader is also registering a "metaDataProvider"
// Each of these has an interface you can program against to create a custom
// Image loader, or metaDataProvider if you need to get data/images into cornerstone
cornerstone.registerImageLoader("http", cornerstoneWebImageLoader.loadImage);
cornerstone.registerImageLoader("https", cornerstoneWebImageLoader.loadImage);

export default {
  name: "CornerstoneCanvas",
  data() {
    return {
      baseUrl: "http://localhost:8585/static/simple-study",
      // Pass in as a property, or use a computed property that looks at Vuex
      // Then... Watch for changes. On change, load the new series
      exampleStudyImageIds: [],
      isInitLoad: true,
      id: "",
      type1:"primary",
      type2:"primary",
      type3:"primary",
      input:"",
      operation:"操作",
      annotation:"标注",
      play:"播放",
      value1:'',
      value2:'',
      len:[
        {
          value:'1',
          label:'1'
        },
        {
          value:'2',
          label:'2'
        },
        {
          value:'3',
          label:'3'
        },
        {
          value:'4',
          label:'4'
        },
        {
          value:'5',
          label:'5'
        },
        {
          value:'6',
          label:'6'
        },

      ],
      wid:[
        {
          value:'1',
          label:'1'
        },
        {
          value:'2',
          label:'2'
        },
        {
          value:'3',
          label:'3'
        },
        {
          value:'4',
          label:'4'
        },
        {
          value:'5',
          label:'5'
        },
        {
          value:'6',
          label:'6'
        },

      ]
    };
  },
created () {

},

   async mounted() {

   await this.getDicom();
    console.log("执行完成")
    let _self = this;
    // this.listenForCornerstoneImageRendered()
    // this.listenForCornerstoneImageLoaded()
    this.listenForWindowResize();

    // Enable Canvas
    let canvas = this.$refs.canvas;
    cornerstone.enable(canvas);



    // ImageId that matches our registered image loader's 'http:' prefix
    // The webImageLoader uses this to make an xhr request to fetch an image
    // Under the hood, it creates a cornerstone "Image" object needed for display

    const imageUrl = this.baseUrl + this.exampleStudyImageIds[0];

    cornerstone.loadImage(imageUrl).then(
      function(image) {
      // Display our loaded image on the target canvas
      cornerstone.displayImage(canvas, image);

      // TODO: It really should be possible to "turn on tools" before an image is loaded
      if (_self.isInitLoad) {
        _self.initCanvasTools();
      }

    });
  },
  beforeDestroy() {
    // Remove jQuery event listeners
    let canvas = this.$refs.canvas;
    $(canvas).off();
  },
  methods: {
    handleClick1(){
      if (this.type1=="success"){
      this.type1="primary";
      this.operation="操作";
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      this.disableAllTools();
      cornerstoneTools.wwwc.activate(canvas,1); // left click
      cornerstoneTools.pan.activate(canvas, 2); // middle click
      cornerstoneTools.zoom.activate(canvas, 4); // right click
      }
    },
    handleCommand1(command){
      this.type2="primary";
      this.type1="success";
      this.operation=command;
      this.annotation="标注"
      this.disableAllTools();
      if (command=="放大镜"){
        this.magnify();
      }
      if (command=="旋转"){
        this.rotate();
      }
    },


    handleClick2(){
      if (this.type2=="success") {
        this.type2 = "primary"
        this.annotation = "标注"
        this.disableAllTools();
        let canvas = this.$refs.canvas;
        cornerstone.enable(canvas);
        cornerstoneTools.wwwc.activate(canvas, 1); // left click
        cornerstoneTools.pan.activate(canvas, 2); // middle click
        cornerstoneTools.zoom.activate(canvas, 4); // right click
      }
    },
    handleCommand2(command){
      this.type2="success"
      this.type1="primary"
      this.annotation=command
      this.operation="操作"
      this.disableAllTools();
      if (command=="长度测量"){
        this.length();
      }
      if (command=="箭头标注"){
        this.arrowAnnotation();
      }

      if (command=="自由标注"){
        this.freehandRoi();
      }
      if (command=="矩形标注"){
        this.rectangle();
      }
      console.log(command)
    },
    deleteAnnotation(){
      console.log("清除标注")
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      cornerstoneTools.clearToolState(canvas,"length");
      cornerstoneTools.clearToolState(canvas,"arrowAnnotate");
      cornerstoneTools.clearToolState(canvas,"freehand");
      cornerstoneTools.clearToolState(canvas,"rectangleRoi");
      cornerstone.updateImage(canvas);
      // cornerstoneTools.clearToolState(element,"ctr");
      // cornerstoneTools.clearToolState(element,"arrowAnnotate");
      // cornerstoneTools.clearToolState(element,"freehand");
      // cornerstoneTools.clearToolState(element,"freehandanno");
    },
    disableAllTools(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);

      cornerstoneTools.wwwc.deactivate(canvas,1); // left click
      cornerstoneTools.pan.deactivate(canvas, 2); // middle click
      cornerstoneTools.zoom.deactivate(canvas, 4); // right click
      cornerstoneTools.length.deactivate(canvas,1);
      cornerstoneTools.magnify.deactivate(canvas);
      cornerstoneTools.arrowAnnotate.deactivate(canvas,1);
      cornerstoneTools.freehand.deactivate(canvas,1);
      cornerstoneTools.rectangleRoi.deactivate(canvas,1);
      cornerstoneTools.rotate.deactivate(canvas,1);


    },
    loadSeries(){
      console.log(this.value1+'   '+this.value2)
    },

    length(){
        let canvas = this.$refs.canvas;
        cornerstone.enable(canvas);
        cornerstoneTools.length.activate(canvas,1);
    },
    invert(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      let viewport = cornerstone.getViewport(canvas);
      viewport.invert = !viewport.invert;
      cornerstone.setViewport(canvas,viewport)
    },

    playClip(){

      if(this.play=="播放"){
        this.type3="success"
        this.play="暂停"
        let canvas = this.$refs.canvas;
        cornerstone.enable(canvas);
        cornerstoneTools.playClip(canvas,15);
      }else {
        this.type3="primary"
        this.play="播放"
        let canvas = this.$refs.canvas;
        cornerstone.enable(canvas);
        cornerstoneTools.stopClip(canvas,15);
      }



    },


    hflip(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      let viewport = cornerstone.getViewport(canvas);
      viewport.hflip = !viewport.hflip;
      cornerstone.setViewport(canvas,viewport)
    },

    vflip(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      let viewport = cornerstone.getViewport(canvas);
      viewport.vflip = !viewport.vflip;
      cornerstone.setViewport(canvas,viewport)
    },

    arrowAnnotation(){
        let canvas = this.$refs.canvas;
        cornerstone.enable(canvas);
        cornerstoneTools.arrowAnnotate.activate(canvas,1);


    },
    rotate(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      cornerstoneTools.rotate.activate(canvas,1);
    },

    freehandRoi(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      cornerstoneTools.freehand.activate(canvas,1);
    },

    rectangle(){
      let canvas = this.$refs.canvas;
      cornerstone.enable(canvas);
      cornerstoneTools.rectangleRoi.activate(canvas,1);
    },

    magnify(){

       let canvas = this.$refs.canvas;
       cornerstone.enable(canvas);
       cornerstoneTools.magnify.activate(canvas,1);


    },


    async getDicom(){
      this.id=this.$route.params.id;
      await dicom.getDicom(this.id).then(response=>{
      this.exampleStudyImageIds=  response.data.list

      })
    },

    initCanvasTools: function() {
      let _self = this;
      let canvas = this.$refs.canvas;

      console.log(canvas);
      this.isInitLoad = false;

      // Find imageIds for canvasStack
      let allImageIds = [];

      this.exampleStudyImageIds.forEach(function(imageId) {
        let imageUrl = _self.baseUrl + imageId;
        allImageIds.push(imageUrl);
      });

      // Create canvasStack
      let canvasStack = {
        currentImageIdIndex: 0,
        imageIds: allImageIds
      };

      // Enable Inputs
      cornerstoneTools.mouseInput.enable(canvas);
      cornerstoneTools.mouseWheelInput.enable(canvas);
      cornerstoneTools.touchInput.enable(canvas);

      // Set the stack as tool state
      cornerstoneTools.addStackStateManager(canvas, ["stack"]);
      cornerstoneTools.addToolState(canvas, "stack", canvasStack);
      cornerstoneTools.stackScrollWheel.activate(canvas); // Mouse wheel
      cornerstoneTools.scrollIndicator.enable(canvas); // Position indicator

      // Mouse
      cornerstoneTools.wwwc.activate(canvas, 1); // left click
      cornerstoneTools.pan.activate(canvas, 2); // middle click
      cornerstoneTools.zoom.activate(canvas, 4); // right click


      // Touch / Gesture
      cornerstoneTools.wwwcTouchDrag.activate(canvas); // - Drag
      cornerstoneTools.zoomTouchPinch.activate(canvas); // - Pinch
      cornerstoneTools.panMultiTouch.activate(canvas); // - Multi (x2)

    },
    /*
     * Window Resize
     *
    */
    listenForWindowResize: function() {
      this.$nextTick(function() {
        window.addEventListener(
          "resize",
          this.debounce(this.onWindowResize, 100)
        );
      });
    },
    onWindowResize: function() {
      cornerstone.resize(this.$refs.canvas, true);
    },
    /*
     * Utility Methods
     *
    */
    debounce: function(func, wait, immediate) {
      var timeout;
      return function() {
        var context = this;
        var args = arguments;
        var later = function() {
          timeout = null;
          if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
      };
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.image-canvas-wrapper {
  width: 100%;
  height: 525px;
}

.image-canvas {
  width: 100%;
  height: 100%;
}
</style>
