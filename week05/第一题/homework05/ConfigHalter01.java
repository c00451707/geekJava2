package springPackage.homework05;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springPackage.homework0501.Halter03;

//@Component
@Configuration
public class ConfigHalter01 {
//    @Autowired
//    Halter04 halter04;

    @Bean
    public Halter01 getHalter01() {
        return new Halter01();
    }

    /**
     * type 'springPackage.homework05.Halter02' available: expected single matching bean but found 2: halter01,getHalter02
     * @return
     */
//    @Bean
//    public Halter02 getHalter02() {
//        return new Halter02();
//    }

    @Bean
    public Halter03 getHalter03() {
        return new Halter03();
    }
}
