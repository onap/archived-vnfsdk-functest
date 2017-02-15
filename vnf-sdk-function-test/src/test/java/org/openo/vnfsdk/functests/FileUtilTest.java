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

package org.openo.vnfsdk.functests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.openo.vnfsdk.functest.FileUtil;

public class FileUtilTest {

    private String createDirPath = "C:/Users/Administrator/Desktop/Unzip";

    private String deleteDirPath = "D:/deletefolder";

    private String zipFileName = "src/test/resources/RobotScript.zip";

    private String extractZip = "D:/Test/AA";

    private String getDirectory = "D:/Test/AA";

    @Test
    public void testCreateDirectory() {
        assertTrue(FileUtil.createDirectory(createDirPath));
    }

    @Test
    public void testDeleteFile() {
        assertTrue(FileUtil.deleteFile(new File(deleteDirPath)));
    }

    @Test
    public void testUnzip() throws IOException {
        ArrayList<String> files = FileUtil.unzip(zipFileName, extractZip);
        assertNotNull(files);
    }

    @Test
    public void testGetDirectory() {
        String[] getDirectories = FileUtil.getDirectory(".");
        assertNotNull(getDirectories);
    }

    @Test
    public void testConvertZipFiletoByteArray() {
        byte[] byteArrayFile = FileUtil.convertZipFiletoByteArray(zipFileName);
        assertNotNull(byteArrayFile);
    }
}
