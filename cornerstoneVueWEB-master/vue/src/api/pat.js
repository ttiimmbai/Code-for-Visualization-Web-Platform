import request from "../utils/request"
export default {
  //1.患者列表 条件查询带分页

  /**@params current:当前页，
   limit:每页记录数，
   patQuery条件对象
   */
  getPatListPage(current,limit,patQuery){
    return request({

      url: `/server/dicom-list/pagePatCondition/${current}/${limit}`,
      method: 'post',
      //teacherQuery对象，后端使用requestbody获取
      //data表示把对象转换为json进行传递
      data:patQuery

    })
  },
}
