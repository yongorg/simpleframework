package org.simpleframework.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Service;
import org.yongz.controller.TestController;
import org.yongz.service.ITestService;

import java.util.Set;

/**
 * @author zheng yong
 * @date 2022/5/2、21:12
 */
public class BeanContainerTest {

    private static BeanContainer beanContainer = BeanContainer.getInstance();

    @BeforeAll
    static void loadBeans() {
        beanContainer.loadBeans("org.yongz");
    }


    @Test
    public void testLoadClass() {
        beanContainer.loadBeans("org.yongz");
    }

    @Test
    public void getClassesByAnnotation() {
        Set<Class<?>> classesByAnnotation = beanContainer.getClassesByAnnotation(Controller.class);
        System.out.println(classesByAnnotation);
    }

    @Test
    public void getClassesBySuperClass() {
        Set<Class<?>> classesBySuperClass = beanContainer.getClassesBySuperClass(ITestService.class);
        System.out.println(classesBySuperClass);
    }


    @Test
    public void getBean() {
        TestController bean = beanContainer.getBean(TestController.class);
        TestController bean2 = beanContainer.getBean(TestController.class);
        System.out.println(bean);
        System.out.println(bean2);
    }
}
