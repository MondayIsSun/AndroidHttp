package mr.lmd.personal.http_06.service;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/13.
 */
public interface UserService {

    public String userUpload(InputStream in, Map<String, String> data) throws Exception;

    public void userDownload() throws Exception;

}
