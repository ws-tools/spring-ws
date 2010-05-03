/*
 * Copyright 2005-2010 the original author or authors.
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

package org.springframework.ws.server.endpoint.adapter.method;

import org.springframework.core.MethodParameter;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import org.jdom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JDomPayloadMethodProcessorTest extends AbstractPayloadMethodProcessorTestCase {

    @Override
    protected AbstractPayloadSourceMethodProcessor createProcessor() {
        return new JDomPayloadMethodProcessor();
    }

    @Override
    protected MethodParameter[] createSupportedParameters() throws NoSuchMethodException {
        return new MethodParameter[]{new MethodParameter(getClass().getMethod("element", Element.class), 0)};
    }

    @Override
    protected MethodParameter[] createSupportedReturnTypes() throws NoSuchMethodException {
        return new MethodParameter[]{new MethodParameter(getClass().getMethod("element", Element.class), -1)};
    }

    @Override
    protected void testArgument(Object argument, MethodParameter parameter) {
        assertTrue("argument not a element", argument instanceof Element);
        Element node = (Element) argument;
        assertEquals("Invalid namespace", NAMESPACE_URI, node.getNamespaceURI());
        assertEquals("Invalid local name", LOCAL_NAME, node.getName());
    }

    @Override
    protected Element getReturnValue(MethodParameter returnType) {
        return new Element(LOCAL_NAME, NAMESPACE_URI);
    }

    @ResponsePayload
    public Element element(@RequestPayload Element element) {
        return element;
    }
}
