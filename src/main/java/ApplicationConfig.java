import org.aeonbits.owner.Config;

/**
 * Created by zsmirnova on 3/16/18.
 */
@Config.Sources({ "file:src/main/resources/config.properties" })
public interface ApplicationConfig extends Config {

    @Key("server.port")
    String port();

    @Key("server.base")
    String base();

    @Key("server.host")
    String host();
}
