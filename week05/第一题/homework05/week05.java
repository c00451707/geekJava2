package springPackage.homework05;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import springPackage.homework05.factory.DemoBean;
import springPackage.homework05.factory.DemoBean01;
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

        // xml import 导入 Bean
        Halter06 halter06 = (Halter06)context.getBean("simple-bean");
        System.out.println(halter06.toString());

        // 构造器方式创建Bean
        Halter07 halter07 = (Halter07)context.getBean("springPackage.homework05.Halter07#0");
        System.out.println(halter07.toString());

        // 内部静态类Bean
        Halter07.InnerClass bean = (Halter07.InnerClass)context.getBean("springPackage.homework05.Halter07$InnerClass#0");
        System.out.println(bean.getId() + "------" +bean.getName());

        // 静态工厂方式创建Bean
        DemoBean bean1 = (DemoBean)context.getBean("springPackage.homework05.factory.BeanFactory#0");
        System.out.println(bean1.toString());

        // 通过实例工厂方法创建的bean
        DemoBean01 bean2 = (DemoBean01)context.getBean("beanInstanceFactory$created#0");
        System.out.println(bean2.toString());

        // ... ...还有很多其他方式

        
    }

}
