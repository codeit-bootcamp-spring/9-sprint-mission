package com.example.hellojar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
public class JavaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(JavaApplication.class, args);

        // 1. 등록된 모든 Bean 이름 확인
        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        System.out.println("Total beans: " + beanNames.length);
        System.out.println("Total beans: " + Arrays.toString(beanNames));
        // 필요하면 전체 목록 출력
        // Arrays.stream(beanNames).forEach(System.out::println);

        // 2. 특정 타입의 Bean 찾기 (DataSource)
        Map<String, DataSource> dataSources = context.getBeansOfType(DataSource.class);
        System.out.println("DataSources: " + dataSources);

        // 3. Bean 생성 시점/스코프 확인 (싱글톤 여부)
        String beanToCheck = "userService"; // 존재하는 빈 이름으로 바꾸세요
        try {
            boolean isSingleton  context.isSingleton(beanToCheck);
            System.out.println(beanToCheck + " is singleton: " + isSingleton);
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("No such bean: " + beanToCheck);
        }

        // 4. 현재 활성 프로파일 확인
        Environment env = context.getEnvironment();
        String[] profiles = env.getActiveProfiles();
        System.out.println("Active profiles: " + Arrays.toString(profiles));

        // 5. 프로퍼티 값 확인
        String port = env.getProperty("server.port");
        System.out.println("server.port = " + port);

        // 6. 자동 설정 적용 여부의 간접 확인(대표 빈 존재 확인)
        boolean hasDataSource = context.containsBean("dataSource");
        System.out.println("has 'dataSource' bean: " + hasDataSource);

        // 7. Bean 의존 관계 확인
        String targetBean = "userController"; // 존재하는 빈 이름으로 바꾸세요
        try {
            String[] deps = context.getBeanFactory().getDependenciesForBean(targetBean);
            System.out.println("Dependencies of " + targetBean + ": " + Arrays.toString(deps));
        } catch (NoSuchBeanDefinitionException e) {
            System.out.println("No such bean: " + targetBean);
        }
    }
}