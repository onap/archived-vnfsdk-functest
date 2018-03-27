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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.onap.vnfsdk.functest.FileUtil;
import org.onap.vnfsdk.functest.util.RestResponseUtil;
import org.onap.vnfsdk.functest.util.ZipCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class VnfFuncTestResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(VnfFuncTestResponseHandler.class);
    private static int actioninProgress = 21;
    private static int error = 22;
    private static String resultFileName = "output.xml";
    private static String resultPathKey = "DIR_RESULT";
    private static Map<String, String> mapConfigValues;
    private static VnfFuncTestResponseHandler vnfFuncRspHandler;

    private VnfFuncTestResponseHandler() {
    }

    public static VnfFuncTestResponseHandler getInstance() {
        if (vnfFuncRspHandler == null) {
            vnfFuncRspHandler = new VnfFuncTestResponseHandler();
            loadConfigurations();
        }
        return vnfFuncRspHandler;
    }

    public static void setConfigMap(Map<String, String> inMapConfigValues) {
        mapConfigValues = inMapConfigValues;
    }

    @SuppressWarnings("unchecked")
    private static void loadConfigurations() {
        String curDir = System.getProperty("user.dir");
        String confDir = curDir + File.separator + "conf" + File.separator + "robot" + File.separator;
//        ObjectMapper mapper = new ObjectMapper();

        try {
//            mapConfigValues = mapper.readValue(new FileInputStream(confDir + "robotMetaData.json"), Map.class);
            mapConfigValues = new Gson().fromJson(new JsonReader(new FileReader(confDir + "robotMetaData.json")), Map.class);
        } catch (IOException e) {
            logger.error("Reading Json Meta data file failed or file do not exist", e);
        }
    }

    public Response getResponseByFuncTestId(String funcTestId) {

        if ((null == mapConfigValues) || (null == mapConfigValues.get(resultPathKey))) {
            logger.warn("Result Store path not configured !!!");
            return RestResponseUtil.getErrorResponse(error);
        }

        String resultPath = mapConfigValues.get(resultPathKey);

        /*
         * Check whether file Exists for the Request received !!!
         * -----------------------------------------------------
         */
        String fileName = resultPath + File.separator + funcTestId;
        if (!FileUtil.checkFileExist(fileName)) {
            logger.warn("Requested function Test result not available/In-Progress !!!");
            return RestResponseUtil.getErrorResponse(actioninProgress);
        }

        String zipFileName = fileName + ".zip";
        try {
            new ZipCompressor(zipFileName).compress(fileName);
        } catch (IOException e) {
            logger.error("getResponseByFuncTestId ", e);
        }

        /*
         * Convert Zip-file byteCode and to response !!!
         * -----------------------------------------------------
         */
        byte[] byteArrayFile = FileUtil.convertZipFiletoByteArray(zipFileName);

        if (null != byteArrayFile) {

            /*
             * Delete Result folders present if Success !!!
             * ----------------------------------------------
             */
            FileUtil.deleteFile(zipFileName);
            /*
             * Later will delete this file
             */
            logger.warn("Requested function Test result Success !!!");
            return RestResponseUtil.getSuccessResponse(byteArrayFile);
        } else {
            logger.warn("Requested function Test result Failed !!!");
            return RestResponseUtil.getErrorResponse(error);
        }
    }

    public Response downloadResults(String funcTestId) {

        if ((null == mapConfigValues) || (null == mapConfigValues.get(resultPathKey))) {
            logger.warn("Result Store path not configured !!!");
            return RestResponseUtil.getErrorResponse(error);
        }

        String resultPath = mapConfigValues.get(resultPathKey);
        String resultfileName = resultPath + File.separator + funcTestId + File.separator + resultFileName;
        logger.info(resultfileName);
        TestResultParser oTestResultParser = new TestResultParser();
        List<TestResult> resultList = oTestResultParser.populateResultList(funcTestId, resultfileName);
        return (!resultList.isEmpty()) ? RestResponseUtil.getSuccessResponse(resultList)
                : RestResponseUtil.getErrorResponse(error);
    }
}
