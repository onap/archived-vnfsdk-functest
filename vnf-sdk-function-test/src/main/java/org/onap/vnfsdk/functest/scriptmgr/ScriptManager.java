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

import com.google.gson.Gson;
import org.onap.vnfsdk.functest.FileUtil;
import org.onap.vnfsdk.functest.TaskExecution;
import org.onap.vnfsdk.functest.constants.ApplicationConstants;
import org.onap.vnfsdk.functest.db.TaskMgrCaseTblDAO;
import org.onap.vnfsdk.functest.db.TaskMgrTaskTblDAO;
import org.onap.vnfsdk.functest.externalservice.entity.Environment;
import org.onap.vnfsdk.functest.externalservice.entity.EnvironmentMap;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.onap.vnfsdk.functest.models.CaseRecord;
import org.onap.vnfsdk.functest.models.TaskRecord;
import org.onap.vnfsdk.functest.responsehandler.TestResult;
import org.onap.vnfsdk.functest.responsehandler.TestResultMap;
import org.onap.vnfsdk.functest.responsehandler.VnfFuncTestResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.*;

public class ScriptManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptManager.class);
    private final TaskMgrTaskTblDAO taskMgrTaskTblDAO;
    private final TaskMgrCaseTblDAO taskMgrCaseTblDAO;

    public ScriptManager(TaskMgrTaskTblDAO taskMgrTaskTblDAO, TaskMgrCaseTblDAO taskMgrCaseTblDAO) {
        this.taskMgrTaskTblDAO = taskMgrTaskTblDAO;
        this.taskMgrCaseTblDAO = taskMgrCaseTblDAO;
    }

    /**
     * Convert the stream to File Name<br/>
     *
     * @param dirName             - Directory name
     * @param fileName            - FileName
     * @param uploadedInputStream - Input Stream
     * @return - File Path
     * @throws IOException - Exception while writing file
     * @since VNFSDK
     */
    public static String storeChunkFileInLocal(String dirName, String fileName, InputStream uploadedInputStream)
            throws IOException {
        File tmpDir = new File(dirName);
        LOGGER.info("tmpdir=" + dirName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        StringTokenizer st = new StringTokenizer(fileName, "/");
        String actualFile = null;
        while (st.hasMoreTokens()) {
            actualFile = st.nextToken();
        }
        File file = new File(tmpDir + File.separator + actualFile);
        OutputStream os = null;
        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            os = new FileOutputStream(file, true);
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            os.flush();
            return file.getAbsolutePath();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public UUID setEnvironment(String env) {
        LOGGER.info("[Scrip Manager] Set Environment.");

        try {

            // Generate UUID for each environment
            final UUID envID = UUID.randomUUID();

            // Convert input string to Environment class
//            ObjectMapper mapper = new ObjectMapper();
//            Environment envObj = mapper.readValue(env, Environment.class);
            Environment envObj = new Gson().fromJson(env, Environment.class);
            if (null == envObj) {
                // Converting input to Env object failed
                return null;
            }

            // Set to the environment map
            EnvironmentMap.getInstance().addEnv(envID, envObj);
            LOGGER.info(EnvironmentMap.getInstance().getEnv(envID).getPassword());

            // Send the envID back
            return envID;

        } catch (Exception e) {
            LOGGER.error("Setting the Environment Fail", e);
        }

        return null;
    }

    public UUID uploadFuncTestPackage(UUID taskID, UUID envID, String url) {
        LOGGER.info("[Scrip Manager] Upload Function Test Package.");

        try {
            URL oracle = new URL(url);

            InputStream fis = new BufferedInputStream(oracle.openStream());

            // Convert the stream to script folder
            String nl = File.separator;
            String filePath = storeChunkFileInLocal("temp", "TempFile.zip", fis);

            // Unzip the folder
            String tempDir = System.getProperty("user.dir") + nl + "temp";
            List<String> list = FileUtil.unzip(filePath, tempDir);
            LOGGER.info("File path=" + filePath);

            String[] directories = FileUtil.getDirectory(tempDir);
            LOGGER.info("tempdir=" + tempDir);
            if (null != directories && 0 != directories.length) {
                filePath = tempDir + File.separator + directories[0];
            } else {
                filePath = tempDir;
            }

            // generate UUID for the upload
            final UUID uploadID = UUID.randomUUID();
            List<TaskRecord> taskRecordList = taskMgrTaskTblDAO.findByTaskID(taskID.toString());
            TaskRecord taskRecord = taskRecordList.get(0);
            taskRecord.setUploadID(uploadID.toString());
            taskRecord.setOperID(uploadID.toString());
            taskMgrTaskTblDAO.saveOrUpdate(taskRecord);

            final String finalPath = filePath;

            ExecutorService es = Executors.newFixedThreadPool(3);
            Future<Integer> future = es.submit(new Callable<Integer>() {

                @Override
                public Integer call() throws Exception {

                    new TaskExecution().uploadScript(finalPath, envID, uploadID);
                    return 0;
                }
            });

            try {
                if (0 == future.get(5, TimeUnit.SECONDS)) {
                    LOGGER.info("ExecutorService Done");

                    OperationStatus operStatus = OperationStatusHandler.getInstance().getOperStatusMap().get(uploadID);
                    taskRecord.setOperFinished(operStatus.isOperFinished() ? "True" : "False");
                    taskRecord.setOperResult(operStatus.getoResultCode().toString());
                    taskRecord.setOperResultMessage(operStatus.getOperResultMessage());
                    taskMgrTaskTblDAO.saveOrUpdate(taskRecord);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("uploadFuncTestPackage InterruptedException", e);
            } catch (ExecutionException e) {
                LOGGER.error("uploadFuncTestPackage ExecutionException", e);
            } catch (TimeoutException e) {
                LOGGER.error("uploadFuncTestPackage TimeoutException", e);
            }

            return uploadID;

        } catch (IOException e) {
            LOGGER.error(ApplicationConstants.RUN_SCRIPT_EXECUTE_CMD, e);
        }

        return null;
    }

    public Response getOperationResult(UUID operID) {
        LOGGER.info("[Script Manager] Query functest Status by ID." + operID);

        return OperationStatusHandler.getInstance().getOperationStatus(operID);
    }

    public Response downloadResults(UUID taskID) {
        LOGGER.info("[Script Manager] Download functest Result by ID: " + taskID);

        Response resp = VnfFuncTestResponseHandler.getInstance().downloadResults(taskID.toString());

        try {
            TestResult testResult = TestResultMap.getInstance().getTestResultMap().get(taskID).get(0);

            CaseRecord caseRecord = taskMgrCaseTblDAO.findByTaskID(taskID.toString());
            caseRecord.setTestID(testResult.getName());
            caseRecord.setTestResult(testResult.getStatus());
            caseRecord.setTestDescription(testResult.getDescription());
            taskMgrCaseTblDAO.saveOrUpdate(caseRecord);
        } catch (Exception e) {
            LOGGER.error("Collect Functest Result by ID Failed.", e);
        }
        return resp;
    }


}
