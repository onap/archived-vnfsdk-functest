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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ServiceRegisterEntityTest {

    private ServiceRegisterEntity serviceRegistry;
    private ServiceNode serviceNode;

    @Before
    public void setUp() {
        serviceRegistry = new ServiceRegisterEntity();
        serviceNode = new ServiceNode();
    }

    @Test
    public void ServiceRegisterEntity() {

        List<ServiceNode> nodes = new ArrayList<ServiceNode>();

        serviceRegistry.setServiceName("nfvo");
        serviceRegistry.setVersion("5.6");
        serviceRegistry.setProtocol("http");
        serviceRegistry.setVisualRange("range");

        serviceNode.setIp("192.168.4.47");
        serviceNode.setPort("8080");
        serviceNode.setTtl(10);
        nodes.add(serviceNode);

        serviceRegistry.setNodes(nodes);

        assertNotNull(serviceRegistry);
        assertNotNull(serviceRegistry.getServiceName());
        assertNotNull(serviceRegistry.getVersion());
        assertNotNull(serviceRegistry.getProtocol());
        assertNotNull(serviceRegistry.getVisualRange());
        assertNotNull(serviceRegistry.getNodes());

    }

    @Test
    public void testSetSingleNode() {
        serviceRegistry.setSingleNode("192.168.4.47", "8080", 10);
    }
}
