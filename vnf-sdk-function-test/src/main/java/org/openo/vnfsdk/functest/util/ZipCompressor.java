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

package org.openo.vnfsdk.functest.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipCompressor {

    public static final Logger LOG = LoggerFactory.getLogger(ZipCompressor.class);

    static final int BUFFER = 8192;

    private File zipFile;

    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
    }

    /**
     * compress file according file path.
     * 
     * @param srcPathName file path name
     * @throws IOException
     */
    public void compress(String srcPathName) throws IOException {
        File file = new File(srcPathName);
        if(!file.exists()) {
            throw new FileNotFoundException(srcPathName + "not exist!");
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir);
            out.close();
        } catch(Exception e1) {
            throw new IOException(e1);
        }
    }

    private void compress(File file, ZipOutputStream out, String basedir) {
        if(file.isDirectory()) {
            LOG.info("compress: " + basedir + file.getName());
            this.compressDirectory(file, out, basedir);
        } else {
            LOG.info("compress: " + basedir + file.getName());
            this.compressFile(file, out, basedir);
        }
    }

    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if(!dir.exists()) {
            return;
        }

        File[] files = dir.listFiles();
        for(int i = 0; i < files.length; i++) {
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if(!file.exists()) {
            return;
        }
        try {
            byte data[] = new byte[BUFFER];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            while((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch(Exception e1) {
            throw new RuntimeException(e1);
        }
    }
}
