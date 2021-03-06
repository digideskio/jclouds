/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.joyent.sdc.v6_5.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.joyent.sdc.v6_5.SDCClient;
import org.jclouds.joyent.sdc.v6_5.internal.BaseSDCClientExpectTest;
import org.jclouds.joyent.sdc.v6_5.parse.ParseKeyListTest;
import org.jclouds.joyent.sdc.v6_5.parse.ParseKeyTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "KeyClientExpectTest")
public class KeyClientExpectTest extends BaseSDCClientExpectTest {
   HttpRequest list = HttpRequest.builder().method("GET").endpoint(
            URI.create("https://api.joyentcloud.com/my/keys")).headers(
            ImmutableMultimap.<String, String> builder().put("X-Api-Version", "~6.5").put("Accept", "application/json")
                     .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build();

   public void testListKeysWhenResponseIs2xx() {
      HttpResponse listResponse = HttpResponse.builder().statusCode(200).payload(
               payloadFromResource("/key_list.json")).build();

      SDCClient clientWhenKeysExists = requestsSendResponses(getDatacenters, getDatacentersResponse, list, listResponse);

      assertEquals(clientWhenKeysExists.getKeyClient().list(), new ParseKeyListTest().expected());
   }

   public void testListKeysWhenResponseIs404() {
      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      SDCClient listWhenNone = requestsSendResponses(getDatacenters, getDatacentersResponse, list, listResponse);

      assertEquals(listWhenNone.getKeyClient().list(), ImmutableSet.of());
   }

   public void testCreateKeyWhenResponseIs202() throws Exception {
      HttpRequest create = HttpRequest
               .builder()
               .method("POST")
               .endpoint(URI.create("https://api.joyentcloud.com/my/keys"))
               .headers(
                        ImmutableMultimap.<String, String> builder().put("X-Api-Version", "~6.5").put("Accept",
                                 "application/json").put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
               .payload(
                     payloadFromStringWithContentType(
                           "{\"name\":\"rsa\",\"key\":\"ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA0A5Pf5Cq...\"}",
                           "application/json")).build();

      HttpResponse createResponse = HttpResponse.builder().statusCode(202).message("HTTP/1.1 202 Accepted")
               .payload(payloadFromResourceWithContentType("/key.json", "application/json; charset=UTF-8"))
               .build();

      SDCClient clientWithNewKey = requestsSendResponses(getDatacenters, getDatacentersResponse, create, createResponse);

      assertEquals(clientWithNewKey.getKeyClient().create(new ParseKeyTest().expected())
            .toString(), new ParseKeyTest().expected().toString());
   }
}
