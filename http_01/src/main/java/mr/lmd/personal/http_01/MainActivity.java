package mr.lmd.personal.http_01;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity {

    private HttpResponse httpResponse = null;
    private HttpEntity httpEntity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button requestButton = (Button) findViewById(R.id.requestButton);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HandlerThread handlerThread = new HandlerThread("access_baidu_thread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //android.os.NetworkOnMainThreadException ---> 需要放在单独的线程当中操作

                        //生成一个请求对象
                        //new HttpPost() ---> 针对GET请求
                        //new HttpGet() ---> 针对POST请求
                        HttpGet httpGet = new HttpGet("http://www.baidu.com");

                        //生成一个Http客户端对象
                        HttpClient httpClient = new DefaultHttpClient();

                        InputStream inputStream = null;
                        try {
                            //使用Http客户端发送请求对象
                            httpResponse = httpClient.execute(httpGet);

                            //接收服务器响应回来的数据
                            httpEntity = httpResponse.getEntity();
                            inputStream = httpEntity.getContent();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            String result = "";
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result = result + line;
                            }
                            System.out.println(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }
}
