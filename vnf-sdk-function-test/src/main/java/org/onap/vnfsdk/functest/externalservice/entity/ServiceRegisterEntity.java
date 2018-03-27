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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRegisterEntity {

    private String serviceName;

    private String version;

    private String url;

    private String protocol;

    private String visualRange;

    private List<ServiceNode> nodes = new ArrayList<ServiceNode>();

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVisualRange() {
        return visualRange;
    }

    public void setVisualRange(String visualRange) {
        this.visualRange = visualRange;
    }

    public List<ServiceNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ServiceNode> nodes) {
        this.nodes = nodes;
    }


    public void setSingleNode(String ip, String port, int ttl) {
        ServiceNode node = new ServiceNode();
        if (ip != null && ip.length() > 0) {
            node.setIp(ip);
        } else {
            node.setIp(null);
        }
        node.setPort(port);
        node.setTtl(ttl);
        nodes.add(node);
    }

}
