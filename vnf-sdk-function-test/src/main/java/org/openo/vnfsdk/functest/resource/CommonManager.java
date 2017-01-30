/*
 * Copyright 2017 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openo.vnfsdk.functest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/functest")
@Api(tags = {" function test Management "})
public class CommonManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonManager.class);

    @Path("")
    @POST
    @ApiOperation(value = "execute the function test")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
                    @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
                    @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
                    @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @Timed
    public Response executeFuncTest() {
        LOGGER.info("execute function test");
        return null;
    }

    @Path("/{functestId}")
    @GET
    @ApiOperation(value = "get function test result by id")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
                    @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
                    @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
                    @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @Timed
    public Response queryResultByFuncTest(@ApiParam(value = "functestId") @PathParam("functestId") String instanceId) {
        LOGGER.info("query functest result by id." + instanceId);
        return null;

    }

}
