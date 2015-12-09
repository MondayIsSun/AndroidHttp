package mr.lmd.personal.http_05.service;


import java.util.List;

import mr.lmd.personal.http_05.bean.Student;

/**
 * Created by Administrator on 2015/4/13.
 */
public interface UserService {
    public List<Student> getStudents() throws Exception;
}
