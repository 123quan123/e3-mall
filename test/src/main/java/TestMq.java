import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;

/**
 * @author quan
 * @create 2020-05-10 10:28
 */
public class TestMq {

    @Test
    public void testQueueProduder() throws JMSException {
        System.out.println("start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp:/192.168.41.128:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("test-queue");
        MessageProducer messageProducer = session.createProducer(queue);
//        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
//        textMessage.setText("hello-test");
        TextMessage textMessage = session.createTextMessage("hello-test");

        messageProducer.send(textMessage);
        messageProducer.close();
        session.close();
        connection.close();
    }
}
