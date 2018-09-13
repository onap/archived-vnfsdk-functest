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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtil {

    public static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    private static final int TRY_COUNT = 3;

    private FileUtil() {

    }

    /**
     * Create directory.
     *
     * @param dir directory to create
     * @return boolean
     */
    public static boolean createDirectory(String dir) {
        File folder = new File(dir);
        int tryCount = 0;
        while (tryCount < TRY_COUNT) {
            tryCount++;
            if (!folder.exists() && !folder.mkdirs()) {
                continue;
            } else {
                return true;
            }
        }

        return folder.exists();
    }

    /**
     * delete file.
     *
     * @param file the file to delete
     * @return boolean
     */
    public static boolean deleteFile(File file) {
        String hintInfo = file.isDirectory() ? "dir " : "file ";
        boolean isFileDeleted = file.delete();
        boolean isFileExist = file.exists();
        if (!isFileExist) {
            if (isFileDeleted) {
                LOG.info("delete {} {}", hintInfo, file.getAbsolutePath());
            } else {
                isFileDeleted = true;
                LOG.info("file not exist. no need delete {} {}", hintInfo, file.getAbsolutePath());
            }
        } else {
            LOG.info("fail to delete {} {}", hintInfo, file.getAbsolutePath());
        }
        return isFileDeleted;
    }

    /**
     * unzip zip file.
     *
     * @param zipFileName file name to zip
     * @param extPlace    extPlace
     * @return unzip file name
     * @throws IOException e1
     */
    public static List<String> unzip(String zipFileName, String extPlace) throws IOException {

        ArrayList<String> unzipFileNams = new ArrayList<String>();

        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Enumeration<?> fileEn = zipFile.entries();
            byte[] buffer = new byte[BUFFER_SIZE];

            while (fileEn.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) fileEn.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                File file = new File(extPlace, entry.getName());
                if (!file.getParentFile().exists()) {
                    createDirectory(file.getParentFile().getAbsolutePath());
                }
                try (InputStream input = zipFile.getInputStream(entry);
                     BufferedOutputStream bos =  new BufferedOutputStream(new FileOutputStream(file))) {

                    while (true) {
                        int length = input.read(buffer);
                        if (length == -1) {
                            break;
                        }
                        bos.write(buffer, 0, length);
                    }
                    unzipFileNams.add(file.getAbsolutePath());
                } catch (Exception ex) {
                    LOG.error("close InputStream error!: {}", ex);
                    LOG.error("close OutputStream error!: {}", ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("close ZipFile error!: ", ex);
            throw new IOException(ex);
        }
        return unzipFileNams;
    }

    public static String[] getDirectory(String directory) {
        File file = new File(directory);
        return file.list(new FilenameFilter() {

            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
    }

    public static Boolean checkFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static Boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static byte[] convertZipFiletoByteArray(String filename) {
        File file = new File(filename);
        byte[] emptyArray = new byte[0];
        if (!file.exists()) {
            return emptyArray;
        }

        byte[] byteArrayFile = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            int value = fileInputStream.read(byteArrayFile);
            LOG.debug("Number of bytes read from fileInputStream = {}", value);
        } catch (Exception e) {
            LOG.error("convertZipFiletoByteArray: {}", e);
        }
        return byteArrayFile;
    }

    /**
     * Interface to Delete Directory
     * <br/>
     *
     * @param directory
     * @since VNFSDK 2.0
     */
    public static void deleteDirectory(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                deleteFile(sub);
            }
        }
        file.delete();
    }
}
