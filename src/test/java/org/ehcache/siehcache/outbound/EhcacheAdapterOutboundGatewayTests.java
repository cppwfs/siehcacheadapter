package org.ehcache.siehcache.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.ehcache.siehcache.EhcacheAdapterHeaders;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.test.util.TestUtils;

public class EhcacheAdapterOutboundGatewayTests {
	private ConfigurableApplicationContext context;

	private EventDrivenConsumer consumer;
	@Test
	public void testCreateNewManager() {
		setUp("EhcacheAdapterOutboundGatewayTests.xml", getClass(),
				"ehcacheadapterOutboundWithCacheManager");
		final EhcacheAdapterOutboundGateway ehcacheadapterOutboundGateway = TestUtils
				.getPropertyValue(this.consumer, "handler",
						EhcacheAdapterOutboundGateway.class);
		assertNotNull(ehcacheadapterOutboundGateway.mgr);
		ehcacheadapterOutboundGateway.getDefaultCacheManager();
		CacheManager mgr = ehcacheadapterOutboundGateway.mgr;
		assertNotNull(ehcacheadapterOutboundGateway.mgr);
		assertEquals(mgr,ehcacheadapterOutboundGateway.getDefaultCacheManager());
		
		assertEquals("mycache", ehcacheadapterOutboundGateway.getCache());	}

	@Test
	public void testDefaults() {
		//fail("Not yet implemented");
		setUp("EhcacheAdapterOutboundGatewayTests.xml", getClass(),
				"defaultSettingGateway");
		final EhcacheAdapterOutboundGateway ehcacheadapterOutboundGateway = TestUtils
				.getPropertyValue(this.consumer, "handler",
						EhcacheAdapterOutboundGateway.class);
		assertNotNull(ehcacheadapterOutboundGateway.mgr);
	}
	@Test
	public void testBadEhcacheXML() {
		System.out.println("Starting");
		setUp("EhcacheAdapterOutboundGatewayTests.xml", getClass(),
				"badSettingGateway");
		System.out.println("setupComplete");
		final EhcacheAdapterOutboundGateway ehcacheadapterOutboundGateway = TestUtils
				.getPropertyValue(this.consumer, "handler",
						EhcacheAdapterOutboundGateway.class);
		assertNotNull(ehcacheadapterOutboundGateway.mgr);
		System.out.println("Ending");
	}
	@Test
	public void testCreateCache() {
	}
	@Test
	public void testHandleRequestMessageMessageOfQ() {
		//Test the Put
		Element message = new Element("1","1 value");
		 Message msg = MessageBuilder.withPayload(message).setHeader(EhcacheAdapterHeaders.COMMAND,EhcacheAdapterHeaders.PUT)
			.setHeader("replyChannel", "endChannel").build();
		 setUp("EhcacheAdapterOutboundGatewayTests.xml", getClass(),
					"ehcacheadapterOutboundWithCacheManager");
			final EhcacheAdapterOutboundGateway ehcacheadapterOutboundGateway = TestUtils
					.getPropertyValue(this.consumer, "handler",
							EhcacheAdapterOutboundGateway.class);
			ehcacheadapterOutboundGateway.handleMessage(msg);
			message = ehcacheadapterOutboundGateway.mgr.getCache(ehcacheadapterOutboundGateway.getCache()).get("1");
			assertNotNull(message);
			assertEquals("1 value",message.getValue());
		//Test the Get	
			msg = MessageBuilder.withPayload(message).setHeader(EhcacheAdapterHeaders.COMMAND,EhcacheAdapterHeaders.GET)
					.setHeader("replyChannel", "endChannel").build();
			ehcacheadapterOutboundGateway.handleMessage(msg);
			//Test a bad put	
			msg = MessageBuilder.withPayload("CRAP").setHeader(EhcacheAdapterHeaders.COMMAND,EhcacheAdapterHeaders.PUT)
					.setHeader("replyChannel", "endChannel").build();
			boolean isException = false;
			try{
				ehcacheadapterOutboundGateway.handleMessage(msg);
			}catch(java.lang.Exception cce){
				isException = true;
			}
			assertTrue(isException);

	}

	@Test
	public void testGetElement() {
		//fail("Not yet implemented");
	}
	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	public void setUp(String name, Class<?> cls, String gatewayId) {
		context = new ClassPathXmlApplicationContext(name, cls);
		consumer = this.context.getBean(gatewayId, EventDrivenConsumer.class);
	}
}
