import com.jayway.restassured.internal.RestAssuredResponseImpl;
import com.jayway.restassured.response.Response;
import data.File;
import data.Items;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static test.Constants.*;

/**
 * Created by zsmirnova on 3/18/18.
 */
public class ServicesSteps {

    public static final Logger LOGGER = Logger.getLogger(ServicesSteps.class);

    public void deleteAllFiles(){
        LOGGER.info("STEP - Delete all files ");
        List<File> files = getAllFiles().as(Items.class).getItems();
        if (files.size() != 0) {
            for (File file : files) {
                given().header(AUTHORIZATION, TOKEN)
                        .contentType(CONTENT_TYPE_JSON)
                        .when()
                        .delete("?path=" + file.getPath())
                        .then().statusCode(204).log().all();
            }
        }
    }

    public Response getAllFiles(){
        LOGGER.info("STEP - Invoke getting all files API ");
        return given().header(AUTHORIZATION, TOKEN)
                .contentType(CONTENT_TYPE_JSON)
                .when()
                .log().all()
                .get("/files");
    }

    public Response uploadFile(String path, String url) {
        LOGGER.info("STEP - Invoke upload file with path " + path + " and url " +url);
        return given().header(AUTHORIZATION, TOKEN)
                .contentType(CONTENT_TYPE_JSON)
                .log().all()
                .when()
                .post("/upload?path=" + path + "&url=" + url);
    }

    public File getFile(String name, int TIMEOUT) throws Exception {
        LOGGER.info("STEP - Invoke getting file with name " + name);
        List<File> result = null;
        int counter = 0;
        int index = 0;
        while (counter < TIMEOUT) {
            do{
                result = getAllFiles().as(Items.class).getItems();
            }
            while (result.size() == 0);
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getName().equals(name)) {
                    index = i;
                    break;
                } else {
                    TimeUnit.SECONDS.sleep(1);
                    ++counter;
                    if (counter == TIMEOUT) {
                        throw new TimeoutException("File doesn't exist");
                    }
                }
            }
            break;
        }
        return result.get(index);
    }

    public ServicesSteps checkErrorResponseMessage(String message, Response response){
        LOGGER.info("STEP - Check response has error message " + message);
        assertThat("Something went wrong" + response.print(),
                ((RestAssuredResponseImpl) response).getContent().toString().replace("\"", ""), containsString(message));
        return this;
    }

    public ServicesSteps checkStatusResponse(int status, Response response){
        LOGGER.info("STEP - Check response has status " + status);
        response.then().log().ifError().assertThat().statusCode(status);
        return this;
    }

    public ServicesSteps checkParamInResponseBody(String expectedValue, String actualValue, String paramName){
        LOGGER.info("STEP - Checking the correspondence of fields " + actualValue + " and " + expectedValue +" for param " +paramName);
        assertThat("Values of parameter " + paramName + " don't match with value " + actualValue, actualValue, equalTo(expectedValue));
        return this;
    }

    public Response deleteFile(String path){
        LOGGER.info("STEP - Invoke delete file with path " + path);
        return given().header(AUTHORIZATION, TOKEN)
                .contentType(CONTENT_TYPE_JSON)
                .log().all()
                .when()
                .delete("?path=" + path);
    }

    public Response createFolder(String path){
        LOGGER.info("STEP - Invoke create folder with path " + path);
        return given().header(AUTHORIZATION, TOKEN)
                .contentType(CONTENT_TYPE_JSON)
                .log().all()
                .when()
                .put("?path=" + path);
    }

    public Response copyFile(String pathFrom, String pathTo){
        LOGGER.info("STEP - Invoke copy file from " + pathFrom + " to " + pathTo);

        return given().header(AUTHORIZATION, TOKEN)
                .contentType(CONTENT_TYPE_JSON)
                .log().all()
                .when()
                .post("/copy?from=" + pathFrom + "&path=" + pathTo);
    }


}
