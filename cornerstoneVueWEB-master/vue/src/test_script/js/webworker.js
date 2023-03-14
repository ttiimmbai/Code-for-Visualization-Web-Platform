
this.onmessage = function(ev){
    var imgList=ev.data
    console.log(imgList.length);
    for (let i =0;i<imgList.length;i++){
        let xhr = new XMLHttpRequest();
        xhr.open('GET',imgList[i],true);
        xhr.onreadystatechange=()=>{
            if(xhr.readyState == 4){
            }
        }
        xhr.send(null)
        if(i==imgList.length-1){
            postMessage(200);

        }
    }


    /*xhr.open('get', 'http://192.168.2.135:8080/test\\H0002\\2019-01\\12\\H0002CT001461\\1.3.12.2.1107.5.1.4.66563.30000016091200210306200000047\\1.3.12.2.1107.5.1.4.66563.30000016091200221342100013840\\CT.1.3.12.2.1107.5.1.4.66563.30000016091200221342100013843.dcm', true);
    xhr.responseType = 'arraybuffer';
    xhr.onreadystatechange=()=>{
        if(xhr.readyState == 4){
        }
    }
    xhr.send(null)*/
}