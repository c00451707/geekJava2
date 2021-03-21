import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

public class HelloClassLoader extends ClassLoader {
    public static void main(String[] args) {
        byte[] aa = getFileBytes("D:\\IdeaProjects\\ClassLoaderDemo\\src\\main\\java\\Hello.xlass");
        assert aa != null;
        byte[] bb = new byte[aa.length];
        for (int i = 0; i < aa.length; i++) {
            bb[i] = (byte) (255 - aa[i]);
        }

        try {
            Class<?> clazz = new HelloClassLoader().findClass(bb);
            Method method = clazz.getMethod("hello");
            method.invoke(clazz.newInstance());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //读取文件到byte[]
    private static byte[] getFileBytes(String file) {
        try {
            File f = new File(file);
            int length = (int) f.length();
            byte[] data = new byte[length];
            int n = new FileInputStream(f).read(data);
            if (n == -1) {
                return null;
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String helloBase64 = "cHVibGljIGNsYXNzIEhlbGxvIHsKICAgIHN0YXRpYyB7CiAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJIZWxsbyBDbGFzcyBJbml0aWFsaXplZCBCeSBIdXN0ZXIhIik7CiAgICB9Cn0==";
        byte[] bytes = decode(helloBase64);
        return defineClass(name,bytes,0,bytes.length);
    }

    protected Class<?> findClass(byte[] bytes) throws ClassNotFoundException {
        return defineClass("Hello",bytes,0,bytes.length);
    }

    private byte[] decode(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}
