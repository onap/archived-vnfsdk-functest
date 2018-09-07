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

package org.onap.vnfsdk.functest;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.SystemUtils;
import org.onap.vnfsdk.functest.constants.ApplicationConstants;
import org.onap.vnfsdk.functest.externalservice.entity.Environment;
import org.onap.vnfsdk.functest.externalservice.entity.EnvironmentMap;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatus.operResultCode;
import org.onap.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class TaskExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecution.class);

    public void executeScript(String dirPath, UUID uniqueKey) {

        String nl = File.separator;
        String curDir = System.getProperty(ApplicationConstants.USER_DIR);
        String confDir = curDir + nl + ApplicationConstants.CONF + nl + ApplicationConstants.ROBOT + nl;

        // Read the MetaData from the VNF package
        Map<String, String> mapValues = null;
        try {
            mapValues = new Gson().fromJson(new JsonReader(new FileReader(confDir + ApplicationConstants.ROBOTMETADATA_JSON)), Map.class);
        } catch (IOException e) {

            LOGGER.error(ApplicationConstants.JSON_METADATA_FILE_FAILED, e);
            return;
        }

        // Form the variables for the upload, transfer and execute command
        String scriptDirName = new File(dirPath).getName();
        mapValues.put("SCRIPT_DIR", dirPath);

        String remoteScriptDir = mapValues.get("DIR_REMOTE") + scriptDirName;
        String remoteScriptResult = remoteScriptDir + "/" + "output ";
        mapValues.put("DIR_REMOTE_RESULT", remoteScriptResult);

        String dirResult = mapValues.get(ApplicationConstants.DIR_RESULT) + uniqueKey;
        mapValues.put(ApplicationConstants.DIR_RESULT, dirResult);

        String remoteScriptFile = remoteScriptDir + "/" + mapValues.get("MAIN_SCRIPT");
        String remoteArgs = "--argumentfile " + remoteScriptDir + "/" + "config.args ";
        String remoteCommand =
                ApplicationConstants.ROBOT_SPACE + "-d " + remoteScriptResult + remoteArgs + remoteScriptFile;
        mapValues.put("REMOTE_COMMAND", "\"" + remoteCommand + "\"");

        String robotvariables = "";
        for (Entry<String, String> values : mapValues.entrySet()) {

            robotvariables = robotvariables + " -v " + values.getKey() + ":" + values.getValue() + " ";
        }

        // Execute the command
        String argumentFilePath = confDir + "config.args ";
        String robotScript = confDir + "RemoteConnection.robot";

        Process process = null;
        InputStream inputStream = null;
        int ch;
        try {
            String command = "robot --argumentfile " + argumentFilePath + robotvariables + " " + robotScript;
            LOGGER.info("Command execute to execute the script:" + command);
            process = Runtime.getRuntime().exec(new String[]{getShellCommand(), getShellArg(), command});
            if (process != null) {
                process.waitFor();
                inputStream = process.getInputStream();
            }
            if (inputStream != null){
                while ((ch = inputStream.read()) != -1) {
                    LOGGER.info(ApplicationConstants.CHARACTER + Integer.toString(ch));
                }
            }
        } catch (Exception e) {
            LOGGER.error(ApplicationConstants.TASKEXE_EXESCRIPT_EXCEPTION, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("executeScript", e);
                }
            }
        }
    }

    public void executeRobotScript(UUID envId, UUID executeId) {

        String nl = File.separator;
        String curDir = System.getProperty(ApplicationConstants.USER_DIR);
        String confDir = curDir + nl + ApplicationConstants.CONF + nl + ApplicationConstants.ROBOT + nl;

        // Read the MetaData from the VNF package
        Map<String, String> mapValues = null;
        try {
            mapValues = new Gson().fromJson(new JsonReader(new FileReader(confDir + ApplicationConstants.ROBOTMETADATA_JSON)), Map.class);
        } catch (IOException e) {

            LOGGER.error(ApplicationConstants.JSON_METADATA_FILE_FAILED, e);
            return;
        }

        String remoteDir = "";
        String remoteArgs = "";

        // Get environment of given UUID
        Environment functestEnv = EnvironmentMap.getInstance().getEnv(envId);
        if (null != functestEnv) {
            LOGGER.info("Function Test Environment path,Path = " + functestEnv.getPath());
            remoteDir = functestEnv.getPath() + mapValues.get("SCRIPT_NAME");
            // set the argument parameters
            remoteArgs = remoteArgs + " -v " + "NODE_IP" + ":" + functestEnv.getRemoteIp() + " ";
            remoteArgs = remoteArgs + " -v " + "NODE_USERNAME" + ":" + functestEnv.getUserName() + " ";
            remoteArgs = remoteArgs + " -v " + "NODE_PASSWORD" + ":" + functestEnv.getPassword() + " ";
        } else {
            LOGGER.error("Function Test Environment details are empty,EnvID = " + envId);
        }

        String remoteConfigArgs = remoteDir + "/" + "config.args ";
        String remoteScriptFile = remoteDir + "/" + mapValues.get("MAIN_SCRIPT");
        String remoteScriptResult = remoteDir + "/" + "output ";
        String dirResult = mapValues.get(ApplicationConstants.DIR_RESULT) + executeId;

        String remoteCommand = ApplicationConstants.ROBOT_SPACE + "-d " + remoteScriptResult + "--argumentfile "
                + remoteConfigArgs + remoteScriptFile;

        // set the parameters required by the execute script
        remoteCommand = "\"" + remoteCommand + "\"";
        remoteArgs = remoteArgs + " -v " + "REMOTE_COMMAND" + ":" + remoteCommand + " ";

        remoteArgs = remoteArgs + " -v " + ApplicationConstants.DIR_RESULT + ":" + dirResult + " ";
        remoteArgs = remoteArgs + " -v " + "DIR_REMOTE_RESULT" + ":" + remoteScriptResult + " ";

        // Execute script directory
        String robotScript = confDir + "execute.robot";

        Process process = null;
        InputStream inputStream = null;
        int ch;
        try {
            String command = ApplicationConstants.ROBOT + remoteArgs + robotScript;
            LOGGER.info("Command execute to execute the script:" + command);
            process = Runtime.getRuntime().exec(new String[]{getShellCommand(), getShellArg(), command});
            if (process != null) {
                process.waitFor();
                inputStream = process.getInputStream();
            }
            if (inputStream != null) {
                while ((ch = inputStream.read()) != -1) {
                    LOGGER.info(ApplicationConstants.CHARACTER + Integer.toString(ch));
                }
            }
        } catch (Exception e) {
            LOGGER.error(ApplicationConstants.TASKEXE_EXESCRIPT_EXCEPTION, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e){
                    LOGGER.error("executeRobotScript IOException", e);
                }
            }
        }

        OperationStatus operstatus = new OperationStatus();
        operstatus.setoResultCode(operResultCode.SUCCESS);
        operstatus.setOperResultMessage("Execute function test finished");
        operstatus.setOperFinished(true);
        OperationStatusHandler.getInstance().setOperStatusMap(executeId, operstatus);
    }

    public void uploadScript(String dirPath, UUID uuidEnv, UUID uuidUpload) {

        String nl = File.separator;
        String curDir = System.getProperty(ApplicationConstants.USER_DIR);
        String confDir = curDir + nl + ApplicationConstants.CONF + nl + ApplicationConstants.ROBOT + nl;

        // Read the MetaData from the VNF package

        Map<String, String> mapValues = null;
        try {
            mapValues = new Gson().fromJson(new JsonReader(new FileReader(confDir + ApplicationConstants.ROBOTMETADATA_JSON)), Map.class);
        } catch (Exception e) {

            LOGGER.error(ApplicationConstants.JSON_METADATA_FILE_FAILED, e);
            return;
        }

        // Form the variables for the upload, transfer and execute command
        mapValues.put("SCRIPT_DIR", dirPath);

        String robotvariables = "";
        for (Entry<String, String> values : mapValues.entrySet()) {

            robotvariables = robotvariables + " -v " + values.getKey() + ":" + values.getValue() + " ";
        }

        // Append the Func test environment variables
        Environment functestEnv = EnvironmentMap.getInstance().getEnv(uuidEnv);
        robotvariables = robotvariables + " -v " + "NODE_IP" + ":" + functestEnv.getRemoteIp() + " ";
        robotvariables = robotvariables + " -v " + "NODE_USERNAME" + ":" + functestEnv.getUserName() + " ";
        robotvariables = robotvariables + " -v " + "NODE_PASSWORD" + ":" + functestEnv.getPassword() + " ";
        robotvariables = robotvariables + " -v " + "DIR_REMOTE" + ":" + functestEnv.getPath() + " ";

        // Execute the command
        String robotScript = confDir + "upload.robot";

        Process process = null;
        InputStream inputStream = null;
        int ch;
        try {
            String command = ApplicationConstants.ROBOT_SPACE + robotvariables + robotScript;
            LOGGER.info("Command execute to upload the script:" + command);
            process = Runtime.getRuntime().exec(new String[]{getShellCommand(), getShellArg(), command});
            if (process != null) {
                process.waitFor();
                inputStream = process.getInputStream();
            }
            if (inputStream != null) {
                while ((ch = inputStream.read()) != -1) {
                    LOGGER.info(ApplicationConstants.CHARACTER + Integer.toString(ch));
                }
            }
        } catch (Exception e) {
            LOGGER.error(ApplicationConstants.TASKEXE_EXESCRIPT_EXCEPTION, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e){
                    LOGGER.error("uploadScript IOException", e);
                }
            }
        }

        OperationStatus operstatus = new OperationStatus();
        operstatus.setoResultCode(operResultCode.SUCCESS);
        operstatus.setOperResultMessage("");
        operstatus.setOperFinished(true);
        OperationStatusHandler.getInstance().setOperStatusMap(uuidUpload, operstatus);

    }

    private String getShellCommand() {

        String shellcommand = ApplicationConstants.SHELL_COMMAND;
        if (SystemUtils.IS_OS_LINUX) {
            shellcommand = ApplicationConstants.SHELL_COMMAND_BASH;
        }

        return shellcommand;
    }

    private String getShellArg() {

        String commandArg = "/c";
        if (SystemUtils.IS_OS_LINUX) {
            commandArg = "-c";
        }

        return commandArg;
    }

}
