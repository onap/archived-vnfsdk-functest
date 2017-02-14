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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.openo.vnfsdk.functest.responsehandler.VnfFuncTestResponseHandler;

public class CommonManagerTest {

    private CommonManager commonManger;

    private String instanceId = "1234567";

    @Before
    public void setUp() {
        commonManger = new CommonManager();
    }

    @Test
    public void testexecuteFunc() throws FileNotFoundException {
        InputStream mockInputStream = new FileInputStream(new File("src//test//resources//RobotScript.zip"));
        Response response = commonManger.executeFuncTest(mockInputStream);
        instanceId = response.getEntity().toString();
        assertNotNull(instanceId);
    }

    @Test
    public void testQueryResultWhenInstanceIdPresent() {

        Map<String, String> mapConfigValues = new HashMap<String, String>();
        mapConfigValues.put("DIR_RESULT", ".\\src\\test\\resources");
        VnfFuncTestResponseHandler.getInstance().setConfigMap(mapConfigValues);
        Response response = commonManger.queryResultByFuncTest("59d1e651-df9f-4008-902f-e3b377e6ec30");
        assertNotNull(response);
    }

    @Test
    public void testQueryResultWhenInstanceIdAbsent() {
        Response response = commonManger.queryResultByFuncTest("1234567");
        assertNotNull(response);
    }
}
