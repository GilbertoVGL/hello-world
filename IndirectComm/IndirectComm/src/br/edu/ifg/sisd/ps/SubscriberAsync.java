package br.edu.ifg.sisd.ps;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SubscriberAsync {

	public static void main(String args[]) {
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

			// create a topic subscriber
			TopicSubscriber topicSub = topicSess.createSubscriber(topic);

			// set listener to receive a message
			topicSub.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message message) {
					try {
						TextMessage text = (TextMessage) message;

						// print the message
						System.out.println("received: " + text.getText());

						// close the topic connection
						topicConn.close();
					} catch (JMSException e) {
						System.err.println(e.getMessage());
					}

				}
			});

			// start the connection
			topicConn.start();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
