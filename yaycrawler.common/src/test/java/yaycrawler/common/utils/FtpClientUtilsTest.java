package yaycrawler.common.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by ucs_guoguibiao on 6/13 0013.
 */
public class FtpClientUtilsTest {

    @Test
    public void uploadFile () throws FileNotFoundException {
        File file = new File("D:/12314/2016011417");
        FtpClientUtils.uploadFile("127.0.0.1",21,"admin","admin","/test222/tetet/tette/tetete/tetetwtf","2016011417",new FileInputStream(file));
    }

    public static void main(String[] args) {
        System.out.println(System.getProperties());
    }
}
