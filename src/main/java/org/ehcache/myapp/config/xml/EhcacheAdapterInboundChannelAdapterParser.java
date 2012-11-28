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
package org.ehcache.myapp.config.xml;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

import org.ehcache.myapp.inbound.EhcacheAdapterPollingChannelAdapter;

/**
 * The EhcacheAdapter Inbound Channel adapter parser
 *
 * @author Glenn Renfro
 * @since 1.0
 *
 */
public class EhcacheAdapterInboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser{


	protected BeanMetadataElement parseSource(Element element, ParserContext parserContext) {

		final BeanDefinitionBuilder ehcacheadapterPollingChannelAdapterBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(EhcacheAdapterPollingChannelAdapter.class);

		final BeanDefinitionBuilder ehcacheadapterExecutorBuilder = EhcacheAdapterParserUtils.getEhcacheAdapterExecutorBuilder(element, parserContext);

		IntegrationNamespaceUtils.setValueIfAttributeDefined(ehcacheadapterExecutorBuilder, element, "example-property");

		final BeanDefinition ehcacheadapterExecutorBuilderBeanDefinition = ehcacheadapterExecutorBuilder.getBeanDefinition();
		final String channelAdapterId = this.resolveId(element, ehcacheadapterPollingChannelAdapterBuilder.getRawBeanDefinition(), parserContext);
		final String ehcacheadapterExecutorBeanName = channelAdapterId + ".ehcacheadapterExecutor";

		parserContext.registerBeanComponent(new BeanComponentDefinition(ehcacheadapterExecutorBuilderBeanDefinition, ehcacheadapterExecutorBeanName));

		ehcacheadapterPollingChannelAdapterBuilder.addConstructorArgReference(ehcacheadapterExecutorBeanName);

		return ehcacheadapterPollingChannelAdapterBuilder.getBeanDefinition();
	}
}
