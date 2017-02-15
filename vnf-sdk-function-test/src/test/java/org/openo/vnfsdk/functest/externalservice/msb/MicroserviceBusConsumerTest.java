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

import javax.ws.rs.ProcessingException;

import org.junit.Test;
import org.openo.vnfsdk.functest.VnfSdkFuncTestAppConfiguration;
import org.openo.vnfsdk.functest.common.Config;
import org.openo.vnfsdk.functest.externalservice.entity.ServiceRegisterEntity;

public class MicroserviceBusConsumerTest {

    @Test
    public void testRegisterService() {

        try {
            ServiceRegisterEntity entity = new ServiceRegisterEntity();
            VnfSdkFuncTestAppConfiguration oConfig = new VnfSdkFuncTestAppConfiguration();
            oConfig.setMsbServerAddr("http://127.0.0.1");
            Config.setConfigration(oConfig);

            MicroserviceBusConsumer.registerService(entity);
        } catch(ProcessingException e) {
            // Connect to MSB will fail, Connect refused is OK
        }
    }
}
