package mr.lmd.personal.http_02;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private final static String BASE_URL = "http://192.168.0.112:8080/AndroidAccessWebServer/servlet/LoginServlet";
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;

    private String username, password;

    private EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //待发送的数据
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //使用GET方式发送请求
        Button btnGet = (Button) findViewById(R.id.btnGet);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandlerThread getThread = new HandlerThread("getThread");
                /**
                 * 首先呢，自己要思考，这个地方是否需要使用HandlerThread类呢?
                 * 因为Android的HandlerThread类是帮我们准备了这个线程都消息队列队
                 * 那么我们如果将来确实是需要和这个子线程进行通信，那么我们就应该使用HandlerThread类来实现
                 * 但是如果我们确实不需要与这个子线程进行通信，那么就没有必要使用这个HandlerThread类的
                 */
                getThread.start();
                Handler handler = new Handler(getThread.getLooper());
                /**
                 * 注意下面的这样方式写的代码是犯了很严重的错误的
                 * 首先，体现了自己对Handler的理解不到位
                 */
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //拼凑GET请求的URL地址
                        username = MainActivity.this.usernameEditText.getText().toString();
                        password = MainActivity.this.passwordEditText.getText().toString();
                        String get_url = BASE_URL + "?username_get=" + username + "&password_get=" + password;

                        HttpGet httpGet = new HttpGet(get_url);
                        HttpClient httpClient = new DefaultHttpClient();
                        InputStream inputStream = null;
                        try {
                            httpResponse = httpClient.execute(httpGet);
                            httpEntity = httpResponse.getEntity();
                            inputStream = httpEntity.getContent();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            StringBuilder result = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            System.out.println("GET请求接收到的服务器返回的数据 ---> " + result.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });

        //使用POST方式发送请求
        Button btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandlerThread getThread = new HandlerThread("getThread");
                getThread.start();
                Handler handler = new Handler(getThread.getLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //1、发送数据的准备 ---> NameValuePair
                        username = MainActivity.this.usernameEditText.getText().toString();
                        password = MainActivity.this.passwordEditText.getText().toString();
                        NameValuePair usernameValuePair = new BasicNameValuePair("username_post", username);
                        NameValuePair passwordValuePair = new BasicNameValuePair("password_post", password);
                        List<NameValuePair> postList = new ArrayList<>();
                        postList.add(usernameValuePair);
                        postList.add(passwordValuePair);

                        try {
                            //2、转换请求的数据集合为HttpEntity对象
                            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(postList);

                            //3、创建Post方式的发送对象以及设置发送的数据
                            HttpPost httpPost = new HttpPost(BASE_URL);
                            httpPost.setEntity(requestHttpEntity);

                            //4、执行发送请求的操作
                            HttpClient httpClient = new DefaultHttpClient();
                            InputStream inputStream = null;
                            try {
                                httpResponse = httpClient.execute(httpPost);

                                //5、接收服务器响应请求的数据
                                httpEntity = httpResponse.getEntity();
                                inputStream = httpEntity.getContent();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                StringBuilder result = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    result.append(line);
                                }
                                System.out.println("POST请求接收到的服务器返回的数据 ---> " + result.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
                passwordEditText.setText("");
            }
        });
    }
}
