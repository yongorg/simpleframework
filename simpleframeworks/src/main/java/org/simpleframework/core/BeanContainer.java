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
     * @return: org.simpleframework.core.BeanContainer
     **/
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;

        private final BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Bean实例数量
     *
     * @return 数量
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 扫描加载所有Bean
     *
     * @author yong.zheng
     * @date: 2022/5/2 19:06
     **/
    public synchronized void loadBeans(String packageName) {
        if (isLoaded()) {
            log.warn("BeanContainer has been loaded.");
            return;
        }

        // 加载类
        Optional.ofNullable(ClassUtil.extractPackageClass(packageName))
                .ifPresentOrElse(
                        classes -> classes.forEach(this::putBeanMap),
                        () -> log.warn("extract nothing from packageName {}", packageName)
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
    public <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz);
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
     *
     * @Param :annotationClass: 注解class对象
     * @return: java.util.Set<java.lang.Class < ?>>
     **/
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotationClass) {
        return getClasses()
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(annotationClass))
                .collect(Collectors.toSet());
    }


    /**
     * 根据父类或接口筛选class对象集合，不包括其本身
     *
     * @Param :annotationClass: 注解class对象
     * @return: java.util.Set<java.lang.Class < ?>>
     **/
    public Set<Class<?>> getClassesBySuperClass(Class<?> interfaceOrSuperClass) {
        return getClasses()
                .stream()
                .filter(clazz -> interfaceOrSuperClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrSuperClass))
                .collect(Collectors.toSet());
    }
}
