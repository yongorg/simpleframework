package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author zheng yong
 * @date 2022/5/2、18:52
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE) //私有无参构造器
public class BeanContainer {

    /**
     * 存放所有配置标记的目标对象的Map
     **/
    private final ConcurrentHashMap<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 加载Bean的注解列表
     **/
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION =
            Arrays.asList(Component.class, Repository.class, Service.class, Controller.class);

    private boolean loaded = false;


    /**
     * 获取bean容器实例
     * （枚举实现单例：防止反射、序列化攻击）
     *
     * @return: org.simpleframework.org.simpleframework.core.BeanContainer
     **/
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;

        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * 扫描加载所有Bean
     *
     * @author: yong.zheng
     * @date: 2022/5/2 19:06
     **/
    public synchronized void loadBeans(String packgeName) {
        if (isLoaded()) {
            log.warn("BeanContainer has been leaded.");
            return;
        }

        // 加载类
        Optional.ofNullable(ClassUtil.extractPackageClass(packgeName))
                .ifPresentOrElse(
                        classes -> classes.forEach(this::putBeanMap),
                        () -> log.warn("extract nothing from packageName {}", packgeName)
                );

        loaded = true;
    }

    /**
     * 判断类是否是标记注解
     **/
    private void putBeanMap(Class<?> clazz) {
        BEAN_ANNOTATION.forEach(
                annotation -> {
                    if (clazz.isAnnotationPresent(annotation))
                        beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
        );
    }


    // 容器的增删改查

    /**
     * 新增bean实例
     */
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    /**
     * 删除bean实例
     **/
    public Object removeBean(Class<?> clazz) {
        return beanMap.remove(clazz);
    }


    /**
     * 获取bean实例
     **/
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 获取容器中管理所有的class对象集合
     **/
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }


    /**
     * 获取容器中管理所有的bean
     **/
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选class对象集合
     * @Param :annotationClass: 注解class对象
     * @return: java.util.Set<java.lang.Class < ?>>
     **/
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotationClass) {
        return getClasses()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotationClass))
                .collect(Collectors.toSet());
    }
}
