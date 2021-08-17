import bean.Config;
import bean.Eqp;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import model.Client;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        FileReader fileReader = new FileReader("e:\\eagle.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        while (null != (line = bufferedReader.readLine())) {
            String[] split = line.split(",");
            if (split.length == 2) {
                Client client = new Client();
                client.setId(split[1]);
                client.setName(split[1]);
                client.setIp(split[0]);
                client.setGroup("eagle");
                clients.add(client);
                System.out.println(split[0] + ":" + split[1]);
            }
        }
        EasyExcel.write("d:\\eagle.xlsx", Client.class).sheet("模板").doWrite(clients);
    }

    @Test
    public void genAutoServerConfigFile() throws IOException {
        ArrayList<Client> clients = new ArrayList<>();
        EasyExcel.read("d:\\机台.xlsx", Client.class, new AnalysisEventListener<Client>() {

            @Override
            public void invoke(Client client, AnalysisContext analysisContext) {
                System.out.println(client);
                clients.add(client);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("所有数据解析完成！");
            }
        }).sheet().doRead();

        for (Client client : clients) {
            client.setId(client.getName());
            if (client.getGroup().equals("ni")) {
                client.setGroup("ni");
            }
            if (client.getGroup().equals("ag93k")) {
                client.setGroup("ag93k");
            }
            if (client.getGroup().equals("eagle")) {
                client.setGroup("eagle");
            }
        }

        String configJson = JSON.toJSONString(clients);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("e:\\clients.json"));
        bufferedWriter.write(configJson);
    }

    @Test
    public void genEAPClientConfigFile() throws IOException {
        ArrayList<Client> clients = new ArrayList<>();
        ArrayList<Config> configs = new ArrayList<>();
        EasyExcel.read("d:\\机台.xlsx", Client.class, new AnalysisEventListener<Client>() {

            @Override
            public void invoke(Client client, AnalysisContext analysisContext) {
                System.out.println(client);
                clients.add(client);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("所有数据解析完成！");
            }
        }).sheet().doRead();
        for (Client client : clients) {
            Config config = new Config();
            config.equipmentName = client.getName();
            config.modelName = client.getGroup();
            config.mqAddress = "10.65.202.211";
            config.devMode = "true";
            if (client.getGroup().equals("ni")) {
                config.workPath = "C:\\NTR\\data";
            }
            if (client.getGroup().equals("ag93k")) {
                config.workPath = "/NTR/data";
            }
            if (client.getGroup().equals("eagle")) {
                config.workPath = "C:\\ets\\" + client.getName();
            }
            configs.add(config);
        }

        String configJson = JSON.toJSONString(configs);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("e:\\config.json"));
        bufferedWriter.write(configJson);
    }

    @Test
    public void genEAPServerConfigFile() throws IOException {
        ArrayList<Client> clients = new ArrayList<>();
        ArrayList<Eqp> eqps = new ArrayList<>();
        EasyExcel.read("d:\\机台.xlsx", Client.class, new AnalysisEventListener<Client>() {

            @Override
            public void invoke(Client client, AnalysisContext analysisContext) {
                System.out.println(client);
                clients.add(client);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("所有数据解析完成！");
            }
        }).sheet().doRead();
        for (Client client : clients) {
            Eqp eqp = new Eqp();

            if (client.getGroup().equals("ni")) {
                eqp.ModelName = "Ni.Eagle";
            }
            if (client.getGroup().equals("ag93k")) {
                eqp.ModelName = "Agilent.Ag93k";
            }
            if (client.getGroup().equals("eagle")) {
                eqp.ModelName = "Ni.Eagle";
            }

            eqp.Name = client.getName();

            eqps.add(eqp);
        }

        String eqpsJson = JSON.toJSONString(eqps);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("e:\\equipments.json"));
        bufferedWriter.write(eqpsJson);
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
