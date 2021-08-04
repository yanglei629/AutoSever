import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EasyExcelTest {

    @Test
    public void export() throws IOException {
        String clients = IOUtils.toString(EasyExcelTest.class.getResource("clients.json"), "utf-8");
        List<Client> clients1 = JSON.parseArray(clients, Client.class);
        EasyExcel.write("d:\\test.xlsx", Client.class).sheet("模板").doWrite(clients1);
    }


    @Test
    public void export2() throws IOException {
        List<Client> clients = new ArrayList<>();
        FileReader fileReader = new FileReader("e:\\tni.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        while (null != (line = bufferedReader.readLine())) {
            //System.out.println(line);
            String[] split = line.split(",");
            if (split.length == 2) {
                Client client = new Client();
                client.setID(split[1]);
                client.setName(split[1]);
                client.setIp(split[0]);
                client.setGroup("ni");
                clients.add(client);
                System.out.println(split[0] + ":" + split[1]);
            }
        }
        EasyExcel.write("d:\\test1.xlsx", Client.class).sheet("模板").doWrite(clients);
    }


    @Test
    public void importFormExcel() throws InterruptedException {
        EasyExcel.read("d:\\test1.xlsx", Client.class, new AnalysisEventListener<Client>() {

            @Override
            public void invoke(Client client, AnalysisContext analysisContext) {
                System.out.println(client);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("所有数据解析完成！");
            }
        }).sheet().doRead();

        Thread.sleep(10000);
    }

    public static void main(String[] args) {

    }
}
