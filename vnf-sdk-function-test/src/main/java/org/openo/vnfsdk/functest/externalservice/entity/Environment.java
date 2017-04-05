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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Environment {
	
	@JsonProperty("RemoteIp")
	private String RemoteIp;

	@JsonProperty("UserName")
	private String UserName;

	@JsonProperty("Password")
	private String Password;

	@JsonProperty("Path")
	private String Path;

    public String getRemoteIp() {
        return RemoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        RemoteIp = remoteIp;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

   

}
