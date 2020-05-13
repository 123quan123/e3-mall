import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

/**
 * @author quan
 * @create 2020-05-06 9:29
 */
public class FatsdfsTest {

    @Test
    public void testUpload() throws IOException, MyException {
        ClientGlobal.init("F:\\IDEA\\e3-mall\\e3-manager-web\\src\\main\\resources\\conf\\fastdfs_client.conf");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        String[] strings = storageClient.upload_file("C:\\Users\\Lenovo\\Desktop\\简历\\me.jpg", "jpg", null);
        for (String s : strings) {
            System.out.println(s);
        }

    }

}
