package br.edu.ifg.sisd.market;

import java.util.Calendar;
import java.util.Properties;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class StockServer {

	public static final int MIN_EVENT_TIME = 1;
	public static final int MAX_EVENT_TIME = 10;
	public static final int MAX_STOCKS = 10;
	public static final int MAX_PRICE = 1000;

	private TopicPublisher topicPub;
	private TopicSession topicSess;
	private Random random;

	private String[] companies = { "Google", "Microsoft", "Amazon", "Yahoo", "HP" };

	public StockServer() throws NamingException, JMSException {
		init();
	}

	private void init() throws NamingException, JMSException {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
		env.put("topic.topicSampleTopic", "StockMarket");

		// get the initial context
		InitialContext ctx = new InitialContext(env);

		// lookup the topic connection factory
		TopicConnectionFactory topicFactory = (TopicConnectionFactory) ctx.lookup("TopicConnectionFactory");

		// create a topic connection
		TopicConnection topicConn = topicFactory.createTopicConnection();

		// create a topic session
		topicSess = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		// lookup the topic object
		Topic topic = (Topic) ctx.lookup("topicSampleTopic");

		// create a topic publisher
		topicPub = topicSess.createPublisher(topic);

		random = new Random(System.currentTimeMillis());
	}

	public void start() throws JMSException, InterruptedException {
		while (true) {
			int sleepTime = MIN_EVENT_TIME + random.nextInt(MAX_EVENT_TIME);

			Thread.sleep(sleepTime * 1000);
			MapMessage message = topicSess.createMapMessage();

			int stocks = random.nextInt(MAX_STOCKS) + 1;

			for (int i = 0; i < stocks; i++) {
				int price = 1 + random.nextInt(MAX_PRICE);
				int companyIdx = random.nextInt(companies.length);
				message.setInt(companies[companyIdx], price);
			}

			// send the message
			topicPub.publish(message);

			String hour = String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			String min = String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE));
			String sec = String.format("%02d", Calendar.getInstance().get(Calendar.SECOND));

			System.out.println("[" + hour + ":" + min + ":" + sec + "] message sent with " + stocks + " stocks");
		}
	}

	public static void main(String[] args) {
		StockServer stockServer;
		try {
			stockServer = new StockServer();
			stockServer.start();
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage());
		}

	}
}
