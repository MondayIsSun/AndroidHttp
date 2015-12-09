package mr.lmd.personal.http_05.service;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mr.lmd.personal.http_05.MainActivity;
import mr.lmd.personal.http_05.bean.Student;

/**
 * Created by Administrator on 2015/4/13.
 */
public class UserServiceImpl implements UserService {

    @Override
    public List<Student> getStudents() throws Exception {

        List<Student> studentsList = new ArrayList<>();

        String uri = "http://192.168.56.1:8080/AndroidHttpServer/studentJSON.get";

        HttpClient client = new DefaultHttpClient();

        HttpGet get = new HttpGet(uri);

        HttpResponse response = client.execute(get);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ServiceRulesException(MainActivity.MSG_SERVER_ERROR);
        }

        String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        JSONArray stu_array = new JSONArray(result);

        for (int i = 0; i < studentsList.size(); i++) {
            JSONObject stu_json = (JSONObject) stu_array.get(i);
            Long id = Long.parseLong(stu_json.getString("id"));
            String name = stu_json.getString("name");
            int age = stu_json.getInt("age");

            System.out.print(name);

            studentsList.add(new Student(id, name, age));
        }
        return studentsList;
    }
}
