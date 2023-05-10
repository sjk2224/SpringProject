package com.co.kr.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //delete
    public HashMap<String, Object> mbRemove(Map<String, String> map);
    
}