package mr.lmd.personal.http_06.service;

import android.os.Environment;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import mr.lmd.personal.http_06.MainActivity;

/**
 * Created by Administrator on 2015/4/13.
 */
public class UserServiceImpl implements UserService {

    @Override
    public String userUpload(InputStream in, Map<String, String> data) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://172.25.82.138:8080/AndroidHttpServer/upload.do");

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //把数据封装到post里面去

        /**
         * HttpMime出场啦 ---> 也是Apache的
         */

        MultipartEntity entity = new MultipartEntity();

        /**
         * 普通的字符串数据封装
         */
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
        }

        /**
         * 二进制文件数据封装
         */
        entity.addPart("file", new InputStreamBody(in, "multipart/form-data", "test.jpg"));

        /**
         * 多文件可以在这里继续加
         */

        //数据放在post里面
        post.setEntity(entity);

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //执行请求 并 获取到服务器端的响应对象
        HttpResponse response = client.execute(post);

        //获取http状态码 ---> 404,500,200
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ServiceRulesException(MainActivity.MSG_SERVER_ERROR);
        }

        String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return result;
    }

    @Override
    public void userDownload() throws Exception {

        InputStream in = null;
        OutputStream out = null;
        URL url;
        HttpURLConnection urlConnection;

        try {

            url = new URL("http://172.25.82.138:8080/AndroidHttpServer/download.do");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(20000);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");

            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceRulesException("Server Error");
            }

            /*
            如果使用HttpClient对象的回去流的方式
            HttpClient client = null;
            HttpResponse response = null;
            InputStream in = response.getEntity().getContent();
            */

            in = new BufferedInputStream(urlConnection.getInputStream());
            out = new BufferedOutputStream(new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/download.zip")));
            System.out.println(Environment.getExternalStorageDirectory() + "/download.zip");

            byte[] buf = new byte[20480];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
