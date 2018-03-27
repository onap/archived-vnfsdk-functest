/*
 * Copyright (c) 2018 Intel Corporation Intellectual Property
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

package org.onap.vnfsdk.functest.responsehandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestResultMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultMap.class);

    private static Map<UUID, List<TestResult>> testResultMap = new HashMap<UUID, List<TestResult>>();

    private static TestResultMap oInstance = new TestResultMap();

    private TestResultMap() {
        // Empty nothing to do
    }

    public static synchronized TestResultMap getInstance() {
        return oInstance;
    }

    public synchronized Map<UUID, List<TestResult>> getTestResultMap() {
        return testResultMap;
    }

    public synchronized void setTestResultMap(UUID uuid, List<TestResult> inputTestResult) {
        testResultMap.put(uuid, inputTestResult);
    }

}
