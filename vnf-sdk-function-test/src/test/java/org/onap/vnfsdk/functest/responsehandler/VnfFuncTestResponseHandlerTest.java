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

package org.onap.vnfsdk.functest.responsehandler;

import org.junit.Test;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class VnfFuncTestResponseHandlerTest {

    private VnfFuncTestResponseHandler vnfSdkFuncHandler;
    private Map<String, String> mapConfigValues;
    private UUID taskID = UUID.fromString("59d1e651-df9f-4008-902f-e3b377e6ec30");
    private Response response = null;

    @Test
    public void testGetInstance() {
        vnfSdkFuncHandler = VnfFuncTestResponseHandler.getInstance();
        assertNotNull(vnfSdkFuncHandler);
    }

    @Test
    public void testSetConfigMap() {
        try {
            Object vnfFuncTestResponseHandlerObj = VnfFuncTestResponseHandler.getInstance();
            Method m = vnfFuncTestResponseHandlerObj.getClass().getDeclaredMethod("setConfigMap", new Class[]{Map.class});
            m.setAccessible(true);
            m.invoke(vnfFuncTestResponseHandlerObj, mapConfigValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadConfigurations() {
        try {
            Object vnfFuncTestResponseHandlerObj = VnfFuncTestResponseHandler.getInstance();
            Method m = vnfFuncTestResponseHandlerObj.getClass().getDeclaredMethod("loadConfigurations");
            m.setAccessible(true);
            m.invoke(vnfFuncTestResponseHandlerObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetResponseByFuncTestId() {
        try {
            vnfSdkFuncHandler = VnfFuncTestResponseHandler.getInstance();
            response = vnfSdkFuncHandler.getResponseByFuncTestId(taskID.toString());
            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadResults() {
        try {
            vnfSdkFuncHandler = VnfFuncTestResponseHandler.getInstance();
            response = vnfSdkFuncHandler.downloadResults(taskID.toString());
            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
