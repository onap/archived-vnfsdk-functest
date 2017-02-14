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

import javax.ws.rs.core.Response;

import org.openo.vnfsdk.functest.FileUtil;
import org.openo.vnfsdk.functest.util.RestResponseUtil;
import org.openo.vnfsdk.functest.util.ZipCompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VnfFuncTestResponseHandler {

    private static boolean deleteFileEnabled = false;

    private static String resultPath = "D:\\Pitchi_docs\\remote\\";

    private static VnfFuncTestResponseHandler vnfFuncRspHandler;

    private static final Logger logger = LoggerFactory.getLogger(VnfFuncTestResponseHandler.class);

    private VnfFuncTestResponseHandler() {
    }

    public static VnfFuncTestResponseHandler getInstance() {
        if(vnfFuncRspHandler == null) {
            vnfFuncRspHandler = new VnfFuncTestResponseHandler();
        }
        return vnfFuncRspHandler;
    }

    public Response getResponseByFuncTestId(String funcTestId) {
        // Check whether file Exists for the Request received !!!
        // -----------------------------------------------------
        String fileName = generateFilePath(funcTestId);
        if(!FileUtil.checkFileExist(fileName)) {
            logger.warn("Resquested function Test result not avaliable/In-Progress !!!");
            return RestResponseUtil.getErrorResponse(21);
        }

        String zipFileName = resultPath + funcTestId + ".zip";
        new ZipCompressor(zipFileName).compress(fileName);

        // Convert Zip-file byteCode and to response !!!
        // -----------------------------------------------------
        byte[] byteArrayFile = FileUtil.convertZipFiletoByteArray(zipFileName);

        // Delete the zip file present if Success !!!
        // ----------------------------------------------
        if(deleteFileEnabled) {
            FileUtil.deleteFile(zipFileName);
        }

        if(null != byteArrayFile) {
            logger.warn("Resquested function Test result Sucess !!!");
            return RestResponseUtil.getSuccessResponse(byteArrayFile);
        } else {
            logger.warn("Resquested function Test result Faiuled !!!");
            return RestResponseUtil.getErrorResponse(21);
        }
    }

    private String generateFilePath(String funcTestId) {
        return resultPath + "/" + funcTestId;
    }
}
