package data;

import org.kohsuke.randname.RandomNameGenerator;

/**
 * Created by zsmirnova on 8/2/17.
 */
public class TestData {

    public static final RandomNameGenerator RND = new RandomNameGenerator();

    public static String getRandomWord(){
        return RND.next();
    }

    public static String getRandomPath(){ return "/" + getRandomWord();}
}
