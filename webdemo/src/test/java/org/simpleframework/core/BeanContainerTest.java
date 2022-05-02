package org.simpleframework.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Service;

import java.util.Set;

/**
 * @author zheng yong
 * @date 2022/5/2、21:12
 */
public class BeanContainerTest {

    private static BeanContainer beanContainer;

    @BeforeAll
    static void init() {
        beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("org.yongz");
    }


    @Test
    public void testLoadClass(){
        beanContainer.loadBeans("org.yongz");
    }

    @Test
    public void getClassesByAnnotation(){
        Set<Class<?>> classesByAnnotation = beanContainer.getClassesByAnnotation(Controller.class);
        System.out.println(classesByAnnotation);
    }
}
