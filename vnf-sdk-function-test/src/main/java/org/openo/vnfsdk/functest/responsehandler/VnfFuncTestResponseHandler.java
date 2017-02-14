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

package org.openo.vnfsdk.functest.responsehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.openo.vnfsdk.functest.FileUtil;
import org.openo.vnfsdk.functest.util.RestResponseUtil;
import org.openo.vnfsdk.functest.util.ZipCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VnfFuncTestResponseHandler {

    private static int actioninProgress = 21;

    private static int error = 22;

    private static String resultpathkey = "DIR_RESULT";

    private static Map<String, String> mapConfigValues;

    private static VnfFuncTestResponseHandler vnfFuncRspHandler;

    private static final Logger logger = LoggerFactory.getLogger(VnfFuncTestResponseHandler.class);

    private VnfFuncTestResponseHandler() {
    }

    public static VnfFuncTestResponseHandler getInstance() {
        if(vnfFuncRspHandler == null) {
            vnfFuncRspHandler = new VnfFuncTestResponseHandler();
            loadConfigurations();
        }
        return vnfFuncRspHandler;
    }

    public void setConfigMap(Map<String, String> inMapConfigValues) {
        mapConfigValues = inMapConfigValues;
    }

    public Response getResponseByFuncTestId(String funcTestId) {

        if((null == mapConfigValues) || (null == mapConfigValues.get(resultpathkey))) {
            logger.warn("Result Store path not configfured !!!");
            return RestResponseUtil.getErrorResponse(error);
        }

        String resultPath = mapConfigValues.get(resultpathkey);

        // Check whether file Exists for the Request received !!!
        // -----------------------------------------------------
        String fileName = resultPath + File.separator + funcTestId;
        if(!FileUtil.checkFileExist(fileName)) {
            logger.warn("Resquested function Test result not avaliable/In-Progress !!!");
            return RestResponseUtil.getErrorResponse(actioninProgress);
        }

        String zipFileName = fileName + ".zip";
        new ZipCompressor(zipFileName).compress(fileName);

        // Convert Zip-file byteCode and to response !!!
        // -----------------------------------------------------
        byte[] byteArrayFile = FileUtil.convertZipFiletoByteArray(zipFileName);

        if(null != byteArrayFile) {

            // Delete Result folders present if Success !!!
            // ----------------------------------------------
            FileUtil.deleteFile(zipFileName);
            // Later will delete this file..FileUtil.deleteDirectory(fileName);

            logger.warn("Resquested function Test result Sucess !!!");
            return RestResponseUtil.getSuccessResponse(byteArrayFile);
        } else {
            logger.warn("Resquested function Test result Faiuled !!!");
            return RestResponseUtil.getErrorResponse(error);
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadConfigurations() {
        String curDir = System.getProperty("user.dir");
        String confDir = curDir + File.separator + "conf" + File.separator + "robot" + File.separator;
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapConfigValues = mapper.readValue(new FileInputStream(confDir + "robotMetaData.json"), Map.class);
        } catch(IOException e) {
            logger.error("Reading Json Meta data file failed or file do not exist" + e.getMessage());
            return;
        }
    }
}
