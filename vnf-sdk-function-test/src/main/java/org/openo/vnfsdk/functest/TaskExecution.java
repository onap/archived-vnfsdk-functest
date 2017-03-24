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

package org.openo.vnfsdk.functest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.SystemUtils;
import org.openo.vnfsdk.functest.externalservice.entity.Environment;
import org.openo.vnfsdk.functest.externalservice.entity.EnvironmentMap;
import org.openo.vnfsdk.functest.externalservice.entity.OperationStatus;
import org.openo.vnfsdk.functest.externalservice.entity.OperationStatus.operResultCode;
import org.openo.vnfsdk.functest.externalservice.entity.OperationStatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskExecution {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecution.class);

    public void executeScript(String dirPath, UUID uniqueKey) {

        String nl = File.separator;
        String curDir = System.getProperty("user.dir");
        String confDir = curDir + nl + "conf" + nl + "robot" + nl;

        // Read the MetaData from the VNF package
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> mapValues = null;
        try {
            mapValues = mapper.readValue(new FileInputStream(confDir + "robotMetaData.json"), Map.class);
        } catch(IOException e) {

            LOGGER.error("Reading Json Meta data file failed or file do not exist", e);
            return;
        }

        // Form the variables for the upload, transfer and execute command
        String scriptDirName = new File(dirPath).getName();
        mapValues.put("SCRIPT_DIR", dirPath);

        String remoteScriptDir = mapValues.get("DIR_REMOTE") + scriptDirName;
        String remoteScriptResult = remoteScriptDir + "/" + "output ";
        mapValues.put("DIR_REMOTE_RESULT", remoteScriptResult);

        String dirResult = mapValues.get("DIR_RESULT") + uniqueKey;
        mapValues.put("DIR_RESULT", dirResult);

        String remoteScriptFile = remoteScriptDir + "/" + mapValues.get("MAIN_SCRIPT");
        String remoteArgs = "--argumentfile " + remoteScriptDir + "/" + "config.args ";
        String remoteCommand = "robot " + "-d " + remoteScriptResult + remoteArgs + remoteScriptFile;
        mapValues.put("REMOTE_COMMAND", "\"" + remoteCommand + "\"");

        String robotvariables = "";
        for(Entry<String, String> values : mapValues.entrySet()) {

            robotvariables = robotvariables + " -v " + values.getKey() + ":" + values.getValue() + " ";
        }

        // Execute the command
        String argumentFilePath = confDir + "config.args ";
        String robotScript = confDir + "RemoteConnection.robot";

        String shellcommand = "cmd.exe /c ";
        if(SystemUtils.IS_OS_LINUX) {
            shellcommand = "bash ";
        }

        Process process = null;
        InputStream inputStream = null;
        int ch;
        try {
            String command =
                    shellcommand + "robot --argumentfile " + argumentFilePath + robotvariables + " " + robotScript;
            process = Runtime.getRuntime().exec(command);
            inputStream = process.getInputStream();
            while((ch = inputStream.read()) != -1) {
                LOGGER.info("character ..." + Integer.toString(ch));
            }

        } catch(Exception e) {
            LOGGER.error("TaskExecution ... executeScript() ... [Exception] ...", e);
        }
    }

    public void executeRobotScript(UUID envId, UUID uploadId, UUID executeId, String frameworktype) {

        String nl = File.separator;
        String curDir = System.getProperty("user.dir");
        String confDir = curDir + nl + "conf" + nl + "robot" + nl;

        // Read the MetaData from the VNF package
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> mapValues = null;
        try {
            mapValues = mapper.readValue(new FileInputStream(confDir + "robotMetaData.json"), Map.class);
        } catch(IOException e) {

            LOGGER.error("Reading Json Meta data file failed or file do not exist", e);
            return;
        }

        // Get environment of given UUID
        Environment functestEnv = EnvironmentMap.getInstance().getEnv(envId);

        String remoteDir = functestEnv.getPath() + mapValues.get("SCRIPT_NAME");
        String remoteScriptFile = remoteDir + "/" + mapValues.get("MAIN_SCRIPT");
        String remoteScriptResult = remoteDir + "/" + "output ";
        String dirResult = mapValues.get("DIR_RESULT") + executeId;

        // set the argument parameters
        String remoteArgs = "";
        remoteArgs = remoteArgs + " -v " + "NODE_IP" + ":" + functestEnv.getRemoteIp() + " ";
        remoteArgs = remoteArgs + " -v " + "NODE_USERNAME" + ":" + functestEnv.getUserName() + " ";
        remoteArgs = remoteArgs + " -v " + "NODE_PASSWORD" + ":" + functestEnv.getPassword() + " ";

        String remoteCommand = "robot " + "-d " + remoteScriptResult + remoteArgs + remoteScriptFile;

        // set the parameters required by the execute script
        remoteCommand = "\"" + remoteCommand + "\"";
        remoteArgs = remoteArgs + " -v " + "REMOTE_COMMAND" + ":" + remoteCommand + " ";

        remoteArgs = remoteArgs + " -v " + "DIR_RESULT" + ":" + dirResult + " ";
        remoteArgs = remoteArgs + " -v " + "DIR_REMOTE_RESULT" + ":" + remoteScriptResult + " ";

        // Execute script directory
        String robotScript = confDir + "execute.robot";
        String shellcommand = "cmd.exe /c ";
        if(SystemUtils.IS_OS_LINUX) {
            shellcommand = "bash ";
        }
        Process process = null;
        InputStream inputStream = null;
        int ch;
        try {
            String command = shellcommand + "robot" + remoteArgs + robotScript;
            process = Runtime.getRuntime().exec(command);
            inputStream = process.getInputStream();
            while((ch = inputStream.read()) != -1) {
                LOGGER.info("character ..." + Integer.toString(ch));
            }
        } catch(Exception e) {
            LOGGER.error("TaskExecution ... executeScript() ... [Exception] ...", e);
        }

        OperationStatus operstatus = new OperationStatus();
        operstatus.setoResultCode(operResultCode.SUCCESS);
        operstatus.setOperResultMessage("Execute function test finished");
        operstatus.setOperFinished(true);
        OperationStatusHandler.getInstance().setOperStatusMap(executeId, operstatus);
    }

    public void uploadScript(String dirPath, UUID UUIDEnv, UUID UUIDUpload) {

        String nl = File.separator;
        String curDir = System.getProperty("user.dir");
        String confDir = curDir + nl + "conf" + nl + "robot" + nl;

        // Read the MetaData from the VNF package
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> mapValues = null;
        try {

            mapValues = mapper.readValue(new FileInputStream(confDir + "robotMetaData.json"), Map.class);
        } catch(Exception e) {

            LOGGER.error("Reading Json Meta data file failed or file do not exist", e);
            return;
        }

        // Form the variables for the upload, transfer and execute command
        String scriptDirName = new File(dirPath).getName();
        mapValues.put("SCRIPT_DIR", dirPath);

        String robotvariables = "";
        for(Entry<String, String> values : mapValues.entrySet()) {

            robotvariables = robotvariables + " -v " + values.getKey() + ":" + values.getValue() + " ";
        }

        // Append the Func test environment variables
        Environment functestEnv = EnvironmentMap.getInstance().getEnv(UUIDEnv);
        robotvariables = robotvariables + " -v " + "NODE_IP" + ":" + functestEnv.getRemoteIp() + " ";
        robotvariables = robotvariables + " -v " + "NODE_USERNAME" + ":" + functestEnv.getUserName() + " ";
        robotvariables = robotvariables + " -v " + "NODE_PASSWORD" + ":" + functestEnv.getPassword() + " ";
        robotvariables = robotvariables + " -v " + "DIR_REMOTE" + ":" + functestEnv.getPath() + " ";

        // Execute the command
        String robotScript = confDir + "upload.robot";

        String shellcommand = "cmd.exe /c ";
        if(SystemUtils.IS_OS_LINUX) {
            shellcommand = "bash ";
        }

        Process process = null;
        InputStream inputStream = null;
        int ch;
        try {
            String command = shellcommand + "robot " + robotvariables + robotScript;
            process = Runtime.getRuntime().exec(command);
            inputStream = process.getInputStream();
            while((ch = inputStream.read()) != -1) {
                LOGGER.info("character ..." + Integer.toString(ch));
            }

        } catch(Exception e) {
            LOGGER.error("TaskExecution ... executeScript() ... [Exception] ...", e);
        }

        OperationStatus operstatus = new OperationStatus();
        operstatus.setoResultCode(operResultCode.SUCCESS);
        operstatus.setOperResultMessage("");
        operstatus.setOperFinished(true);
        OperationStatusHandler.getInstance().setOperStatusMap(UUIDUpload, operstatus);

    }

}
