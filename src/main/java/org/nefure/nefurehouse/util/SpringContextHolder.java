package org.nefure.nefurehouse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 用与获取spring上下文的工具类，能够通过此工具类获取spring容器里的bean对象
 * @author nefure
 * @date 2022/3/18 12:46
 */
@Service
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    public static String ROOT_PATH = Objects.requireNonNull(SpringContextHolder.class.getResource("/")).getPath();

    private static ApplicationContext applicationContext;

    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    public static void clearHolder(){
        if (logger.isDebugEnabled()) {
            logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        }
        logger.info("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        applicationContext = null;
    }

    public static<T> Map<String,T> getBeansOfType(Class<T> aClass){
        return applicationContext.getBeansOfType(aClass);
    }

    public static<T> T getBean(String name) {
        return (T)applicationContext.getBean(name);
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
