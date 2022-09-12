package com.xiaobai.redismulticache.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * className: JSONUtil
 * description:
 * author: xiaopangio
 * date: 2022/9/12 18:46
 * version: 1.0
 */
@Component
public class JSONUtil  {
     private static final ObjectMapper objectMapper=new ObjectMapper();
     public static String toJSONString(Object object){
         try {
             return objectMapper.writeValueAsString(object);
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }
     public static <T> T parseObject(String json,Class<T> clazz){
         try {
             return objectMapper.readValue(json,clazz);
         }catch (Exception e){
             e.printStackTrace();
         }
         return null;
     }
}
