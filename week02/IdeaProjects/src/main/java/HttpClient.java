import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpClient {
    /**
     * GET---无参测试
     * @date
     */
    public String doGetTestOne() {
        // 创建Get请求
        HttpGet httpGet = new HttpGet("http://localhost:8801/");
        String resultBody = null;
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上 HttpClient 与浏览器是不一样的)
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为 --> " + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为 -->" + responseEntity.getContentLength());
                resultBody = EntityUtils.toString(responseEntity);
                System.out.println("响应内容为 --> " + resultBody);
            }
        } catch (ParseException | IOException e) {
            System.out.println("error: 连接失败 "+e.getMessage());
        }
        return resultBody;
    }
}