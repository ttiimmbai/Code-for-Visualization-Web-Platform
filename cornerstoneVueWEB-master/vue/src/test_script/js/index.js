var getParam = function () {
  try{
    var url = window.location.href;
    var result = url.split("?")[1];
    var keyValue = result.split("&");
    var obj = {};
    for (var i = 0; i < keyValue.length; i++) {
      var item = keyValue[i].split("=");
      obj[item[0]] = item[1];
    }
    return obj;
  }catch(e){
    console.warn("There has no param value!");
  }
};
//图像的URL
var imageUrl = getParam()['url'].replace(/@/g,"\\");
var id=getParam()['id'];
console.log("imgurl是。。。。。"+imageUrl)
var checkId= getParam()['checkNum'];


var imageIdlist;

//图像是否转换完的flag
var convertFlag = false;
//studyId
var studyId = "";
//接收后台传过来的图像集合
$.ajax({
  async:false,
  cache:true,
  traditional:true,//
  type: 'post',
  dataType: 'json',
  url: 'http://localhost:9091/get/sendDcm1',
  /*data: {xy:JSON.stringify(xy)},*/
  data : {
    id:id
  },
  // beforeSend:function(){
  //   var openId=$.openLoadForm();
  // },
  // complete:function(){
  //   $.closeLoadForm();//关闭openId的加载动画
  //   /* setTimeout(function(){
  //        $.closeLoadForm();//关闭openId的加载动画
  //    }, 1000);*/
  // },
  success: function (data) {
    imageIdlist = data;
    if(imageIdlist.length<1){
      alert("图像路径不存在");
    }
    //以下多线程操作
    /* for(i=1;i<imageIdlist.length;i++){
         var o = new Worker('../test_script/js/webworker.js');
         o.postMessage(imageIdlist[i]);
         o.onmessage = function (ev) {
             if(ev.data==200){

             }
         }
     }*/


    //以上多线程操作
    /*imageIdlist = data[0].map(function (item) {
        return 'wadouri:'+item;
    })*/
  },
  error: function(){
    alert("加载图像失败");
  }
});
const imageIds =imageIdlist

function _init() {
  // Externals
  cornerstoneWADOImageLoader.external.cornerstone = cornerstone
  cornerstoneWADOImageLoader.external.dicomParser = dicomParser
  cornerstoneTools.external.cornerstoneMath = cornerstoneMath
  cornerstoneTools.external.cornerstone = cornerstone
  cornerstoneTools.external.Hammer = Hammer
  cornerstoneSideImageLoader.external.cornerstone = cornerstone

  // Image Loader
  const config = {
    webWorkerPath: `https://tools.cornerstonejs.org/examples/assets/image-loader/cornerstoneWADOImageLoaderWebWorker.js`,
    taskConfiguration: {
      decodeTask: {
        codecsPath: `https://tools.cornerstonejs.org/examples/assets/image-loader/cornerstoneWADOImageLoaderCodecs.js`,
      },
    },
  }
  cornerstoneWADOImageLoader.webWorkerManager.initialize(config)
  cornerstoneTools.init()
}

const synchronizer = new cornerstoneTools.Synchronizer(
  "cornerstonenewimage",
  cornerstoneTools.updateImageSynchronizer,
)

const display = async (element, imageIds) => {
  cornerstone.enable(element)
  const image = await cornerstone.loadAndCacheImage(imageIds[0])
  cornerstone.displayImage(element, image)
  synchronizer.add(element)
  cornerstoneTools.addStackStateManager(element, [
    "stack",
    "playClip",
    "Crosshairs",
  ])
  cornerstoneTools.addToolState(element, "stack", {
    imageIds: [...imageIds],
    currentImageIdIndex: 0,
  })
  cornerstoneTools.addToolForElement(
    element,
    cornerstoneTools["StackScrollMouseWheelTool"],
  )
  cornerstoneTools.addToolForElement(
    element,
    cornerstoneTools["CrosshairsTool"],
  )
  cornerstoneTools.setToolActive("StackScrollMouseWheel", {
    mouseButtonMask: 0,
  })
  cornerstoneTools.setToolActive("Crosshairs", {
    mouseButtonMask: 2,
    synchronizationContext: synchronizer,
  })
  return Promise.all(
    imageIds.map((imageId) => cornerstone.loadAndCacheImage(imageId)),
  )
}

const { generateSideImages } = cornerstoneSideImageLoader

;(async function () {
  _init()
  const axial = document.querySelector("#axial")
  const sagittal = document.querySelector("#sagittal")
  const coronal = document.querySelector("#coronal")

  const images = await display(axial, imageIds)
  const { coronalImageIds, sagittalImageIds } = generateSideImages(images)
  await display(coronal, coronalImageIds)
  await display(sagittal, sagittalImageIds)
})()
