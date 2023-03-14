import request from "../utils/request"
export default {

  getDicom (id) {
    return request({

      url: `/server/dicom-list/getSeriesnumAndDateById/${id}`,
      method: 'post',

    })
  },

  getStatus (id) {
    return request({

      url: `/server/dicom-list/getStatusById/${id}`,
      method: 'post',

    })
  },
  restruct (id) {
    return request({
      url: `/processing/dicom-list/restructById/${id}`,
      method: 'post'
    })
  },

  return3D (id) {
    return request({
      url: `/processing/dicom-list/return3D/${id}`,
      method: 'post'
    })
  },
}
