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

package org.openo.vnfsdk.functest.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.openo.vnfsdk.functest.FileUtil;
import org.openo.vnfsdk.functest.TaskExecution;
import org.openo.vnfsdk.functest.responsehandler.VnfFuncTestResponseHandler;
import org.openo.vnfsdk.functest.util.RestResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/functest")
@Api(tags = {" function test Management "})
public class CommonManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonManager.class);

    @Path("")
    @POST
    @ApiOperation(value = "execute the function test")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
                    @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
                    @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
                    @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @Timed
    public Response executeFuncTest(InputStream csarInputStream) {
        LOGGER.info("execute function test");

        try {

            // Convert the stream to script folder
            String nl = File.separator;
            String filePath = storeChunkFileInLocal("temp", "TempFile.rar", csarInputStream);

            // Unzip the folder
            String tempDir = System.getProperty("user.dir") + nl + "temp";
            FileUtil.unzip(filePath, tempDir);
            LOGGER.info("File path=" + filePath);

            String[] directories = FileUtil.getDirectory(tempDir);
            if(null != directories) {
                filePath = tempDir + File.separator + directories[0];
            }

            // Upload the script and execute the script and run command
            final UUID uniqueKey = UUID.randomUUID();

            final String finalPath = filePath;
            ExecutorService es = Executors.newFixedThreadPool(3);
            es.submit(new Callable<Integer>() {

                public Integer call() throws Exception {

                    new TaskExecution().executeScript(finalPath, uniqueKey);
                    return 0;
                }
            });

            // Send REST response
            return RestResponseUtil.getSuccessResponse(uniqueKey);

        } catch(IOException e) {
            LOGGER.error("Upload the script and execute the script and run command", e);
        }

        return null;
    }

    @Path("/{functestId}")
    @GET
    @ApiOperation(value = "get function test result by id")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
                    @ApiResponse(code = HttpStatus.NOT_FOUND_404, message = "microservice not found", response = String.class),
                    @ApiResponse(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE_415, message = "Unprocessable MicroServiceInfo Entity ", response = String.class),
                    @ApiResponse(code = HttpStatus.INTERNAL_SERVER_ERROR_500, message = "internal server error", response = String.class)})
    @Timed
    public Response queryResultByFuncTest(@ApiParam(value = "functestId") @PathParam("functestId") String instanceId) {
        LOGGER.info("query functest result by id." + instanceId);
        // Query VNF Function test result by function test ID
        return VnfFuncTestResponseHandler.getInstance().getResponseByFuncTestId(instanceId);
    }

    /**
     * Convert the stream to File Name<br/>
     * 
     * @param dirName - Directory name
     * @param fileName - FileName
     * @param uploadedInputStream - Input Stream
     * @return - File Path
     * @throws IOException - Exception while writing file
     * @since VNFSDK
     */
    public String storeChunkFileInLocal(String dirName, String fileName, InputStream uploadedInputStream)
            throws IOException {
        File tmpDir = new File(dirName);
        LOGGER.info("tmpdir=" + dirName);
        if(!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        StringTokenizer st = new StringTokenizer(fileName, "/");
        String actualFile = null;
        while(st.hasMoreTokens()) {
            actualFile = st.nextToken();
        }
        File file = new File(tmpDir + File.separator + actualFile);
        OutputStream os = null;
        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            os = new FileOutputStream(file, true);
            while((read = uploadedInputStream.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            os.flush();
            return file.getAbsolutePath();
        } finally {
            if(os != null) {
                os.close();
            }
        }
    }

}
