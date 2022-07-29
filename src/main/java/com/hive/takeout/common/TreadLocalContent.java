package com.hive.takeout.common;

public class TreadLocalContent {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void set(Long obj){
        threadLocal.set(obj);
    }

    public static Long get(){
        return threadLocal.get();
    }
}
