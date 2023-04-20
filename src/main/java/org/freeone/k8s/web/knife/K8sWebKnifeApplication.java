package org.freeone.k8s.web.knife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@ComponentScan({"io.kubernetes.client.extended.kubectl","org.freeone.k8s.web.knife"})
@SpringBootApplication(
//        exclude = DataSourceAutoConfiguration.class
)
public class K8sWebKnifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sWebKnifeApplication.class, args);
        System.out.println("===================== application startup completed =======================");
    }


}
