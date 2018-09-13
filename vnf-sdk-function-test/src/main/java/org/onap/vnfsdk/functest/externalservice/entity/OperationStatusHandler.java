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

package org.onap.vnfsdk.functest.externalservice.entity;

import org.onap.vnfsdk.functest.util.RestResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OperationStatusHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationStatusHandler.class);

    private static Map<UUID, OperationStatus> operStatusMap = new HashMap<UUID, OperationStatus>();

    private static OperationStatusHandler oInstance = new OperationStatusHandler();

    private OperationStatusHandler() {
        // Empty nothing to do
    }

    public static synchronized OperationStatusHandler getInstance() {
        return oInstance;
    }

    public synchronized Map<UUID, OperationStatus> getOperStatusMap() {
        return operStatusMap;
    }

    public synchronized void setOperStatusMap(UUID uuid, OperationStatus inputOperStatusMap) {
        operStatusMap.put(uuid, inputOperStatusMap);
    }

    public Response getOperationStatus(UUID uuid) {

        if (getOperStatusMap().containsKey(uuid)) {

            OperationStatus operstatus = getOperStatusMap().get(uuid);
            LOGGER.info("Operation Finished? {}", operstatus.isOperFinished());
            LOGGER.info("Operation Result Message: {}.", operstatus.getOperResultMessage());

            return RestResponseUtil.getSuccessResponse(operstatus);
        } else {
            OperationStatus operstatus = new OperationStatus();
            operstatus.setOperFinished(true);
            operstatus.setoResultCode(OperationStatus.operResultCode.NOTFOUND);
            LOGGER.error("uuid not found");

            return RestResponseUtil.getSuccessResponse(operstatus);

        }

    }

}
