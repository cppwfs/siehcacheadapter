/*
uytea * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.integration.jpa.config.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.test.util.TestUtils;

import org.ehcache.siehcache.core.EhcacheAdapterExecutor;

/**
 * @author Glenn Renfro
 * @since 1.0
 *
 */
public class EhcacheAdapterInboundChannelAdapterParserTests {

	private ConfigurableApplicationContext context;

	private SourcePollingChannelAdapter consumer;

	@Test
	public void testJpaInboundChannelAdapterParser() throws Exception {

		setUp("EhcacheAdapterInboundChannelAdapterParserTests.xml", getClass(), "ehcacheadapterInboundChannelAdapter");

		final AbstractMessageChannel outputChannel = TestUtils.getPropertyValue(this.consumer, "outputChannel", AbstractMessageChannel.class);

		assertEquals("out", outputChannel.getComponentName());

		final EhcacheAdapterExecutor ehcacheadapterExecutor = TestUtils.getPropertyValue(this.consumer, "source.ehcacheadapterExecutor", EhcacheAdapterExecutor.class);

		assertNotNull(ehcacheadapterExecutor);

		final String exsampleProperty = TestUtils.getPropertyValue(ehcacheadapterExecutor, "exampleProperty", String.class);

		//assertEquals("I am a sample property", exsampleProperty);

	}

	@Test
	public void testJpaExecutorBeanIdNaming() throws Exception {

		this.context = new ClassPathXmlApplicationContext("EhcacheAdapterInboundChannelAdapterParserTests.xml", getClass());
		assertNotNull(context.getBean("ehcacheadapterInboundChannelAdapter.ehcacheadapterExecutor", EhcacheAdapterExecutor.class));

	}

	@After
	public void tearDown(){
		if(context != null){
			context.close();
		}
	}

	public void setUp(String name, Class<?> cls, String consumerId){
		context    = new ClassPathXmlApplicationContext(name, cls);
		consumer   = this.context.getBean(consumerId, SourcePollingChannelAdapter.class);
	}

}
