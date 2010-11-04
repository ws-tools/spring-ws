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

package org.springframework.ws.test.server;

import java.io.IOException;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.test.support.matcher.PayloadDiffMatcher;
import org.springframework.ws.test.support.matcher.SchemaValidatingMatcher;
import org.springframework.ws.test.support.matcher.WebServiceMessageMatcher;
import org.springframework.xml.transform.ResourceSource;

import static org.springframework.ws.test.support.AssertionErrors.assertEquals;
import static org.springframework.ws.test.support.AssertionErrors.assertTrue;

/**
 * Factory methods for {@link ResponseMatcher} classes. Typically used to provide input for {@link
 * ResponseActions#andExpect(ResponseMatcher)}.
 *
 * @author Arjen Poutsma
 * @since 2.0
 */
public abstract class ResponseMatchers {

    private ResponseMatchers() {
    }

    /**
     * Expects any response.
     *
     * @return the response matcher
     */
    public static ResponseMatcher anything() {
        return new ResponseMatcher() {
            public void match(WebServiceMessage request, WebServiceMessage response)
                    throws IOException, AssertionError {
            }
        };
    }

    // Payload

    /**
     * Expects the given {@link Source} XML payload.
     *
     * @param payload the XML payload
     * @return the response matcher
     */
    public static ResponseMatcher payload(Source payload) {
        return new WebServiceMessageMatcherAdapter(new PayloadDiffMatcher(payload));
    }

    /**
     * Expects the given {@link Resource} XML payload.
     *
     * @param payload the XML payload
     * @return the response matcher
     */
    public ResponseMatcher payload(Resource payload) throws IOException {
        return payload(new ResourceSource(payload));
    }

    /**
     * Expects the payload to validate against the given XSD schema(s).
     *
     * @param schema         the schema
     * @param furtherSchemas further schemas, if necessary
     * @return the response matcher
     */
    public static ResponseMatcher validPayload(Resource schema, Resource... furtherSchemas) throws IOException {
        return new WebServiceMessageMatcherAdapter(new SchemaValidatingMatcher(schema, furtherSchemas));
    }


    // SOAP Fault

    /**
     * Expects the response <strong>not</strong> to contain a SOAP fault.
     *
     * @return the response matcher
     */
    public static ResponseMatcher noFault() {
        return new SoapResponseMatcher() {
            @Override
            protected void matchSoap(SoapMessage request, SoapMessage response) throws IOException, AssertionError {
                SoapBody responseBody = response.getSoapBody();
                assertTrue("Response has no SOAP Body", responseBody != null);
                assertTrue("Response has a SOAP Fault", !responseBody.hasFault());
            }
        };
    }

    /**
     * Expects a {@code MustUnderstand} fault.
     *
     * @see org.springframework.ws.soap.SoapBody#addMustUnderstandFault(String, Locale)
     */
    public static ResponseMatcher mustUnderstandFault() {
        return mustUnderstandFault(null);
    }

    /**
     * Expects a {@code MustUnderstand} fault with a particular fault string or reason.
     *
     * @param faultStringOrReason the SOAP 1.1 fault string or SOAP 1.2 reason text. If {@code null} the fault string or
     * reason text will not be verified
     * @see org.springframework.ws.soap.SoapBody#addMustUnderstandFault(String, Locale)
     */
    public static ResponseMatcher mustUnderstandFault(String faultStringOrReason) {
        return new SoapFaultResponseMatcher(faultStringOrReason) {
            @Override
            protected QName getExpectedFaultCode(SoapVersion version) {
                return version.getMustUnderstandFaultName();
            }
        };
    }

    /**
     * Expects a {@code Client} (SOAP 1.1) or {@code Sender} (SOAP 1.2) fault.
     *
     * @see org.springframework.ws.soap.SoapBody#addClientOrSenderFault(String, Locale)
     */
    public static ResponseMatcher clientOrSenderFault() {
        return clientOrSenderFault(null);
    }

    /**
     * Expects a {@code Client} (SOAP 1.1) or {@code Sender} (SOAP 1.2) fault with a particular fault string or reason.
     *
     * @param faultStringOrReason the SOAP 1.1 fault string or SOAP 1.2 reason text. If {@code null} the fault string or
     * reason text will not be verified
     * @see org.springframework.ws.soap.SoapBody#addClientOrSenderFault(String, Locale)
     */
    public static ResponseMatcher clientOrSenderFault(String faultStringOrReason) {
        return new SoapFaultResponseMatcher(faultStringOrReason) {
            @Override
            protected QName getExpectedFaultCode(SoapVersion version) {
                return version.getClientOrSenderFaultName();
            }
        };
    }

    /**
     * Expects a {@code Server} (SOAP 1.1) or {@code Receiver} (SOAP 1.2) fault.
     *
     * @see org.springframework.ws.soap.SoapBody#addServerOrReceiverFault(String, java.util.Locale)
     */
    public static ResponseMatcher serverOrReceiverFault() {
        return serverOrReceiverFault(null);
    }

    /**
     * Expects a {@code Server} (SOAP 1.1) or {@code Receiver} (SOAP 1.2) fault with a particular fault string or reason.
     *
     * @param faultStringOrReason the SOAP 1.1 fault string or SOAP 1.2 reason text. If {@code null} the fault string or
     * reason text will not be verified
     * @see org.springframework.ws.soap.SoapBody#addClientOrSenderFault(String, Locale)
     */
    public static ResponseMatcher serverOrReceiverFault(String faultStringOrReason) {
        return new SoapFaultResponseMatcher(faultStringOrReason) {
            @Override
            protected QName getExpectedFaultCode(SoapVersion version) {
                return version.getServerOrReceiverFaultName();
            }
        };
    }

    /**
     * Expects a {@code VersionMismatch} fault.
     *
     * @see org.springframework.ws.soap.SoapBody#addVersionMismatchFault(String, java.util.Locale)
     */
    public static ResponseMatcher versionMismatchFault() {
        return versionMismatchFault(null);
    }

    /**
     * Expects a {@code VersionMismatch} fault with a particular fault string or reason.
     *
     * @param faultStringOrReason the SOAP 1.1 fault string or SOAP 1.2 reason text. If {@code null} the fault string or
     * reason text will not be verified
     * @see org.springframework.ws.soap.SoapBody#addClientOrSenderFault(String, Locale)
     */
    public static ResponseMatcher versionMismatchFault(String faultStringOrReason) {
        return new SoapFaultResponseMatcher(faultStringOrReason) {
            @Override
            protected QName getExpectedFaultCode(SoapVersion version) {
                return version.getVersionMismatchFaultName();
            }
        };
    }


    private static abstract class SoapResponseMatcher implements ResponseMatcher {

        public final void match(WebServiceMessage request, WebServiceMessage response) throws IOException, AssertionError {
            assertTrue("Request is not a SOAP message", request instanceof SoapMessage);
            assertTrue("Response is not a SOAP message", response instanceof SoapMessage);
            matchSoap((SoapMessage)request, (SoapMessage) response);
        }

        protected abstract void matchSoap(SoapMessage request, SoapMessage response) throws IOException, AssertionError;

    }
    private static abstract class SoapFaultResponseMatcher extends SoapResponseMatcher {

        private final String expectedFaultStringOrReason;

        protected SoapFaultResponseMatcher(String expectedFaultStringOrReason) {
            this.expectedFaultStringOrReason = expectedFaultStringOrReason;
        }

        @Override
        protected void matchSoap(SoapMessage request, SoapMessage response) throws IOException, AssertionError {
            SoapBody responseBody = response.getSoapBody();
            assertTrue("Response has no SOAP Body", responseBody != null);
            assertTrue("Response has no SOAP Fault", responseBody.hasFault());
            SoapFault soapFault = responseBody.getFault();
            QName expectedFaultCode = getExpectedFaultCode(response.getVersion());
            assertEquals("Invalid SOAP Fault code", expectedFaultCode, soapFault.getFaultCode());
            if (expectedFaultStringOrReason != null) {
                assertEquals("Invalid SOAP Fault string/reason", expectedFaultStringOrReason,
                        soapFault.getFaultStringOrReason());
            }
        }

        protected abstract QName getExpectedFaultCode(SoapVersion version);

    }

    /**
     * Adapts a {@link WebServiceMessageMatcher} to the {@link ResponseMatcher} contract.
     */
    private static class WebServiceMessageMatcherAdapter implements ResponseMatcher {

        private final WebServiceMessageMatcher adaptee;

        private WebServiceMessageMatcherAdapter(WebServiceMessageMatcher adaptee) {
            this.adaptee = adaptee;
        }

        public void match(WebServiceMessage request, WebServiceMessage response) throws IOException, AssertionError {
            adaptee.match(response);
        }
    }

}