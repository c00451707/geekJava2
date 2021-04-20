package springPackage.homework05;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Data
@ToString
public class Halter07 {
    private long id=7777777;

    private String name7="Halter07";

    private String dream7;

    private int perseverance7=9;

    public static class InnerClass {
        private int id = 0;
        private String name = "hahaha";

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
