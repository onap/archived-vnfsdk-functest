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

package org.onap.vnfsdk.functest.resource;

import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;
import org.onap.vnfsdk.functest.FileUtil;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus.operResultCode;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.onap.vnfsdk.functest.responsehandler.VnfFuncTestResponseHandler;
import org.onap.vnfsdk.functest.util.RestResponseUtil;
import org.onap.vnfsdk.functest.util.ZipCompressor;

import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommonManagerTest {

    private CommonManager commonManger;

    private String instanceId;

    private String funcTestId = "59d1e651-df9f-4008-902f-e3b377e6ec30";

    private Response response = null;

    @Before
    public void setUp() {
        commonManger = new CommonManager();
    }

    @Test
    public void testexecuteFunc() throws FileNotFoundException {

        URL url = Thread.currentThread().getContextClassLoader().getResource("RobotScript");
        String zipFileName = url.getPath() + ".zip";
        try {
            new ZipCompressor(zipFileName).compress(url.getPath());
        } catch (IOException e) {

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
        Response response = commonManger.queryResultByFuncTest(funcTestId);
        assertNotNull(response);
    }

    @Test
    public void testUploadFuncTestPackage() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("RobotScript");
        // Some temporary folder uploaded in github
        String zipFileName = "https://github.com/zoul/Finch/zipball/master/";

        new MockUp<FileUtil>() {

            @Mock
            public String[] getDirectory(String directory) {
                File file = new File("temp");
                return file.list();
            }
        };

        try {
            // InputStream mockInputStream = new FileInputStream(zipFileName);
            response = commonManger.uploadFuncTestPackage(funcTestId, zipFileName);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetOperationResult() {
        try {
            response = commonManger.getOperationResult(funcTestId);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDownloadResults() {
        new MockUp<OperationStatusHandler>() {

            @Mock
            public Response getOperationStatus(UUID uuid) {
                OperationStatus operstatus = new OperationStatus();
                operstatus.setOperFinished(true);
                operstatus.setoResultCode(operResultCode.SUCCESS);
                operstatus.setOperResultMessage("finished");
                return response;
            }
        };

        new MockUp<VnfFuncTestResponseHandler>() {

            @Mock
            public Response downloadResults(String funcTestId) {
                OperationStatus operstatus = new OperationStatus();
                operstatus.setOperFinished(true);
                operstatus.setoResultCode(operResultCode.SUCCESS);
                operstatus.setOperResultMessage("finished");

                return RestResponseUtil.getSuccessResponse(operstatus);
            }
        };

        try {
            response = commonManger.downloadResults(funcTestId);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStoreChunkFileInLocal() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("RobotScript");
        String zipFileName = url.getPath() + ".zip";

        try {
            InputStream mockInputStream = new FileInputStream(zipFileName);
            String chunkFilePath =
                    commonManger.storeChunkFileInLocal("src/test/resources", "chunkFileInLocal", mockInputStream);
            assertNotNull(chunkFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetEnvironment() {
        try {

            String jsonInput =
                    "{\"remoteIp\":\"192.168.4.47\",\"userName\":\"root\",\"password\":\"root123\", \"path\":\"/src/test/resources\"}";
            response = commonManger.setEnvironment(jsonInput);
            commonManger.executeFunctionTest(funcTestId, response.getEntity().toString(), "robot");
            assertNotNull(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
