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

package org.ehcache.siehcache.outbound;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.ehcache.siehcache.EhcacheAdapterHeaders;
import org.ehcache.siehcache.core.EhcacheAdapterExecutor;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.integration.Message;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * 
 * @author Glenn Renfro
 * @since 1.0
 * 
 */
public class EhcacheAdapterOutboundGateway extends
		AbstractReplyProducingMessageHandler {

	private final EhcacheAdapterExecutor ehcacheadapterExecutor;
	private boolean producesReply = true; // false for outbound-channel-adapter,
											// true for outbound-gateway
	private static String CACHE_MANAGER_NAME = "glenncache";
	private static String CACHE_NAME = "myCache";
	public String ehcacheXml;
	public String cache;

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getEhcacheXml() {
		return ehcacheXml;
	}

	public void setEhcacheXml(String ehcacheXml) {
		this.ehcacheXml = ehcacheXml;
	}

	/**
	 * Constructor taking an {@link EhcacheAdapterExecutor} that wraps common
	 * EhcacheAdapter Operations.
	 * 
	 * @param ehcacheadapterExecutor
	 *            Must not be null
	 * 
	 */
	public EhcacheAdapterOutboundGateway(
			EhcacheAdapterExecutor ehcacheadapterExecutor) {

		Assert.notNull(ehcacheadapterExecutor,
				"ehcacheadapterExecutor must not be null.");
		this.ehcacheadapterExecutor = ehcacheadapterExecutor;
	}

	/**
	 *
	 */
	@Override
	protected void onInit() {
		super.onInit();
		System.out.println("INIT");
		CacheManager mgr = CacheManager.getCacheManager(CACHE_MANAGER_NAME);
		if (mgr == null) {
			mgr = createNewManager(CACHE_MANAGER_NAME);
		}

		
	}

	protected CacheManager createNewManager(String cacheName) {
		CacheManager mgr = CacheManager.create();
		mgr.setName(CACHE_MANAGER_NAME);
		return mgr;
	}

	protected void createCache(CacheManager mgr) {

		CacheConfiguration cfg = new CacheConfiguration();
		cfg.setName(cache);
		cfg.setMaxBytesLocalHeap("25m");
		Cache cache = new Cache(cfg);
		mgr.addCache(cache);

	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {

		final Object result;

		result = this.ehcacheadapterExecutor
				.executeOutboundOperation(requestMessage);
		System.out.println(ehcacheXml);
		System.out.println(cache);

		try {
			if(cache == null){
				cache = CACHE_NAME;
			}
			Cache cacheInstance = CacheManager.getInstance().getCache(cache);
			if (cacheInstance == null) {
				createCache(CacheManager.getInstance());
				cacheInstance = CacheManager.getInstance().getCache(cache);
			}
			

			if (getCommand(requestMessage).equals(EhcacheAdapterHeaders.PUT)) {
				putData(requestMessage, cacheInstance);
			} else {
				if (getCommand(requestMessage)
						.equals(EhcacheAdapterHeaders.GET)) {
					return getData(requestMessage, cacheInstance);
				}
			}
		} catch (Exception e) {
			System.out.println(CacheManager.getInstance());
			e.printStackTrace();
		}
		if (result == null || !producesReply) {

			return null;
		}
		
		return MessageBuilder.withPayload(result)
				.copyHeaders(requestMessage.getHeaders()).build();
	}

	public String getCommand(Message msg) {
		return (String) msg.getHeaders().get(EhcacheAdapterHeaders.COMMAND);
	}

	public String getKey(Message msg) {
		
		return (String) msg.getHeaders().get(EhcacheAdapterHeaders.KEY);
	}

	/**
	 * If set to 'false', this component will act as an Outbound Channel
	 * Adapter. If not explicitly set this property will default to 'true'.
	 * 
	 * @param producesReply
	 *            Defaults to 'true'.
	 * 
	 */
	public void setProducesReply(boolean producesReply) {
		this.producesReply = producesReply;
	}

	public void putData(Message message, Cache cache) {
		System.out.println("Putting Data for "+getKey(message) );
		cache.put(new Element(getKey(message), message.getPayload()));

	}

	public Object getData(Message message, Cache cache) {
		Element val = cache.get(getKey(message));
		System.out.println("Getting Data for "+getKey(message) );
		if (val == null) {
			return null;
		}
		return val.getValue();
	}
}
