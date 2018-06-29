package br.edu.ifg.sisd.ps;

import java.util.Properties;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Publisher {

	public static void main(String[] args) {
		try {
			Properties env = new Properties();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
			env.put("topic.topicSampleTopic", "MyNewTopic");

			// get the initial context
			InitialContext ctx = new InitialContext(env);

			// lookup the topic connection factory
			TopicConnectionFactory topicFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");

			// create a topic connection
			TopicConnection topicConn = topicFactory.createTopicConnection();

			// create a topic session
			TopicSession topicSess = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

			// lookup the topic object
			Topic topic = (Topic) ctx.lookup("topicSampleTopic");

			// create a topic publisher
			TopicPublisher topicPub = topicSess.createPublisher(topic);

			// create a simple message to say "Hello"
			TextMessage message = topicSess.createTextMessage("Hello");

			// send the message
			topicPub.publish(message);

			System.out.println("sent: " + message.getText());

			// close the topic connection
			topicConn.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
