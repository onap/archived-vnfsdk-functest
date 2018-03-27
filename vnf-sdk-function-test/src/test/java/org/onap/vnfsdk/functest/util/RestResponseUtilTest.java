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

package org.onap.vnfsdk.functest.util;

import org.junit.Before;
import org.junit.Test;
import org.onap.vnfsdk.functest.externalservice.entity.Environment;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RestResponseUtilTest {

    private Response response;
    private Object envObj;

    @Before
    public void setUp() {
        envObj = new Environment();
        ((Environment) envObj).setRemoteIp("192.168.4.47");
        ((Environment) envObj).setUserName("root");
        ((Environment) envObj).setPassword("root123");
        ((Environment) envObj).setPath("src\\test\\resources");
    }


    @Test
    public void testGetSuccessResponse() {
        response = RestResponseUtil.getSuccessResponse(envObj);
        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testGetCreateSussceeResponse() {
        response = RestResponseUtil.getCreateSuccessResponse(envObj);
        assertNotNull(response);
        assertEquals(201, response.getStatus());
    }

    @Test
    public void testGetErrorResponse() {
        response = RestResponseUtil.getErrorResponse(envObj);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetNotFoundResponse() {
        response = RestResponseUtil.getNotFoundResponse(envObj);
        assertEquals(404, response.getStatus());
    }
}
