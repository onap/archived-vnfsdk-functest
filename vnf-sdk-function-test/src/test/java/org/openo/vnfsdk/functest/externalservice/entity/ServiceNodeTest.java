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

package org.openo.vnfsdk.functest.externalservice.entity;

import org.junit.Before;
import org.junit.Test;

public class ServiceNodeTest {

    private ServiceNode serviceNode = null;

    @Before
    public void setUp() {
        serviceNode = new ServiceNode();
    }

    @Test
    public void testSetIP() {
        serviceNode.setIp("192.168.4.47");
    }

    @Test
    public void testSetPort() {
        serviceNode.setPort("8080");
    }

    @Test
    public void testSetTtl() {
        serviceNode.setTtl(80);
    }
}
