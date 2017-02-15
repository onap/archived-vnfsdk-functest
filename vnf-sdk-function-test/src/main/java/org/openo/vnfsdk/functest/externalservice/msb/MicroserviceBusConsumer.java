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

package org.openo.vnfsdk.functest.externalservice.msb;

import java.io.IOException;

import org.glassfish.jersey.client.ClientConfig;
import org.openo.vnfsdk.functest.common.Config;
import org.openo.vnfsdk.functest.externalservice.entity.ServiceRegisterEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;

public class MicroserviceBusConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MicroserviceBusConsumer.class);

    private MicroserviceBusConsumer() {

    }

    public static boolean registerService(ServiceRegisterEntity entity) {
        ClientConfig config = new ClientConfig();
        try {
            MicroserviceBusRest resourceserviceproxy = ConsumerFactory
                    .createConsumer(Config.getConfigration().getMsbServerAddr(), config, MicroserviceBusRest.class);
            resourceserviceproxy.registerServce("false", entity);
        } catch(IOException error) {
            LOG.error("Microservice register failed!", error);
            return false;
        }
        return true;
    }
}
