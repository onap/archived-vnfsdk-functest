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

package org.onap.vnfsdk.functest.taskmgr;

import io.dropwizard.testing.junit.DAOTestRule;
import javafx.concurrent.Task;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.onap.vnfsdk.functest.db.TaskMgrCaseTblDAO;
import org.onap.vnfsdk.functest.db.TaskMgrTaskTblDAO;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.onap.vnfsdk.functest.models.CaseRecord;
import org.onap.vnfsdk.functest.models.TaskRecord;
import org.onap.vnfsdk.functest.responsehandler.TestResult;
import org.onap.vnfsdk.functest.scriptmgr.ScriptManager;
import org.onap.vnfsdk.functest.util.RestResponseUtil;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskManagerTest {

    @Rule
    public DAOTestRule daoTestRule = DAOTestRule.newBuilder()
            .addEntityClass(TaskRecord.class)
            .addEntityClass(CaseRecord.class)
            .build();
    private TaskManager taskManager;
    private ScriptManager scriptManager;
    private TaskMgrTaskTblDAO taskMgrTaskTblDAO;
    private TaskMgrCaseTblDAO taskMgrCaseTblDAO;
    private Response response = null;
    private TaskManager.RequestBody requestBody;
    private String packageID = "1234567890";
    private String taskID = "c0a1a373-8635-484d-bc6c-307a606cb8a1";
    private String envID = "0034b3f3-caea-4138-b186-e260a0cc509c";
    private String uploadID = "3dbf7978-0e5d-4fa8-ade7-bf6450b54a9b";
    private String operID = "3dbf7978-0e5d-4fa8-ade7-bf6450b54a9b";
    private String funcID = "";
    private String status = "CREATED";
    private String operFinished = "True";
    private String operResult = "SUCCESS";
    private String operResultMessage = "";
    private String testID = "NOT CREATED";
    private String testResult = "NULL";
    private String testDescription = "";

    @Before
    public void setUp() throws Exception {
        taskMgrTaskTblDAO = new TaskMgrTaskTblDAO(daoTestRule.getSessionFactory());
        taskMgrCaseTblDAO = new TaskMgrCaseTblDAO(daoTestRule.getSessionFactory());
        scriptManager = new ScriptManager(taskMgrTaskTblDAO, taskMgrCaseTblDAO);
        taskManager = new TaskManager(taskMgrTaskTblDAO, taskMgrCaseTblDAO, scriptManager);
        requestBody = new TaskManager.RequestBody();
        requestBody.setPackageID("1234567890");
    }

    @Test
    public void testStartOnboardTestingPackageIDAbsentInDB() {
        new MockUp<ScriptManager>() {
            @Mock
            public UUID uploadFuncTestPackage(UUID taskID, UUID envID, String url) {
                return UUID.randomUUID();
            }
        };

        try {
            response = taskManager.startOnboardTesting(requestBody);
            assertNotNull(response);
            assertEquals(201, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStartOnboardTestingPackageIDAlreadyPresentInDB() {
        try {
            daoTestRule.inTransaction(() -> taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage)));
            response = taskManager.startOnboardTesting(requestBody);
            assertNotNull(response);
            assertEquals(500, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryTestStatusPresentInDB() {
        try {
            daoTestRule.inTransaction(() -> taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage)));
            response = taskManager.queryTestStatus(taskID);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryTestStatusPresentInOperationStatus() {
        try {
            daoTestRule.inTransaction(() -> taskMgrTaskTblDAO.saveOrUpdate(new TaskRecord(packageID, taskID, envID, uploadID, operID, funcID, status, operFinished, operResult, operResultMessage)));
            UUID operID = UUID.fromString(taskMgrTaskTblDAO.findByTaskID(taskID).get(0).getOperID());
            OperationStatus operStatus = new OperationStatus();
            operStatus.setoResultCode(OperationStatus.operResultCode.SUCCESS);
            operStatus.setOperResultMessage("Execute function test finished");
            operStatus.setOperFinished(true);
            OperationStatusHandler.getInstance().setOperStatusMap(operID, operStatus);

            response = taskManager.queryTestStatus(taskID);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryTestStatusAbsent() {
        try {
            response = taskManager.queryTestStatus(taskID);
            assertNotNull(response);
            assertEquals(404, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCollectTaskResultCreated() {
        try {
            testID = "INTEL";
            daoTestRule.inTransaction(() -> taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription)));
            response = taskManager.collectTaskResult(taskID);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCollectTaskResultUncreated() {
        new MockUp<ScriptManager>() {
            @Mock
            public Response downloadResults(UUID taskID) {
                return RestResponseUtil.getSuccessResponse(null);
            }
        };
        try {
            daoTestRule.inTransaction(() -> taskMgrCaseTblDAO.saveOrUpdate(new CaseRecord(taskID, funcID, testID, testResult, testDescription)));
            response = taskManager.collectTaskResult(taskID);
            assertNotNull(response);
            assertEquals(200, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCollectTaskResultAbsent() {
        try {
            response = taskManager.collectTaskResult(taskID);
            assertNotNull(response);
            assertEquals(404, response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInitOnboardTesting() {
        TaskRecord taskRecord = new TaskRecord();
        CaseRecord caseRecord = new CaseRecord();

        try {
            TaskManager taskManagerObj = new TaskManager(taskMgrTaskTblDAO, taskMgrCaseTblDAO, scriptManager);
            Method m = taskManagerObj.getClass().getDeclaredMethod("initOnboardTesting", new Class[]{TaskRecord.class, CaseRecord.class, String.class});
            m.setAccessible(true);
            m.invoke(taskManagerObj, taskRecord, caseRecord, packageID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
