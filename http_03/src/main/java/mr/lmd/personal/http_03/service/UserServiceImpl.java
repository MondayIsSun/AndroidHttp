package mr.lmd.personal.http_03.service;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mr.lmd.personal.http_03.MainActivity;

/**
 * Created by Administrator on 2015/4/13.
 */
public class UserServiceImpl implements UserService {

    private static final String TAG = "UserServiceImpl";

    @Override
    public void userLoginGet(String username, String password) throws Exception {

        Log.d(TAG, username);
        Log.d(TAG, password);

        /*
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (username.equals("tom") && password.equals("123")) {

        } else {
            throw new ServiceRulesException(MainActivity.MSG_LOGIN_FAILED);
        }
        */

        /**************************************************************/

        /**
         * 客户端开始与服务器交互
         * HttpClient
         * HttpGet
         * HttpResponse
         */

        String uri = "http://192.168.56.1:8080/AndroidHttpServer/servlet/LoginServlet?username=" + username + "&password=" + password;

        //1、创建HttpClient对象
        HttpClient client = new DefaultHttpClient();

        //2、创建HttpGet对象
        //请求信息都被封装在这个对象里面了
        HttpGet get = new HttpGet(uri);

        //3、执行请求，并获取到响应对象 ---> 服务器处理请求以后的结果都被封装到了HttpResponse对象里面了
        HttpResponse response = client.execute(get);

        /**************************************************************/

        /**
         * 客户端开始根据服务器返回的结果作出不同的处理逻辑
         * HttpResponse
         */

        //4、获取http状态码 ---> 404,500,200
        /**
         * 404表示服务器资源没有找到
         * 500表示服务器内部出错
         * 200表示服务器成功返回了结构
         *
         * 接下来都是根据response中封装到信息作相应的处理
         */
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ServiceRulesException(MainActivity.MSG_SERVER_ERROR);
        }

        //5、获取服务端的响应信息
        //我们可以使用Apache提供的EntityUtils来解析response.getEntity对象
        //当然你也可以自己去一层一层地剥离Entity对象啦
        String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        //6、客户端根据服务端的响应信息作相应的处理
        if (result.equals("success")) {
            /**
             * 表示登录成功
             */
        } else {
            throw new ServiceRulesException(MainActivity.MSG_LOGIN_FAILED);
        }

        /**************************************************************/

    }

    @Override
    public void userLoginPost(String username, String password) throws Exception {

        //服务器地址
        String uri = "http://192.168.56.1:8080/AndroidHttpServer/servlet/LoginServlet";

        /**************************************************************/

        /**
         * 更加详细地控制请求 ---> 配置请求参数
         */

        //1、通过HttpParams封装请求参数
        HttpParams params = new BasicHttpParams();
        //通过params设置请求的字符集
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        //设置客户端和服务器连接的超时时间 ---> 超时会抛ConnectionTimeoutException异常
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        //设置服务器的响应超时时间 ---> 超时会抛SocketTimeoutException异常
        HttpConnectionParams.setSoTimeout(params, 3000);
        /**
         * 这个地方还可以设置很多请求参数信息
         * 自己去查看API，顺便把HTTP协议学习了
         */

        //2
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
        schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 433));

        //3、通过ClientConnectionManager来管理连接时的请求参数
        ClientConnectionManager conman = new ThreadSafeClientConnManager(params, schReg);

        /**************************************************************/

        //创建HttpClient对象
        HttpClient client = new DefaultHttpClient(conman, params);
        //HttpClient client = new DefaultHttpClient();

        //创建Post请求的对象
        HttpPost post = new HttpPost(uri);

        /**************************************************************/

        /**
         * 请求参数的设置
         */

        //设置要传递的参数信息
        NameValuePair loginUsername = new BasicNameValuePair("username", username);
        NameValuePair loginPassword = new BasicNameValuePair("password", password);

        //把参数添加到List里面
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(loginUsername);
        parameters.add(loginPassword);

        //把参数添加到Post请求里面
        post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));

        /**************************************************************/

        //执行请求 并 获取到服务器端的响应对象
        HttpResponse response = client.execute(post);

        //获取http状态码 ---> 404,500,200
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ServiceRulesException(MainActivity.MSG_SERVER_ERROR);
        }

        String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        if (result.equals("success")) {
            /**
             * 表示登录成功
             */
        } else {
            throw new ServiceRulesException(MainActivity.MSG_LOGIN_FAILED);
        }

        /**************************************************************/
    }

    @Override
    public void userRegister(String loginName, List<String> interesting) throws Exception {

        String uri = "http://192.168.56.1:8080/AndroidHttpServer/servlet/RegisterServlet";

        HttpClient client = new DefaultHttpClient();

        HttpPost post = new HttpPost(uri);

        /**********************************************************************************/

        /**
         * 客户端JSON数据的封装
         *
         * {
         *      "name" : "Marry" ,
         *
         *      "interesting" : [ "Music" , "Sport" , "Game" ]
         * }
         *
         */

        // { }
        JSONObject object = new JSONObject();
        object.put("LoginName", loginName);

        // [ ]
        JSONArray array = new JSONArray();
        if (interesting != null) {
            for (String str : interesting) {
                array.put(str);
            }
        }
        object.put("Interesting", array);

        /**********************************************************************************/

        //如何封装post请求的JSON参数 ---> JSONObject.toString()

        NameValuePair parameter = new BasicNameValuePair("data", object.toString());

        List<NameValuePair> params = new ArrayList<>();
        params.add(parameter);

        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        //获取到服务器端的响应对象
        HttpResponse response = client.execute(post);

        //获取http状态码 ---> 404,500,200
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ServiceRulesException(MainActivity.MSG_SERVER_ERROR);
        }

        /**********************************************************************************/

        /**
         * 客户端解析JSON数据
         */

        //获取到服务端的JSON数据
        String results = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        //客户端开始解析服务端传递过来的JSON数据
        JSONObject jsonResult = new JSONObject(results);
        String result = jsonResult.getString("result");

        if (result.equals("success")) {
            /**
             * 表示注册成功
             */
        } else {
            String errorMsg = jsonResult.getString("errorMsg");
            throw new ServiceRulesException(errorMsg);
        }

        /**********************************************************************************/
    }
}
