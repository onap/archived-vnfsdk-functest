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

package org.onap.vnfsdk.functest.responsehandler;

import org.onap.vnfsdk.functest.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestResultParser {

    private static final String STATUSPASS = "PASS";

    private static final String RESULTTAG = "test";

    private static final String NAMETAG = "name";

    private static final String STATUSTAG = "status";

    private static final String KWTAG = "kw";

    private static final String DOCTAG = "doc";

    private static final Logger logger = LoggerFactory.getLogger(TestResultParser.class);

    public List<TestResult> populateResultList(String taskID, String xmlFile) {
        List<TestResult> resultData = new ArrayList<>();
        if (!FileUtil.checkFileExist(xmlFile)) {
            logger.error("File Not Found !!! : {}", xmlFile);
            return resultData;
        }
        parseResultData(taskID, xmlFile, resultData);
        return resultData;
    }

    private void parseResultData(String taskID, String xmlFile, List<TestResult> resultData) {
        try {
            Document doc = createDocument(xmlFile);
            NodeList list = doc.getElementsByTagName(RESULTTAG);
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                NamedNodeMap attr = node.getAttributes();
                if (null == attr) {
                    continue;
                }

                String nameAttr = getNodeValue(attr.getNamedItem(NAMETAG));
                if (null == nameAttr) {
                    continue;
                }

                String descriptionAttr = nameAttr;
                String statusAttr = STATUSPASS;
                NodeList childlist = node.getChildNodes();
                for (int j = 0; j < childlist.getLength(); j++) {
                    Node childNode = childlist.item(j);
                    if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    if (KWTAG == childNode.getNodeName()) {
                        NodeList kwNodeList = childNode.getChildNodes();
                        for (int k = 0; k < kwNodeList.getLength(); k++) {
                            Node descNode = kwNodeList.item(k);
                            if (descNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }

                            if (DOCTAG == descNode.getNodeName()) {
                                if (null != descNode.getTextContent()) {
                                    descriptionAttr = descNode.getTextContent();
                                    break;
                                }
                            }
                        }
                    }

                    if (STATUSTAG == childNode.getNodeName()) {
                        NamedNodeMap statusAttrMap = childNode.getAttributes();
                        if (null != statusAttrMap) {
                            statusAttr = getNodeValue(statusAttrMap.getNamedItem(STATUSTAG));
                        }
                    }
                }

                TestResult testData = new TestResult();
                testData.setName(nameAttr);
                testData.setDescription(descriptionAttr);
                testData.setStatus(statusAttr);

                resultData.add(testData);
            }

            TestResultMap.getInstance().setTestResultMap(UUID.fromString(taskID), resultData);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Exception while parsing file : {}", xmlFile);
            logger.error("Exception while parsing file :", e);
        }
    }

    private Document createDocument(String fileName) throws ParserConfigurationException, SAXException, IOException {
        try (InputStream inputStream = new FileInputStream(fileName)) {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            return doc;
        }
    }

    private String getNodeValue(Node namedItem) {
        return (null != namedItem) ? namedItem.getNodeValue() : null;
    }
}
