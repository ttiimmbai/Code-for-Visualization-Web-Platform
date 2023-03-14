package com.example.demo.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Tim
 * @since 2021-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DicomList extends Model<DicomList> {

    private static final long serialVersionUID = 1L;

    private String id;

    private String patientname;

    private String description;

    private LocalDateTime gmtDate;

    private LocalDateTime gmtModified;

    private String seriesnum;

    private String devtype;

    private String patientid;

    private String hosid;

    private Integer status;

    private String position;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
