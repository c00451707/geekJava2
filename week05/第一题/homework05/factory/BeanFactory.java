package springPackage.homework05.factory;

public class BeanFactory {
    // 1. 创建实例化bean的静态工厂

    /**
     * 创建DemoBean的静态工厂方法
     */
    public static DemoBean createDemoBean() {
        return new DemoBean();
    }

    // 2. 创建实例化bean的实例工厂
    // package com.huawei.share.guidespring.beans

    /**
     * 创建DemoBean的实例工厂方法
     */
    public DemoBean01 createDemoBean01() {
        return new DemoBean01();
    }
}
