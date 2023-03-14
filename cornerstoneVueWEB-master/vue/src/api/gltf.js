const obj2gltf = require("obj2gltf");
const fs = require("fs");
const options={
  separate:""
}
obj2gltf("D:\\Test\\MCObj\\polywrite.obj",options).then(function (gltf) {
  const data = Buffer.from(JSON.stringify(gltf));
  fs.writeFileSync("D:\\Test\\MCObj\\polywrite.obj", data);

});
