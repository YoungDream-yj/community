package com.nowcoder.community.service;

import com.nowcoder.community.dao.MiaoshaCourseMapper;
import com.nowcoder.community.entity.MiaoshaCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiaoshaCourseService {

    @Autowired
    private MiaoshaCourseMapper miaoshaCourseMapper;

    public MiaoshaCourse findMiaoshaCourseById(int id){
        return miaoshaCourseMapper.selectMiaoshaCourseById(id);
    }

    public List<MiaoshaCourse> findMiaoshaCourses(int offset, int limit){
        return miaoshaCourseMapper.selectMiaoshaCourses(offset, limit);
    }

    public int findMiaoshaCourseRows(){
        return miaoshaCourseMapper.selectMiaoshaCourseRows();
    }

    public int reduceStock(int id){
        return miaoshaCourseMapper.reduceStock(id);
    }

}
