import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.MessageProperties;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@XmlRootElement
@XmlType(propOrder = {"service", "timestamp", "error", "status"})
public class Heartbeat {

    private static final String EXCHANGE_NAME = "controlroom_exchange";
    private static final String HOST = "10.2.160.9";
    private static final String SERVICE = "CRM";

    private String timestamp;
    private String error;
    private int status;

    public Heartbeat() throws Exception {
        this.timestamp = getCurrentTimestamp();
        this.status = isSalesforceAvailable() ? 1 : 0;
        this.error = this.status == 1 ? "No error" : "Error";
    }

    @XmlElement
    public String getService() {
        return SERVICE;
    }

    @XmlElement
    public String getTimestamp() {
        return timestamp;
    }

    @XmlElement
    public String getError() {
        return error;
    }

    @XmlElement
    public int getStatus() {
        return status;
    }

    public String createXML() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Heartbeat.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(this, stringWriter);

        String xmlString = stringWriter.toString();
        System.out.println(xmlString);
        return xmlString;
    }

    public void sendHeartbeat() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String xml = createXML();
            byte[] xmlBytes = xml.getBytes(MessageProperties.CONTENT_TYPE_XML);

            channel.basicPublish(EXCHANGE_NAME, "", null, xmlBytes);
            System.out.println("Heartbeat has been sent successfully");

        } catch (IOException | TimeoutException e) {
            System.err.println("Heartbeat was not sent due to error");
            e.printStackTrace();
        }
    }

    public static boolean isSalesforceAvailable() throws Exception {
        String endpoint = "https://erasmushogeschool7-dev-ed.develop.lightning.force.com/lightning/page/home";

        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Salesforce response code: " + responseCode);
            return responseCode == 200;

        } catch (IOException e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return false;
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static void main(String[] args) {
        try {
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.sendHeartbeat();
        } catch (Exception e) {
            System.err.println("Error in sending heartbeat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
