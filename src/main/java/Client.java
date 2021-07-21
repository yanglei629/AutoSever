import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Client {
    public static final Logger logger = LogManager.getLogger(Client.class);

    private String ID;
    private String name;
    private String ip;
    private int port = 9000;
    private String group;
    private Status status = Status.OFFLINE;

    public void queryStatus() {
        try {
            //http://localhost:8080/status
            HttpClientUtil.doGet("http://" + this.ip + ":" + (0 == this.port ? "8080" : this.port) + "/" + "status");
        } catch (ConnectTimeoutException e) {
            logger.warn(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", group='" + group + '\'' +
                ", status=" + status +
                '}';
    }
}
