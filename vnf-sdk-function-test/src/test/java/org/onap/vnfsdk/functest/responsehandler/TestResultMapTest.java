/*
 * Copyright (c) 2018 Intel Corporation Intellectual Property
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.vnfsdk.functest.responsehandler;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

public class TestResultMapTest {

    private UUID uuid = UUID.randomUUID();

    private Map<UUID, TestResult> testResultMap = new HashMap<UUID, TestResult>();

    @Test
    public void testGetInstance() {
        assertNotNull(TestResultMap.getInstance());
    }

    @Test
    public void testGetTestResultMap() {
        assertNotNull(TestResultMap.getInstance().getTestResultMap());
    }

    @Test
    public void testSetTestResultMap() {
        TestResult testResult = new TestResult();
        testResult.setName("INTEL");
        testResult.setStatus("SUCCESS");
        testResult.setDescription("INTEL TEST");
        testResultMap.put(uuid, testResult);
    }
}
