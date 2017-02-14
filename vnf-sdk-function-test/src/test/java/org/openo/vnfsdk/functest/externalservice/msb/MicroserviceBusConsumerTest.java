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

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Test;
import org.mockito.Mockito;
import org.openo.vnfsdk.functest.externalservice.entity.ServiceRegisterEntity;
import org.openo.vnfsdk.functest.externalservice.msb.MicroserviceBusConsumer;
import org.openo.vnfsdk.functest.externalservice.msb.MicroserviceBusRest;
import org.powermock.api.mockito.PowerMockito;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;

public class MicroserviceBusConsumerTest {

    @Test
    public void testRegisterService() {
        ServiceRegisterEntity entity = Mockito.mock(ServiceRegisterEntity.class);
        Mockito.mock(MicroserviceBusRest.class);
        Mockito.mock(ClientConfig.class);
        PowerMockito.mockStatic(ConsumerFactory.class);
        PowerMockito.mockStatic(MicroserviceBusConsumer.class);
        MicroserviceBusConsumer.registerService(entity);
    }
}
