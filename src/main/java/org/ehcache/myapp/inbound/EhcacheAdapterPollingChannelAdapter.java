/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.myapp.inbound;

import org.springframework.integration.Message;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

import org.ehcache.myapp.core.EhcacheAdapterExecutor;

/**
 * @author Glenn Renfro
 * @since 1.0
 *
 */
public class EhcacheAdapterPollingChannelAdapter extends IntegrationObjectSupport implements MessageSource<Object>{

	private final EhcacheAdapterExecutor ehcacheadapterExecutor;

	/**
	 * Constructor taking a {@link EhcacheAdapterExecutor} that provide all required EhcacheAdapter
	 * functionality.
	 *
	 * @param ehcacheadapterExecutor Must not be null.
	 */
	public EhcacheAdapterPollingChannelAdapter(EhcacheAdapterExecutor ehcacheadapterExecutor) {
		super();
		Assert.notNull(ehcacheadapterExecutor, "ehcacheadapterExecutor must not be null.");
		this.ehcacheadapterExecutor = ehcacheadapterExecutor;
	}

	/**
	 * Check for mandatory attributes
	 */
	@Override
	protected void onInit() throws Exception {
		 super.onInit();
	}

	/**
	 * Uses {@link EhcacheAdapterExecutor#poll()} to executes the EhcacheAdapter operation.
	 *
	 * If {@link EhcacheAdapterExecutor#poll()} returns null, this method will return
	 * <code>null</code>. Otherwise, a new {@link Message} is constructed and returned.
	 */
	public Message<Object> receive() {

		final Object payload = ehcacheadapterExecutor.poll();

		if (payload == null) {
			return null;
		}

		return MessageBuilder.withPayload(payload).build();
	}

	@Override
	public String getComponentType(){
		return "ehcacheadapter:inbound-channel-adapter";
	}

}
