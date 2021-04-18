package springPackage.homework05;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springPackage.homework0501.Halter03;

public class week05 {
    @Autowired
    Halter01 halter001;

    public static void main(String[] args) {
//        new week05().printObj();
        ApplicationContext context = new ClassPathXmlApplicationContext("homeworkContext.xml");
        Halter01 halter01 = context.getBean(Halter01.class);
        System.out.println(halter01.toString());
        Halter02 halter02 = (Halter02)context.getBean("halter01");
        System.out.println(halter02.toString());
        Halter03 halter03 = context.getBean(Halter03.class);
        System.out.println(halter03.toString());

        Halter04 halter04 = context.getBean(Halter04.class);
        System.out.println(halter04.toString());
    }

}
