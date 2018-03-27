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

package org.onap.vnfsdk.functest.common;

import org.onap.vnfsdk.functest.externalservice.entity.ServiceRegisterEntity;
import org.onap.vnfsdk.functest.externalservice.msb.MicroserviceBusConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistration implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistration.class);
    private final ServiceRegisterEntity funcTestEntity = new ServiceRegisterEntity();

    public ServiceRegistration() {
        initServiceEntity();
    }

    @Override
    public void run() {
        LOG.info("start extsys microservice register");
        boolean flag = false;
        int retry = 0;

        while (!flag) {
            LOG.info("VNF-SDK function test microservice register.retry:" + retry);
            retry++;

            flag = MicroserviceBusConsumer.registerService(funcTestEntity);
            if (retry >= 1000) {
                flag = true;
            }

            if (flag == false) {
                LOG.warn("microservice register failed, sleep 30S and try again.");
                threadSleep(30000);
            } else {
                LOG.info("microservice register success!");
                break;
            }
        }
        LOG.info("VNF-SDK function test microservice register end.");
    }

    private void threadSleep(int second) {
        LOG.info("start sleep ....");
        try {
            Thread.sleep(second);
        } catch (InterruptedException error) {
            LOG.error("thread sleep error.errorMsg:", error);
            Thread.currentThread().interrupt();
        }
        LOG.info("sleep end .");
    }

    private void initServiceEntity() {
        funcTestEntity.setServiceName("vnfsdk");
        funcTestEntity.setProtocol("REST");
        funcTestEntity.setVersion("v1");
        funcTestEntity.setUrl("/api/vnfsdk/v1");
        funcTestEntity.setSingleNode(Config.getConfigration().getServiceIp(), "8701", 0);
        funcTestEntity.setVisualRange("1");
    }
}
