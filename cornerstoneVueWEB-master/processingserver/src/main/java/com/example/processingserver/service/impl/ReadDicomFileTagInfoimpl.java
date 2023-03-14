package com.example.processingserver.service.impl;

import com.example.processingserver.service.ReadDicomFileTagInfoService;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *读Tag信息
 */
@Service
public class ReadDicomFileTagInfoimpl implements ReadDicomFileTagInfoService {


    private String sex ;
    private String modality ;
    private String date ;
    private String birth;
    private String age ;
    private String studyDate ;
    private String accessionN ;
    private String patientId ;
    private String patName;
    private String StudyUID;
    private String SeriesUID;

    private Map<String, List<String>> filenameMap = new HashMap<>();
    private Map<String, Integer> study_countMap = new HashMap<>();
    private Map<String, List<String>> SeriesMap = new HashMap<>();
    private Map<String , String> seriesDiMap = new HashMap<>();
    private Map<String , String> fileInstanceNumMap = new HashMap<>();

    /**
     *  读Tag信息
     */
    private Attributes loadDicomObject(File f) throws IOException {
        if (f == null)
        {
            return null;
        }
        else
        {
            DicomInputStream dis = new DicomInputStream(f);
            Attributes obj = dis.readDataset(-1, -1);
            dis.close();
            return obj;
        }
    }

    /*
     *获取病人的基本信息
     */
    @Override
    public void  getUserInfo(String filePath){
        try{
            File src = new File(filePath);
            Attributes attr1 = this.loadDicomObject(src);
            String patientIDtmp = new String(attr1.getBytes(Tag.PatientID) , "gb18030").toString().trim();
            String patientNametmp = new String(attr1.getBytes(Tag.PatientName) , "gb18030").toString().trim();
            String patientSex = new String( attr1.getBytes(Tag.PatientSex) , "gb18030").toString().trim();
            String patientAge = new String(attr1.getBytes(Tag.PatientAge) , "gb18030").toString().trim();
            String type = new String(attr1.getBytes(Tag.Modality),"gb18030").toString().trim();
            String studyDate = new String( attr1.getBytes(Tag.StudyDate) , "gb18030").toString().trim();

            patientId = patientIDtmp;
            patName = patientNametmp;
            sex = patientSex;
            age = patientAge;
            modality = type;
            date = studyDate;

        }catch(Exception e){
            e.printStackTrace();
            return;

        }

    }

    /*
     *获取DICOM图像的Tag信息，studyUID,seriesUID
     */
    public static void main(String[] args){
        ReadDicomFileTagInfoimpl a=new ReadDicomFileTagInfoimpl();
//        org.springmvc.service.impl.ReadDicomFileTagInfoimpl a = new org.springmvc.service.impl.ReadDicomFileTagInfoimpl();
        File f = new File("C:\\Users\\TT\\Desktop\\H0002T1855619\\1.2.840.113704.1.111.5612.1498891796.1");
        File[] farr = f.listFiles();
        for(int i=0;i<3;i++){
            File[] tempF =farr[i].listFiles();
            for(File fas:tempF){
                a.getTagInfo(fas.getAbsolutePath());
            }
        }

        for ( Map.Entry<String, List<String>> entry : a.getFilenameMap().entrySet()) {

            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

        }
    }
    @Override
    public void  getTagInfo(String filePath)  {
        try{
            File src = new File(filePath);
            Attributes attr1 = this.loadDicomObject(src);
            byte[] byteAcceNum = attr1.getBytes(Tag.AccessionNumber);
            byte[] bytePatientID = attr1.getBytes(Tag.PatientID);
            byte[] byteModality = attr1.getBytes(Tag.Modality);
            byte[] byteUID = attr1.getBytes(Tag.StudyInstanceUID);
            byte[] byteName = attr1.getBytes(Tag.PatientName);
            byte[] byteSeriesInstanceUID= attr1.getBytes(Tag.SeriesInstanceUID);
            //byte[] byteSeriesDiscript = attr1.getBytes(Tag.SeriesDescription);
            byte[] byteStudyDate = attr1.getBytes(Tag.StudyDate);
            byte[] byteInstanceNumber = attr1.getBytes(Tag.InstanceNumber);
            String patientBirth = new String(attr1.getBytes(Tag.PatientBirthDate),"gb18030").toString().trim();
            String patientSex = new String( attr1.getBytes(Tag.PatientSex) , "gb18030").toString().trim();
            String patientAge = new String( attr1.getBytes(Tag.PatientAge) , "gb18030").toString().trim();
            this.birth = patientBirth;
            this.sex = patientSex;
            this.age = patientAge;
            this.studyDate = new String(byteStudyDate , "gb18030").toString().trim();
            String accessionNtemp = new String(byteAcceNum,"gb18030");
            if(accessionNtemp != null && !accessionNtemp.equals(""))
            {
                this.accessionN = accessionNtemp;
            }
            String patientIdtemp = new String(bytePatientID,"gb18030");
            if(patientIdtemp != null && !patientIdtemp.equals("")) {
                this.patientId = patientIdtemp;
            }
            String modalitytemp = new String(byteModality , "gb18030");
            if(modalitytemp != null && !modalitytemp.equals(""))
            {
                this.modality = modalitytemp;
            }
            String name_temp = new String(byteName,"gb18030");
            if(name_temp !=null && !name_temp.equals(""))
            {
                this.patName = name_temp;
            }
            //count  每个series有几幅图像
            this.SeriesUID= new String(byteSeriesInstanceUID,"gb18030").toString().trim();
            //series 和 filename 关联
            if(this.filenameMap.containsKey(this.SeriesUID)){
                this.filenameMap.get(this.SeriesUID).add(src.getName());
            }else{
                List<String> l = new ArrayList<>();
                l.add(src.getName());
                this.filenameMap.put(this.SeriesUID,l);
            }
            //图像排序
            String instancenum = new String(byteInstanceNumber ,"gb18030").toString();
            this.fileInstanceNumMap.put(src.getName() , instancenum );
            //每个series 的  seriesDiscripttemp
            //String seriesDiscripttemp = new String(byteSeriesDiscript , "gb18030").toString().trim();
           /* if(!this.seriesDiMap.containsKey(this.SeriesUID))
            {
                this.seriesDiMap.put(this.SeriesUID , seriesDiscripttemp);
            }*/

            // study_count, 每个study 有多少幅图像
            this.StudyUID = new String(byteUID,"gb18030").toString().trim();

            if(StudyUID == null||StudyUID == ""){
                this.StudyUID = new File(filePath).getParentFile().getParentFile().getName();
            }



            this.study_countMap.put(this.StudyUID,this.study_countMap.getOrDefault(this.StudyUID,0) + 1);
            // study 和 series 关联
            String series_str = new String(byteSeriesInstanceUID , "gb18030").toString().trim();
            if(this.SeriesMap.containsKey(this.StudyUID)){
                List<String> list = this.SeriesMap.get(this.StudyUID);
                if(!list.contains(series_str)){
                    list.add(series_str);
                }
            }else{
                List<String> l = new ArrayList<>();
                l.add(series_str);
                this.SeriesMap.put(this.StudyUID,l);
            }
            attr1.clear();
        }catch (Exception E){
            E.printStackTrace();
        }

    }





    @Override
    public String getSex() {
        return sex;
    }

    @Override
    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String getModality() {
        return modality;
    }

    @Override
    public void setModality(String modality) {
        this.modality = modality;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getBirth() {
        return birth;
    }

    @Override
    public void setBirth(String birth) {
        this.birth = birth;
    }

    @Override
    public String getAge() {
        return age;
    }

    @Override
    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String getStudyDate() {
        return studyDate;
    }

    @Override
    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }

    @Override
    public String getAccessionN() {
        return accessionN;
    }

    @Override
    public void setAccessionN(String accessionN) {
        this.accessionN = accessionN;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    @Override
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public String getPatName() {
        return patName;
    }

    @Override
    public void setPatName(String patName) {
        patName = patName;
    }

    @Override
    public String getStudyUID() {
        return StudyUID;
    }

    @Override
    public void setStudyUID(String studyUID) {
        StudyUID = studyUID;
    }

    @Override
    public String getSeriesUID() {
        return SeriesUID;
    }

    @Override
    public void setSeriesUID(String seriesUID) {
        SeriesUID = seriesUID;
    }

    @Override
    public Map<String, List<String>> getFilenameMap() {
        return filenameMap;
    }

    @Override
    public void setFilenameMap(Map<String, List<String>> filenameMap) {
        this.filenameMap = filenameMap;
    }

    @Override
    public Map<String, Integer> getStudy_countMap() {
        return study_countMap;
    }

    @Override
    public void setStudy_countMap(Map<String, Integer> study_countMap) {
        this.study_countMap = study_countMap;
    }

    @Override
    public Map<String, List<String>> getSeriesMap() {
        return SeriesMap;
    }

    @Override
    public void setSeriesMap(Map<String, List<String>> seriesMap) {
        SeriesMap = seriesMap;
    }

    @Override
    public Map<String, String> getSeriesDiMap() {
        return seriesDiMap;
    }

    @Override
    public void setSeriesDiMap(Map<String, String> seriesDiMap) {
        this.seriesDiMap = seriesDiMap;
    }

    @Override
    public Map<String, String> getFileInstanceNumMap() {
        return fileInstanceNumMap;
    }

    @Override
    public void setFileInstanceNumMap(Map<String, String> fileInstanceNumMap) {
        this.fileInstanceNumMap = fileInstanceNumMap;
    }

}
