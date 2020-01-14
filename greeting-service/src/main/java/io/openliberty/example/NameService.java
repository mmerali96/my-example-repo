/*
 *
 *  Copyright 2016-2017 Red Hat, Inc, IBM, and individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package io.openliberty.example;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@ApplicationScoped
public interface NameService {
    @GET
    @Path("/api/name")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "fallback")
    String get();

    default String fallback() {
        return "Fallback";
    }

    static boolean isCircuitBreakerOpen() {
    	System.out.println("isCircuitBreakerOpen enter");
       // There isn't a good way to do this with normal APIs. Instead, we gather the CircuitBreaker total closed time twice and
    	// see if the values are different. If they are, we assume that it's closed. 
    	Client client = ClientBuilder.newClient();
		WebTarget target = client.target(
				"http://localhost:9080/metrics/application/ft.io.openliberty.example.NameService.get.circuitbreaker.closed.total");
		Response response = target.request(MediaType.TEXT_PLAIN).get();
		String value1 = response.readEntity(String.class);
	
		// If there's no data yet, assume closed
		if ( "".contentEquals(value1))
			return false;
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
		response = target.request(MediaType.TEXT_PLAIN).get();
		String value2 = response.readEntity(String.class);
		
		if ( value1 != null && value2 != null && value1.contentEquals(value2)) {
			// Total closed time has not grown, so assume open (or half open)
			
			return true;
		}
		
		return false;
    }
}
