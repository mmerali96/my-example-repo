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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class CircuitBreakerEndpoint {
    @GET
    @Path("/cb-state")
    @Produces("application/json")
    public CircuitBreaker getState() {
        return NameService.isCircuitBreakerOpen() ? CircuitBreaker.OPEN : CircuitBreaker.CLOSED;
    }

    public static class CircuitBreaker {
        static final CircuitBreaker OPEN = new CircuitBreaker("open");
        static final CircuitBreaker CLOSED = new CircuitBreaker("closed");

        private final String state;

        public CircuitBreaker(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }
    }
}
