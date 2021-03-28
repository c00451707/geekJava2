import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NIOApplicationDemo {
    static void getPrintWriter(Socket socket, String s) throws IOException {
        //读取客户端发来的消息
//        Scanner scan = new Scanner(socket.getInputStream());
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println("Content-Type:text/html;charset=utf-8");
//        s = s + scan.nextLine();
        printWriter.println("Content-Length:" + s.getBytes().length);
        printWriter.println();
        printWriter.write(s);
        printWriter.close();

    }
}
