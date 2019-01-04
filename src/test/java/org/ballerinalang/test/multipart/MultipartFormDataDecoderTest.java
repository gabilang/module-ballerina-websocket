/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.test.multipart;

import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BServiceUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.util.JsonParser;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.model.values.BXMLItem;
import org.ballerinalang.test.utils.HTTPTestRequest;
import org.ballerinalang.test.utils.ResponseReader;
import org.ballerinalang.test.utils.Services;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.transport.http.netty.message.HttpCarbonMessage;

import java.util.ArrayList;
import java.util.Map;

import static org.ballerinalang.mime.util.MimeConstants.CONTENT_TRANSFER_ENCODING_7_BIT;
import static org.ballerinalang.mime.util.MimeConstants.CONTENT_TRANSFER_ENCODING_8_BIT;
import static org.ballerinalang.mime.util.MimeConstants.MULTIPART_FORM_DATA;
import static org.ballerinalang.test.mime.Util.getArrayOfBodyParts;
import static org.ballerinalang.test.mime.Util.getBinaryBodyPart;
import static org.ballerinalang.test.mime.Util.getBinaryFilePart;
import static org.ballerinalang.test.mime.Util.getJsonBodyPart;
import static org.ballerinalang.test.mime.Util.getJsonFilePart;
import static org.ballerinalang.test.mime.Util.getTextBodyPart;
import static org.ballerinalang.test.mime.Util.getTextFilePart;
import static org.ballerinalang.test.mime.Util.getTextFilePartWithEncoding;
import static org.ballerinalang.test.mime.Util.getXmlBodyPart;
import static org.ballerinalang.test.mime.Util.getXmlFilePart;
import static org.ballerinalang.test.utils.MultipartUtils.createPrerequisiteMessages;
import static org.ballerinalang.test.utils.MultipartUtils.getCarbonMessageWithBodyParts;

/**
 * Test cases for multipart/form-data handling.
 *
 * @since 0.962.0
 */
public class MultipartFormDataDecoderTest {

    private CompileResult result, serviceResult;
    private static final String MOCK_ENDPOINT_NAME = "mockEP";

    @BeforeClass
    public void setup() {
        String sourceFilePath = "test-src/multipart/multipart-request.bal";
        result = BCompileUtil.compile(sourceFilePath);
        serviceResult = BServiceUtil.setupProgramFile(this, sourceFilePath);
    }

    @Test(description = "Test sending a multipart request with a text body part which is kept in memory")
    public void testTextBodyPart() {
        String path = "/test/textbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getTextBodyPart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), "Ballerina text body part");
    }

    @Test(description = "Test sending a multipart request with a text body part where the content is kept in a file")
    public void testTextBodyPartAsFileUpload() {
        String path = "/test/textbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getTextFilePart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), "Ballerina text as a file part");
    }

    @Test(description = "Test sending a multipart request with a json body part which is kept in memory")
    public void testJsonBodyPart() {
        String path = "/test/jsonbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getJsonBodyPart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        BValue json = JsonParser.parse(ResponseReader.getReturnValue(response));
        Assert.assertEquals(((BMap) json).get("bodyPart").stringValue(), "jsonPart");
    }

    @Test(description = "Test sending a multipart request with a json body part where the content is kept in a file")
    public void testJsonBodyPartAsFileUpload() {
        String path = "/test/jsonbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getJsonFilePart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        BValue json = JsonParser.parse(ResponseReader.getReturnValue(response));
        Assert.assertEquals(((BMap) json).get("name").stringValue(), "wso2");
    }

    @Test(description = "Test sending a multipart request with a xml body part which is kept in memory")
    public void testXmlBodyPart() {
        String path = "/test/xmlbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getXmlBodyPart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(new BXMLItem(ResponseReader.getReturnValue(response)).getTextValue().stringValue(),
                "Ballerina");
    }

    @Test(description = "Test sending a multipart request with a json body part where the content is kept in a file")
    public void testXmlBodyPartAsFileUpload() {
        String path = "/test/xmlbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getXmlFilePart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(new BXMLItem(ResponseReader.getReturnValue(response)).getTextValue().stringValue(),
                "Ballerina" +
                        " xml file part");
    }

    @Test(description = "Test sending a multipart request with a binary body part which is kept in memory")
    public void testBinaryBodyPart() {
        String path = "/test/binarybodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getBinaryBodyPart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), "Ballerina binary part");
    }

    @Test(description = "Test sending a multipart request with a binary body part where the content " +
            "is kept in a file")
    public void testBinaryBodyPartAsFileUpload() {
        String path = "/test/binarybodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getBinaryFilePart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), "Ballerina binary file part");
    }

    @Test(description = "Test sending a multipart request as multipart/form-data with multiple body parts")
    public void testMultiplePartsForFormData() {
        String path = "/test/multipleparts";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getJsonBodyPart(result));
        bodyParts.add(getXmlFilePart(result));
        bodyParts.add(getTextBodyPart(result));
        bodyParts.add(getBinaryFilePart(result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), " -- jsonPart -- Ballerina xml " +
                "file part -- Ballerina text body part -- Ballerina binary file part");
    }

    @Test(description = "Test sending a multipart request with a text body part that has 7 bit tranfer encoding")
    public void testTextBodyPartWith7BitEncoding() {
        String path = "/test/textbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getTextFilePartWithEncoding(CONTENT_TRANSFER_ENCODING_7_BIT, "èiiii", result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), "èiiii");
    }

    @Test(description = "Test sending a multipart request with a text body part that has 8 bit transfer encoding")
    public void testTextBodyPartWith8BitEncoding() {
        String path = "/test/textbodypart";
        Map<String, Object> messageMap = createPrerequisiteMessages(path, MULTIPART_FORM_DATA, result);
        ArrayList<BMap<String, BValue>> bodyParts = new ArrayList<>();
        bodyParts.add(getTextFilePartWithEncoding(CONTENT_TRANSFER_ENCODING_8_BIT, "èlllll", result));
        HTTPTestRequest cMsg = getCarbonMessageWithBodyParts(messageMap, getArrayOfBodyParts(bodyParts));
        HttpCarbonMessage response = Services.invokeNew(serviceResult, MOCK_ENDPOINT_NAME, cMsg);
        Assert.assertNotNull(response, "Response message not found");
        Assert.assertEquals(ResponseReader.getReturnValue(response), "èlllll");
    }
}
