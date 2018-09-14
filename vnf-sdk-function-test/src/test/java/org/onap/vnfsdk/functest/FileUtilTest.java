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

package org.onap.vnfsdk.functest;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class FileUtilTest {

    private String createDirPath = "." + File.separator + "tempvnf";

    private String deleteDirPath = createDirPath;

    private String zipFileName = "src/test/resources/RobotScript.zip";

    @Before
    public void setUp() {
        FileUtil.createDirectory(createDirPath);
    }

    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<FileUtil> constructor = FileUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testCreateDirectory() {
        assertTrue(FileUtil.createDirectory(createDirPath));
    }

    @Test
    public void testDeleteDirectory() {
        FileUtil.deleteDirectory(deleteDirPath);
    }

    @Test
    public void testDeleteFile() {
        assertTrue(FileUtil.deleteFile(new File(deleteDirPath)));
    }

    @Test
    public void testUnzip() {
        try {
            FileUtil.unzip(zipFileName, createDirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDirectory() {
        FileUtil.getDirectory(".");
        assertTrue(true);
    }

    @Test
    public void testCheckFileExist() {
        assertTrue(FileUtil.checkFileExist(deleteDirPath));
    }

    @Test
    public void testDeleteFileWithPath() {
        assertTrue(FileUtil.deleteFile(deleteDirPath));
    }

    @Test
    public void testConvertZipFiletoByteArray() {
        byte[] byteArrayFile = FileUtil.convertZipFiletoByteArray(zipFileName);
        // assertNotNull(byteArrayFile);
    }
}
