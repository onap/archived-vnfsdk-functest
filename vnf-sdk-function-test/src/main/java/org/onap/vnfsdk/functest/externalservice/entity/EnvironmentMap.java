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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnvironmentMap {

    private static Map<UUID, Environment> envMap = new HashMap<UUID, Environment>();

    private static EnvironmentMap oInstance = new EnvironmentMap();

    private EnvironmentMap() {
        // Empty nothing to do
    }

    public static synchronized EnvironmentMap getInstance() {
        return oInstance;
    }

    public synchronized Map<UUID, Environment> getEnvmap() {
        return envMap;
    }

    public synchronized void addEnv(UUID uuid, Environment envobj) {
        envMap.put(uuid, envobj);
    }

    public synchronized void delEnv(UUID uuid) {
        envMap.remove(uuid);
    }

    public synchronized Environment getEnv(UUID uuid) {
        return envMap.get(uuid);
    }

}
