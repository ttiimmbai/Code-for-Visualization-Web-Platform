package com.example.processingserver.service.impl;

import com.example.processingserver.entity.DicomList;
import com.example.processingserver.mapper.DicomListMapper;
import com.example.processingserver.service.DicomListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tim
 * @since 2021-11-25
 */
@Service
public class DicomListServiceImpl extends ServiceImpl<DicomListMapper, DicomList> implements DicomListService {

}
