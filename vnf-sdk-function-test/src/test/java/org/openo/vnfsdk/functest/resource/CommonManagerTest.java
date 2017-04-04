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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.openo.vnfsdk.functest.responsehandler.VnfFuncTestResponseHandler;
import org.openo.vnfsdk.functest.util.ZipCompressor;

public class CommonManagerTest {

    private CommonManager commonManger;

    private String instanceId = "1234567";

    private Response response = null;

    @Before
    public void setUp() {
        commonManger = new CommonManager();
    }

    @Test
    public void testSetEnvironment() {
        try {

            String jsonInput =
                    "{\"RemoteIp\":\"192.168.4.47\",\"UserName\":\"root\",\"Password\":\"root123\", \"Path\":\"/src/test/resources\"}";
            response = commonManger.setEnvironment(jsonInput);
            assertNotNull(response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testexecuteFunc() throws FileNotFoundException {

        URL url = Thread.currentThread().getContextClassLoader().getResource("RobotScript");
        String zipFileName = url.getPath() + ".zip";
        try {
            new ZipCompressor(zipFileName).compress(url.getPath());
        } catch(IOException e) {

        }

        InputStream mockInputStream = new FileInputStream(zipFileName);
        Response response = commonManger.executeFuncTest(mockInputStream);
        instanceId = response.getEntity().toString();
        assertNotNull(instanceId);
    }

    @Test
    public void testQueryResultWhenInstanceIdPresent() {

        Map<String, String> mapConfigValues = new HashMap<String, String>();
        String resultFolder = "59d1e651-df9f-4008-902f-e3b377e6ec30";
        URL url = Thread.currentThread().getContextClassLoader().getResource(resultFolder);
        File file = new File(url.getPath());
        File parentFile = file.getParentFile();
        mapConfigValues.put("DIR_RESULT", parentFile.getAbsolutePath());
        VnfFuncTestResponseHandler.getInstance().setConfigMap(mapConfigValues);
        Response response = commonManger.queryResultByFuncTest(resultFolder);
        assertNotNull(response);
    }

    @Test
    public void testQueryResultWhenInstanceIdAbsent() {
        Response response = commonManger.queryResultByFuncTest("1234567");
        assertNotNull(response);
    }
}
