/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.quantum.v1_0.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;
import org.jclouds.openstack.quantum.v1_0.internal.BaseQuantumClientExpectTest;
import org.jclouds.openstack.quantum.v1_0.parse.ParseNetworkDetailsTest;
import org.jclouds.openstack.quantum.v1_0.parse.ParseNetworkTest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of NetworkClient
 *
 * @author Adam Lowe
 */
@Test(groups="unit", testName = "NetworkClientExpectTest")
public class NetworkClientExpectTest extends BaseQuantumClientExpectTest {
   
   public void testListReferencesReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/list_network_refs.json", APPLICATION_JSON)).build())
            .getNetworkClientForRegion("region-a.geo-1");
      
      Set<Reference> nets = client.listReferences();
      assertEquals(nets, listOfNetworkRefs());
   }

   public void testListReferencesReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks").build(),
            standardResponseBuilder(404).build())
            .getNetworkClientForRegion("region-a.geo-1");

      assertTrue(client.listReferences().isEmpty());
   }

   public void testListReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/detail").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/list_networks.json", APPLICATION_JSON)).build())
            .getNetworkClientForRegion("region-a.geo-1");

      Set<Network> nets = client.list();
      assertEquals(nets, listOfNetworks());
   }

   public void testListReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/detail").build(),
            standardResponseBuilder(404).build())
            .getNetworkClientForRegion("region-a.geo-1");

      assertTrue(client.list().isEmpty());
   }

   public void testShowReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/network.json", APPLICATION_JSON)).build())
            .getNetworkClientForRegion("region-a.geo-1");

      Network net = client.get("16dba3bc-f3fa-4775-afdc-237e12c72f6a");
      assertEquals(net, new ParseNetworkTest().expected());
   }

   public void testShowReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
            standardResponseBuilder(404).build())
            .getNetworkClientForRegion("region-a.geo-1");

      assertNull(client.get("16dba3bc-f3fa-4775-afdc-237e12c72f6a"));
   }

   public void testShowDetailsReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/detail").build(),
            standardResponseBuilder(200).payload(payloadFromResourceWithContentType("/network_details.json", APPLICATION_JSON)).build())
            .getNetworkClientForRegion("region-a.geo-1");

      NetworkDetails net = client.getDetails("16dba3bc-f3fa-4775-afdc-237e12c72f6a");
      assertEquals(net, new ParseNetworkDetailsTest().expected());
   }

   public void testShowDetailsReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/16dba3bc-f3fa-4775-afdc-237e12c72f6a/detail").build(),
            standardResponseBuilder(404).build())
            .getNetworkClientForRegion("region-a.geo-1");

      assertNull(client.getDetails("16dba3bc-f3fa-4775-afdc-237e12c72f6a"));
   }

   public void testCreateReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks").method("POST")
                  .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\"}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(200).payload(payloadFromStringWithContentType("{\"network\":{\"id\":\"12345\"}}", APPLICATION_JSON)).build())
            .getNetworkClientForRegion("region-a.geo-1");

      Reference net = client.create("another-test");
      assertEquals(net, Reference.builder().id("12345").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testCreateReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks").method("POST")
                  .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\"}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(401).build())
            .getNetworkClientForRegion("region-a.geo-1");

      client.create("another-test");
   }
   
   public void testUpdateReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/12345").method("PUT")
                  .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\"}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(200).build())
            .getNetworkClientForRegion("region-a.geo-1");

      assertTrue(client.rename("12345", "another-test"));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/12345").method("PUT")
                  .payload(payloadFromStringWithContentType("{\"network\":{\"name\":\"another-test\"}}", MediaType.APPLICATION_JSON)).build(),
            standardResponseBuilder(404).build())
            .getNetworkClientForRegion("region-a.geo-1");

      client.rename("12345", "another-test");
   }

   public void testDeleteReturns2xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/12345").method("DELETE").build(),
            standardResponseBuilder(200).build())
            .getNetworkClientForRegion("region-a.geo-1");

      assertTrue(client.delete("12345"));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDeleteReturns4xx() {
      NetworkClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/tenants/3456/networks/12345").method("DELETE").build(),
            standardResponseBuilder(403).build())
            .getNetworkClientForRegion("region-a.geo-1");

      client.delete("12345");
   }
   
   protected Set<Reference> listOfNetworkRefs() {
      return ImmutableSet.of(
            Reference.builder().id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
            Reference.builder().id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
            Reference.builder().id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
            Reference.builder().id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
            Reference.builder().id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
            Reference.builder().id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
            Reference.builder().id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
            Reference.builder().id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
            Reference.builder().id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
            Reference.builder().id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
            Reference.builder().id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
            Reference.builder().id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }
   
   protected Set<Network> listOfNetworks() {
      return ImmutableSet.of(
            Network.builder().name("jclouds-port-test").id("16dba3bc-f3fa-4775-afdc-237e12c72f6a").build(),
            Network.builder().name("wibble").id("1a104cf5-cb18-4d35-9407-2fd2646d9d0b").build(),
            Network.builder().name("jclouds-test").id("31083ae2-420d-48b2-ac98-9f7a4fd8dbdc").build(),
            Network.builder().name("jclouds-test").id("49c6d6fa-ff2a-459d-b975-75a8d31c9a89").build(),
            Network.builder().name("wibble").id("5cb3d6f4-62cb-41c9-b964-ba7d9df79e4e").build(),
            Network.builder().name("jclouds-port-test").id("5d51d012-3491-4db7-b1b5-6f254015015d").build(),
            Network.builder().name("wibble").id("5f9cf7dc-22ca-4097-8e49-1cc8b23faf17").build(),
            Network.builder().name("jclouds-test").id("6319ecad-6bff-48b2-9b53-02ede8cb7588").build(),
            Network.builder().name("jclouds-port-test").id("6ba4c788-661f-49ab-9bf8-5f10cbbb2f57").build(),
            Network.builder().name("jclouds-test").id("74ed170b-5069-4353-ab38-9719766dc57e").build(),
            Network.builder().name("wibble").id("b71fcac1-e864-4031-8c5b-edbecd9ece36").build(),
            Network.builder().name("jclouds-port-test").id("c7681895-d84d-4650-9ca0-82c72036b855").build()
      );
   }

}
