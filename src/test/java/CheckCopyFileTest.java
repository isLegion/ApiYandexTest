import com.jayway.restassured.response.Response;
import data.File;
import data.Items;
import data.TestData;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

import static test.Constants.URL_TEMPLATE;

/**
 * Created by zsmirnova on 3/20/18.
 */
public class CheckCopyFileTest extends BaseTest{

    public static final Logger LOGGER = Logger.getLogger(CheckCopyFileTest.class);

    private String pathFrom;
    private String pathTo;

    @Before
    public void createData(){
        pathFrom = TestData.getRandomPath();
        pathTo = TestData.getRandomPath();
    }

    private ServicesSteps servicesSteps = new ServicesSteps();

    @Test
    public void testCopyOneFile() throws Exception{
        LOGGER.info("TEST -- Checking file copy --");
        Response response = servicesSteps.uploadFile(pathFrom, URL_TEMPLATE);
        LOGGER.info(response.print());
        File original = servicesSteps.getFile(pathFrom, 2);
        Response response1 = servicesSteps.copyFile(original.getPath(), pathTo);
        LOGGER.info(response1.print());
        File copiedFile = servicesSteps.getFile(pathTo, 3);
        servicesSteps
                .checkStatusResponse(202, response)
                .checkStatusResponse(201, response1)
                .checkParamInResponseBody(original.getSize(), copiedFile.getSize(), "file size")
                .checkParamInResponseBody(original.getMd5(), copiedFile.getMd5(), "md5")
                .checkParamInResponseBody(original.getMedia_type(), copiedFile.getMedia_type(), "media type")
                .checkParamInResponseBody(original.getSha256(), copiedFile.getSha256(), "sha256");
    }

    @Test()
    public void testCopySeveralTimesFile() throws Exception{
        LOGGER.info("TEST -- Check copy several files --");
        Response response = servicesSteps.uploadFile(pathFrom, URL_TEMPLATE);
        File original = servicesSteps.getFile(pathFrom, 2);
        Integer count = 10;
        IntStream.range(1, count).forEach(i->
                servicesSteps.copyFile(original.getPath(), TestData.getRandomPath()));
        Response response1 = servicesSteps.getAllFiles();
        List<File> items = response1.as(Items.class).getItems();
        servicesSteps
                .checkStatusResponse(202, response)
                .checkStatusResponse(200, response1)
                .checkParamInResponseBody(String.valueOf(items.size()), count.toString(), "count of files");
    }

    @Test()
    public void testAlreadyExistCopyFile() throws Exception{
        LOGGER.info("TEST -- Check copy to already exist file --");
        Response response = servicesSteps.uploadFile(pathFrom, URL_TEMPLATE);
        File original = servicesSteps.getFile(pathFrom, 2);
        Response response1 = servicesSteps.copyFile(original.getPath(), pathTo);
        Response response2 = servicesSteps.copyFile(original.getPath(), pathTo);
        servicesSteps
                .checkStatusResponse(202, response)
                .checkStatusResponse(201, response1)
                .checkStatusResponse(409, response2)
                .checkErrorResponseMessage("Ресурс \\"+ pathTo +"\\ уже существует.", response2);
    }

    @Test()
    public void testIncorrectPathFromCopyFile() throws Exception{
        LOGGER.info("TEST -- Check file cope with incorrect path from --");
        Response response = servicesSteps.copyFile(pathFrom, pathTo);
        servicesSteps
                .checkStatusResponse(404, response)
                .checkErrorResponseMessage("Не удалось найти запрошенный ресурс.", response);

    }

    @Test()
    public void testIncorrectPathToCopyFile() throws Exception{
        LOGGER.info("TEST -- Check file cope with incorrect path to --");
        Response response = servicesSteps.uploadFile(pathFrom, URL_TEMPLATE);
        Response response1 = servicesSteps.copyFile(pathFrom, "");
        servicesSteps
                .checkStatusResponse(202, response)
                .checkStatusResponse(400, response1)
                .checkErrorResponseMessage("Ошибка проверки поля \\path\\: Это поле является обязательным.", response1);

    }
}
