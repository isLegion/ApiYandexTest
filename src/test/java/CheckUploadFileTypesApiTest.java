import com.jayway.restassured.response.Response;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;



/**
 * Created by zsmirnova on 3/16/18.
 */
@RunWith(Parameterized.class)
public class CheckUploadFileTypesApiTest extends BaseTest {

    public static final Logger LOGGER = Logger.getLogger(CheckCopyFileTest.class);

    private ServicesSteps servicesSteps = new ServicesSteps();
    private String url;
    private String path;
    private String type;
    private String size;

    public CheckUploadFileTypesApiTest(String url, String path, String type, String size){
        this.url = url;
        this.path = path;
        this.type = type;
        this.size = size;
    }

    @Parameterized.Parameters
    public static Collection files() {
        return Arrays.asList(new Object[][] {
                { "http://kot-pes.com/wp-content/uploads/2016/10/image1-21.jpeg", "cat.jpg", "image", "203791"},
                { "http://www.ornatus.ru/download/obrazets_plan_referata.doc", "document.doc", "document", "43520"},
                {"https://chromedriver.storage.googleapis.com/2.37/chromedriver_win32.zip", "archive.zip", "compressed", "3348573"},
                {"http://www.temabiz.com/files/annuitet_temabiz1.xlsx", "/excel.xlsx", "document", "10681"},
                {"http://komotoz.ru/gifki/images/prikolnie_gifki/prikolnie_gifki_14.gif", "gifka.gif", "image", "7387086"},
                {"http://www.renedettweiler.de/xred_hot_chili_peppers_-_by_the_way.mp3", "song.mp3", "audio", "3459216"}
        });
    }

    @Test
    public void testUploadDifferentTypesFiles() throws Exception{
        LOGGER.info("TEST -- Check uploading with different types of files with path" + path + " and type " + type + "--");
        Response response = servicesSteps.uploadFile(path, url);
        LOGGER.info(response.print());
        String actualType = servicesSteps.getFile(path, 3).getMedia_type();
        servicesSteps
                .checkStatusResponse(202, response)
                .checkParamInResponseBody(type, actualType, "file type");
    }

    @Test
    public void testCheckUploadDifferentSizeFiles() throws Exception{
        LOGGER.info("TEST -- Check uploading with different sizes with path" + path + " and type " + size + "--");
        Response response = servicesSteps.uploadFile(path, url);
        LOGGER.info(response.print());
        String actualSize = servicesSteps.getFile(path, 3).getSize();
        servicesSteps
                .checkStatusResponse(202, response)
                .checkParamInResponseBody(size, actualSize, "file size");
    }

}
