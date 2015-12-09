package mr.lmd.personal.http_03.service;

import java.util.List;

/**
 * Created by Administrator on 2015/4/13.
 */
public interface UserService {
    public void userLoginGet(String username, String password) throws Exception;

    public void userLoginPost(String username, String password) throws Exception;

    public void userRegister(String loginName, List<String> interesting) throws Exception;
}
