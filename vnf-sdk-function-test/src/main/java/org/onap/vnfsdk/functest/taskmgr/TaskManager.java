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

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.*;
import org.eclipse.jetty.http.HttpStatus;
import org.onap.vnfsdk.functest.constants.ApplicationConstants;
import org.onap.vnfsdk.functest.db.TaskMgrCaseTblDAO;
import org.onap.vnfsdk.functest.db.TaskMgrTaskTblDAO;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.onap.vnfsdk.functest.models.CaseRecord;
import org.onap.vnfsdk.functest.models.TaskRecord;
import org.onap.vnfsdk.functest.scriptmgr.ScriptManager;
import org.onap.vnfsdk.functest.util.RestResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/functest/taskmanager")
@Api(tags = {" Function Test Task Manager "})
public class TaskManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskManager.class);

    private final TaskMgrTaskTblDAO taskMgrTaskTblDAO;
    private final TaskMgrCaseTblDAO taskMgrCaseTblDAO;
    private final ScriptManager scriptManager;

    public TaskManager(TaskMgrTaskTblDAO taskMgrTaskTblDAO, TaskMgrCaseTblDAO taskMgrCaseTblDAO, ScriptManager scriptManager) {
        this.taskMgrTaskTblDAO = taskMgrTaskTblDAO;
        this.taskMgrCaseTblDAO = taskMgrCaseTblDAO;
        this.scriptManager = scriptManager;
    }

    @Path("/onboard")
    @POST
    @ApiOperation(value = "Start Onboard Testing")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //@Timed
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @UnitOfWork
    public Response startOnboardTesting(RequestBody requestBody) {
        LOGGER.info("[Task Manager] Start Onboard Testing");
        TaskRecord taskRecord = new TaskRecord();
        CaseRecord caseRecord = new CaseRecord();

        String packageID = requestBody.packageID;

        try {
            if (taskMgrTaskTblDAO.findByPackageID(packageID).isPresent()) {
                throw new IllegalArgumentException("Already Onboard.");
            } else {
                initOnboardTesting(taskRecord, caseRecord, packageID);
                scriptManager.uploadFuncTestPackage(UUID.fromString(taskRecord.getTaskID()), UUID.fromString(taskRecord.getEnvID()), ApplicationConstants.CATALOG_URI);
            }
            return RestResponseUtil.getCreateSuccessResponse(UUID.fromString(taskRecord.getTaskID()));

        } catch (Exception e) {
            if ("Already Onboard.".equals(e.getMessage())) {
                LOGGER.error("The Package " + packageID + " is already the onboarding package.", e);
                return RestResponseUtil.getErrorResponse(packageID);
            } else {
                LOGGER.error("Start Onboard Testing Fail", e);
            }
        }
        return null;
    }

    @Path("/status/{taskID}")
    @GET
    @ApiOperation(value = "Query Function Test Status by ID")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @Timed
    @UnitOfWork
    public Response queryTestStatus(@ApiParam(value = "taskID") @PathParam("taskID") String taskID) {
        LOGGER.info("[Task Manager] Query Function Test Status by ID: {}.", taskID);

        try {

            List<TaskRecord> taskRecordList = taskMgrTaskTblDAO.findByTaskID(taskID);
            if (taskRecordList.isEmpty()) {
                throw new IllegalArgumentException(ApplicationConstants.TASK_NOT_EXIST);
            } else {

                TaskRecord taskRecord = taskRecordList.get(0);
                UUID operID = UUID.fromString(taskRecord.getOperID());

                LOGGER.info(taskRecord.getOperID());
                OperationStatus operStatus = OperationStatusHandler.getInstance().getOperStatusMap().get(operID);
                /**
                 * To check whether the operID belongs to a record in memory.
                 * If so, needs refresh db: retrieve the data from the record in memory and update the db accordingly.
                 * If not, means a historical record in db. Obtain the data from db.
                 * */
                if (null != operStatus) {
                    taskRecord.setOperFinished(operStatus.isOperFinished() ? "True" : "False");
                    taskRecord.setOperResult(operStatus.getoResultCode().toString());
                    taskRecord.setOperResultMessage(operStatus.getOperResultMessage());
                    taskMgrTaskTblDAO.saveOrUpdate(taskRecord);
                } else {
                    OperationStatus oldOperStatus = new OperationStatus();
                    oldOperStatus.setoResultCode(OperationStatus.operResultCode.valueOf(taskRecord.getOperResult()));
                    oldOperStatus.setOperResultMessage(taskRecord.getOperResultMessage());
                    oldOperStatus.setOperFinished("True".equals(taskRecord.getOperFinished()));
                    OperationStatusHandler.getInstance().setOperStatusMap(operID, oldOperStatus);
                }
                return scriptManager.getOperationResult(operID);
            }
        } catch (Exception e) {
            if ("Task Not Exist.".equals(e.getMessage())) {
                LOGGER.error("The Task " + taskID + " does not exist..!", e);
                return RestResponseUtil.getNotFoundResponse(taskID);
            } else {
                LOGGER.error("Query Function Test Status by ID Failed.", e);
            }
        }
        return null;
    }

    @Path("/result/{taskID}")
    @GET
    @ApiOperation(value = "Get Function Test Result by ID")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
            @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
            @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @Timed
    @UnitOfWork
    public Response collectTaskResult(@ApiParam(value = "taskID") @PathParam("taskID") String taskID) {
        LOGGER.info("[Task Manager] Collect Function Test Result by ID: {}.", taskID);

        try {
            CaseRecord caseRecord = taskMgrCaseTblDAO.findByTaskID(taskID);
            if (null == caseRecord) {
                throw new IllegalArgumentException(ApplicationConstants.TASK_NOT_EXIST);
            } else {
                /* To check whether we have already collected the result of Task: taskID. */
                if (ApplicationConstants.NOT_CREATED.equals(caseRecord.getTestID())) {
                    return scriptManager.downloadResults(UUID.fromString(taskID));
                } else {
                    CaseRecord oldCaseRecord = new CaseRecord();
                    oldCaseRecord.setTestID(caseRecord.getTestID());
                    oldCaseRecord.setTestResult(caseRecord.getTestResult());
                    oldCaseRecord.setTestDescription(caseRecord.getTestDescription());
                    return RestResponseUtil.getSuccessResponse(oldCaseRecord);
                }
            }
        } catch (Exception e) {
            if (ApplicationConstants.TASK_NOT_EXIST.equals(e.getMessage())) {
                LOGGER.error("The Task " + taskID + " does not exist..!", e);
                return RestResponseUtil.getNotFoundResponse(taskID);
            } else {
                LOGGER.error("Collect Function Test Result by ID Failed.", e);
            }
        }
        return null;
    }

    private void initOnboardTesting(TaskRecord taskRecord, CaseRecord caseRecord, String packageID) {
        /* Create TaskRecord entry in db */
        taskRecord.setPackageID(packageID);
        // Generate UUID for each task
        final UUID taskID = UUID.randomUUID();
        taskRecord.setTaskID(taskID.toString());
        // Setup the environment
        final UUID envID = scriptManager.setEnvironment(loadEnvConfigurations());
        taskRecord.setEnvID(envID.toString());
        taskRecord.setUploadID(ApplicationConstants.NOT_CREATED);
        taskRecord.setOperID(ApplicationConstants.NOT_CREATED);
        taskRecord.setFuncID("");
        taskRecord.setStatus(ApplicationConstants.CREATED);
        taskRecord.setOperFinished("False");
        taskRecord.setOperResult("FAILURE");
        taskRecord.setOperResultMessage("");
        taskMgrTaskTblDAO.saveOrUpdate(taskRecord);

        /* Create CaseRecord entry in db */
        caseRecord.setTaskID(taskID.toString());
        caseRecord.setFuncID("");
        caseRecord.setTestID(ApplicationConstants.NOT_CREATED);
        caseRecord.setTestResult("NULL");
        caseRecord.setTestDescription("");
        taskMgrCaseTblDAO.saveOrUpdate(caseRecord);
    }

    private String loadEnvConfigurations() {
        Map<String, String> envConfigurations;
        String strEnvConfigurations;
        String curDir = System.getProperty("user.dir");
        String confDir = curDir + File.separator + ApplicationConstants.CONF + File.separator + ApplicationConstants.ENVIRONMENT + File.separator;

        try {

            envConfigurations = new Gson().fromJson(new JsonReader(new FileReader(confDir + ApplicationConstants.ENVIRONMENT_JSON)), Map.class);
            Gson gson = new GsonBuilder().create();
            strEnvConfigurations = gson.toJson(envConfigurations);

            return strEnvConfigurations;
        } catch (IOException e) {
            LOGGER.error("Reading Environment Json data file failed or file does not exist", e);
        }
        return null;
    }

    public static class RequestBody {

        @JsonProperty("packageID")
        private String packageID;

        public String getPackageID() {
            return packageID;
        }

        public void setPackageID(String packageID) {
            this.packageID = packageID;
        }
    }

}
