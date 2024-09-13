package com.example.demo;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

@RegisterReflectionForBinding(Cart.class)
@ImportRuntimeHints(DemoApplication.Hints.class)
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    static class Hints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.serialization().registerType(TypeReference.of(Cart.class));
            hints.reflection().registerType(TypeReference.of(MyConfig.class),
                    MemberCategory.values());
        }
    }
}


@Component
class Cart implements Serializable {

}

class BFPIAP implements BeanFactoryInitializationAotProcessor {

    @Override
    public BeanFactoryInitializationAotContribution processAheadOfTime(
            ConfigurableListableBeanFactory beanFactory) {

        var serializable = new ArrayList<String>();
        // 1. identify serializable beans
        for (var beanName : beanFactory.getBeanDefinitionNames()) {
            var beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (StringUtils.hasText(beanDefinition.getBeanClassName())) {
                try {
                    var clzz = Class.forName(beanDefinition.getBeanClassName());
                    if (Serializable.class.isAssignableFrom(clzz)) {
                        serializable.add(clzz.getName());
                        System.out.println("adding a hint for [" + clzz.getName() + "]");
                    }
                } catch (Throwable throwable) {
                    // 
                    System.out.println("ouch.");
                }
            }
        }

        // 2. contribute hints to the *-config.json

        return (ctx, code) -> {
            var runtimeHints = ctx.getRuntimeHints();
            runtimeHints.resources().registerPattern("images/my.favico");
            for (var s : serializable)
                runtimeHints.serialization().registerType(TypeReference.of(s));

            code.getMethods().add("myInit", builder -> builder.addCode("""
                    System.out.println("hello, world");
                    """));
        };
    }
}

class BFPP implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        for (var beanName : beanFactory.getBeanDefinitionNames()) {
            var bd = beanFactory.getBeanDefinition(beanName);
            System.out.println("bean class " + bd.getBeanClassName());
        }

    }
}

@Configuration
class MyConfig {

    @Bean
    static BFPIAP bfpiap() {
        return new BFPIAP();
    }

    @Bean
    static BFPP bfpp() {
        return new BFPP();
    }

    @Bean
    static BPP bpp() {
        return new BPP();
    }

}

@Configuration
@EnableConfigurationProperties(BootifulProperties.class)
class MyAutoConfig {


    @Bean
    @ConditionalOnProperty(value = "bootiful.runner", havingValue = "true")
    ApplicationRunner applicationRunner(BootifulProperties properties) {
        return args -> System.out.println("hello, auto config: " + properties.runner());
    }
}

@ConfigurationProperties(prefix = "bootiful")
record BootifulProperties(boolean runner) {
}

class BPP implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        System.out.println("inspecting [" + beanName + "]");
        return bean;
    }
}