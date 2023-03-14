package com.example.processingserver.service;

import java.util.List;
import java.util.Map;

public interface ReadDicomFileTagInfoService {
    /*
     *获取病人的基本信息
     */
  void  getUserInfo(String filePath);

    /*
     *获取DICOM图像的Tag信息，studyUID,seriesUID
     */
   void  getTagInfo(String filePath);






   String getSex();

   void setSex(String sex);

   String getModality();

   void setModality(String modality);

   String getDate();

   void setDate(String date);

   String getBirth();

   void setBirth(String birth);

   String getAge();

   void setAge(String age);

   String getStudyDate();

   void setStudyDate(String studyDate);

   String getAccessionN();

   void setAccessionN(String accessionN);

   String getPatientId();

   void setPatientId(String patientId);

   String getPatName();

   void setPatName(String patName);

   String getStudyUID();

   void setStudyUID(String studyUID);

   String getSeriesUID();

   void setSeriesUID(String seriesUID);

   Map<String, List<String>> getFilenameMap();

   void setFilenameMap(Map<String, List<String>> filenameMap);

   Map<String, Integer> getStudy_countMap();

   void setStudy_countMap(Map<String, Integer> study_countMap);

   Map<String, List<String>> getSeriesMap();

   void setSeriesMap(Map<String, List<String>> seriesMap) ;

   Map<String, String> getSeriesDiMap() ;

   void setSeriesDiMap(Map<String, String> seriesDiMap);

   Map<String, String> getFileInstanceNumMap();

   void setFileInstanceNumMap(Map<String, String> fileInstanceNumMap);

}
