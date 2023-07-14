package com.example.petshopuser.utils;

import org.lionsoul.ip2region.xdb.Searcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class IpUtil {
    public static String getIpAddr(HttpServletRequest request) {
        // 优先取 X-Real-IP
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ip))
            {
                ip = "unknown";
            }
        }
        if ("unknown".equalsIgnoreCase(ip)){
            ip = "unknown";
            return ip;
        }
        int index = ip.indexOf(',');
        if (index >= 0){
            ip = ip.substring(0, index);
        }
        System.out.println(getCityInfo(ip));
        return getCityInfo(ip);
    }
    public static String getCityInfo(String ip){
        // 1、创建 searcher 对象
        //String dbPath = "springboot/src/main/resources/City/ip2region.xdb";
        String dbPath = "src/main/resources/City/ip2region.xdb";//服务器
        Searcher searcher;
        try {
            searcher = Searcher.newWithFileOnly(dbPath);
        } catch (IOException e) {
            System.out.printf("failed to create searcher with `%s`: %s\n", dbPath, e);
            return null;
        }
        // 2、查询
        String region = null;
        try {
            long sTime = System.nanoTime();
            region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - sTime);
            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
        } catch (Exception e) {
            System.out.printf("failed to search(%s): %s\n", ip, e);
        }
        return region;
    }
}
