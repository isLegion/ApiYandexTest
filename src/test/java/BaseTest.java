import com.jayway.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;

/**
 * Created by zsmirnova on 3/18/18.
 */
public class BaseTest {

    @Before
    public void setup(){
        ApplicationConfig cfg = ConfigFactory.create(ApplicationConfig.class);
        RestAssured.port = Integer.valueOf(cfg.port());
        RestAssured.basePath = cfg.base();
        RestAssured.baseURI = cfg.host();
        PropertyConfigurator.configure("/Users/zsmirnova/yandexApiTest/src/main/resources/log4j.properties");
    }

    @After
    public void deleteRedundantFiles(){
        ServicesSteps servicesSteps= new ServicesSteps();
        servicesSteps.deleteAllFiles();
    }
}
