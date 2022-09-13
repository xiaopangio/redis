package com.xiaobai.redismulticache.filter;

import com.xiaobai.redismulticache.common.RedisUtil;
import com.xiaobai.redismulticache.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * className: BloomFilter
 * description:
 * author: xiaopangio
 * date: 2022/9/13 9:25
 * version: 1.0
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/product/*")
public class BloomFilter implements Filter ,ApplicationContextAware{
    private float fpp = 0.01f;
    private int expectedInsertions = 1000000;
    private int numHashFunctions =0;
    private int bitSize = 0;
    private int[] hash;
    private static final String BLOOM_FILTER_KEY = "bloom_filter_key";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    public void add(String key) {
        getHash(key);
        for (int i = 0; i < numHashFunctions; i++) {
            RedisUtil.setBitmap(BLOOM_FILTER_KEY, hash[i], true);
            String countKey=BLOOM_FILTER_KEY+"_"+hash[i];
            int bitMapCount = RedisUtil.getBitMapCount(countKey);
            RedisUtil.setBitmap(countKey, bitMapCount, true);
        }
    }
    public void delete(String key){
        getHash(key);
        for (int i = 0; i < numHashFunctions; i++) {
            String countKey=BLOOM_FILTER_KEY+"_"+hash[i];
            int bitMapCount = RedisUtil.getBitMapCount(countKey);
            if(bitMapCount==0){
                RedisUtil.deleteBitMap(BLOOM_FILTER_KEY,hash[i]);
                RedisUtil.delete(countKey);
                return;
            }
            RedisUtil.deleteBitMap(countKey,bitMapCount-1);
        }
    }
    private void getHash(String key){
        for (int i = 0; i <numHashFunctions ; i++) {
            hash[i]=(key+i).hashCode()*i%bitSize;
        }
        log.info("hash"+key+":{}",hash);
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        String productId = httpServletRequest.getRequestURI().split("/")[3];
        getHash(productId);
        log.info("hash"+productId+":{}",hash);
        for (int i = 0; i < numHashFunctions; i++) {
            if(!RedisUtil.getBitmap(BLOOM_FILTER_KEY,hash[i])){
                log.info("不存在");
                Result result = new Result(404, "不存在");
                servletResponse.setContentType("application/json;charset=UTF-8");
                servletResponse.getWriter().write(result.toString());
                return;
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public void setFpp(float fpp) {
        this.fpp = fpp;
    }

    public void setExpectedInsertions(int expectedInsertions) {
        this.expectedInsertions = expectedInsertions;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        bitSize=(int)(-1*expectedInsertions*Math.log(fpp)/Math.pow(Math.log(2),2));
        numHashFunctions=(int)Math.ceil(((float)bitSize / expectedInsertions) * Math.log(2));
        hash=new int[numHashFunctions];
    }
}
