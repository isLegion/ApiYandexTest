import com.jayway.restassured.response.Response;
import data.File;
import data.TestData;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static test.Constants.URL_TEMPLATE;

/**
 * Created by zsmirnova on 3/20/18.
 */
public class CheckDeletingFileTest extends BaseTest{

    public static final Logger LOGGER = Logger.getLogger(CheckCopyFileTest.class);

    private ServicesSteps servicesSteps = new ServicesSteps();
    private String path;

    @Before
    public void setData(){
       path = TestData.getRandomPath();
    }

    @Test
    public void testDeleteOneFile() throws Exception{
        LOGGER.info("TEST -- Checking the correct deletion of one file --");
        Response response = servicesSteps.uploadFile(path, URL_TEMPLATE);
        LOGGER.info(response.print());
        File file = servicesSteps.getFile(path, 2);
        Response response1 = servicesSteps.deleteFile(file.getPath());
        LOGGER.info(response1);
        servicesSteps
                .checkStatusResponse(202, response)
                .checkStatusResponse(204, response1);
    }

    @Test
    public void testDeleteNonExistFile() throws Exception{
        LOGGER.info("TEST -- Checking deletion of an existing file --");
        Response response = servicesSteps.deleteFile(path);
        LOGGER.info(response);
        servicesSteps
                .checkStatusResponse(404, response)
                .checkErrorResponseMessage("Не удалось найти запрошенный ресурс.", response);
    }

}
