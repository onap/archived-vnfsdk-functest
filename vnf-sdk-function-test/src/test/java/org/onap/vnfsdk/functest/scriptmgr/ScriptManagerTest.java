/*
 * Copyright (c) 2018 Intel Corporation Intellectual Property
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

package org.onap.vnfsdk.functest.scriptmgr;

import io.dropwizard.testing.junit.DAOTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.vnfsdk.functest.FileUtil;
import org.onap.vnfsdk.functest.db.TaskMgrCaseTblDAO;
import org.onap.vnfsdk.functest.db.TaskMgrTaskTblDAO;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.onap.vnfsdk.functest.models.CaseRecord;
import org.onap.vnfsdk.functest.models.TaskRecord;
import org.onap.vnfsdk.functest.responsehandler.VnfFuncTestResponseHandler;
import org.onap.vnfsdk.functest.util.RestResponseUtil;
import org.onap.vnfsdk.functest.util.ZipCompressor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
public class ScriptManagerTest {

    @Rule
    public DAOTestRule daoTestRule = DAOTestRule.newBuilder()
            .addEntityClass(TaskRecord.class)
            .addEntityClass(CaseRecord.class)
            .build();
    private ScriptManager scriptManager;
    private TaskMgrTaskTblDAO taskMgrTaskTblDAO;
    private TaskMgrCaseTblDAO taskMgrCaseTblDAO;
    private String instanceId;
    private UUID taskID = UUID.fromString("59d1e651-df9f-4008-902f-e3b377e6ec30");
    private UUID envID = UUID.fromString("f5881897-c748-4f6e-9294-92c730faa001");
    private UUID operID = UUID.fromString("ed058d84-4a42-4c8e-8ecf-90de4c5a8bc8");
    private Response response = null;

    @Before
    public void setUp() {
        taskMgrTaskTblDAO = new TaskMgrTaskTblDAO(daoTestRule.getSessionFactory());
        taskMgrCaseTblDAO = new TaskMgrCaseTblDAO(daoTestRule.getSessionFactory());
        scriptManager = new ScriptManager(taskMgrTaskTblDAO, taskMgrCaseTblDAO);
    }

    @Test
    @PrepareForTest({FileUtil.class})
    public void testUploadFuncTestPackage() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("RobotScript");
        // Some temporary folder uploaded in github
        String zipFileName = "https://github.com/zoul/Finch/zipball/master/";

        File file = new File("temp");
        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.getDirectory(Mockito.anyString())).thenReturn(file.list());

        try {
            // InputStream mockInputStream = new FileInputStream(zipFileName);
            UUID uploadID = scriptManager.uploadFuncTestPackage(taskID, envID, zipFileName);
            assertNotNull(uploadID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetOperationResult() {
        try {
            response = scriptManager.getOperationResult(operID);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @PrepareForTest({OperationStatusHandler.class, VnfFuncTestResponseHandler.class})
    public void testDownloadResults() {
        OperationStatusHandler mockOperationStatusHandler = PowerMockito.mock(OperationStatusHandler.class);
        Whitebox.setInternalState(OperationStatusHandler.class, "oInstance", mockOperationStatusHandler);
        PowerMockito.when(mockOperationStatusHandler.getOperationStatus(Mockito.any())).thenReturn(response);

        VnfFuncTestResponseHandler mockVnfFuncTestResponseHandler = PowerMockito.mock(VnfFuncTestResponseHandler.class);
        Whitebox.setInternalState(VnfFuncTestResponseHandler.class, "vnfFuncRspHandler", mockVnfFuncTestResponseHandler);
        OperationStatus operstatus = new OperationStatus();
        operstatus.setOperFinished(true);
        operstatus.setoResultCode(OperationStatus.operResultCode.SUCCESS);
        operstatus.setOperResultMessage("finished");
        PowerMockito.when(mockVnfFuncTestResponseHandler.downloadResults(Mockito.anyString())).thenReturn(RestResponseUtil.getSuccessResponse(operstatus));

        try {
            response = scriptManager.downloadResults(taskID);
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
            new ZipCompressor(zipFileName).compress(url.getPath());
            InputStream mockInputStream = new FileInputStream(zipFileName);
            String chunkFilePath =
                    scriptManager.storeChunkFileInLocal("src/test/resources", "chunkFileInLocal", mockInputStream);
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
            envID = scriptManager.setEnvironment(jsonInput);
//            scriptManager.executeFunctionTest(taskID.toString(), response.getEntity().toString(), "robot");
            assertNotNull(envID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
