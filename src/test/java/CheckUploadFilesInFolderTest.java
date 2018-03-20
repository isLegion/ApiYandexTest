import com.jayway.restassured.response.Response;
import data.TestData;
import org.apache.log4j.Logger;
import org.junit.Test;

import static test.Constants.URL_TEMPLATE;

/**
 * Created by zsmirnova on 3/20/18.
 */
public class CheckUploadFilesInFolderTest extends BaseTest  {

    public static final Logger LOGGER = Logger.getLogger(CheckCopyFileTest.class);

    private ServicesSteps servicesSteps = new ServicesSteps();

    @Test
    public void testUploadFile(){
        LOGGER.info("TEST -- Check the correct uploading file --");
        String path = "/cat.jpg";
        Response response = servicesSteps.uploadFile(path, URL_TEMPLATE);
        LOGGER.info(response.print());
        servicesSteps.checkStatusResponse(202, response);
    }

    @Test
    public void testUploadFileInSomeFolder() throws Exception {
        LOGGER.info("TEST -- Checking upload file to some folder --");
        String firstFolderPath = TestData.getRandomPath();
        String secondFolderPath = firstFolderPath + TestData.getRandomPath();
        String filePath = "/kot.jpeg";
        String expectedPath = secondFolderPath + filePath;
        Response responseOne = servicesSteps.createFolder(firstFolderPath);
        Response responseTwo = servicesSteps.createFolder(secondFolderPath);
        Response responseThree = servicesSteps.uploadFile(expectedPath, URL_TEMPLATE);
        LOGGER.info(responseOne.print() + "/n" + responseTwo.print() + "/n" + responseThree.print());
        String actualPath = servicesSteps.getFile(filePath.replace("/", ""), 3).getPath();
        servicesSteps.checkStatusResponse(201, responseOne)
                .checkStatusResponse(201, responseTwo)
                .checkStatusResponse(202, responseThree)
                .checkParamInResponseBody("disk:" + expectedPath, actualPath, "path");
    }

    @Test
    public void testIncorrectPathUploadedFile() {
        LOGGER.info("TEST -- Check upload file with incorrect path --");
        String path = "/qwe$%68/Nfnfnfn";
        Response response = servicesSteps.uploadFile(path, URL_TEMPLATE);
        LOGGER.info(response.print());
        servicesSteps.checkStatusResponse(404, response)
                .checkErrorResponseMessage("Указанного пути \\" + path + "\\ не существует.", response);
    }

    @Test
    public void testLocalUrlUploadedFile() {
        LOGGER.info("TEST -- Check upload file with local url --");
        String path = "/cat";
        String incorectUrl = "/yandexApiTest/src/main/resources/dog.png";
        Response response = servicesSteps.uploadFile(path, incorectUrl);
        LOGGER.info(response.print());
        servicesSteps.checkStatusResponse(400, response)
                .checkErrorResponseMessage("Относительные URL не допустимы", response);
    }

    @Test
    public void testEmptyParamUploadFile() {
        LOGGER.info("TEST -- Check upload file with empty options --");
        String path = "";
        String incorectUrl = "";
        Response response = servicesSteps.uploadFile(path, incorectUrl);
        LOGGER.info(response.print());
        servicesSteps.checkStatusResponse(400, response)
                .checkErrorResponseMessage("Это поле является обязательным", response);
    }

    @Test
    public void testUploadFileWithIncorrectUrl() throws Exception {
        LOGGER.info("TEST -- Check upload file with incorrect url --");
        String path = "/cat";
        String incorrectUrl = "#6363bhjbjbh";
        Response response = servicesSteps.uploadFile(path, incorrectUrl);
        LOGGER.info(response.print());
        servicesSteps.checkStatusResponse(400, response)
                .checkErrorResponseMessage("Относительные URL не допустимы.", response);
    }

}
