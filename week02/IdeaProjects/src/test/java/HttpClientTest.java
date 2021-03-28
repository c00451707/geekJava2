import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpClientTest {
    @Test
    public void test_do_get_test_one() {
        HttpClient httpClient = new HttpClient();
        String response = httpClient.doGetTestOne();
        assertEquals("hello nio1",response);
    }
}