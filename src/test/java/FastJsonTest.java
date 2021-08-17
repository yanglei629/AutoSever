import com.alibaba.fastjson.JSON;
import model.Client;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FastJsonTest {
    @Test
    public void test() throws IOException {
        String clients = IOUtils.toString(new FileInputStream("src/main/resources/clients.json"), "utf-8");
        List<Client> clients1 = JSON.parseArray(clients, Client.class);
        System.out.println(clients1);
    }
}
