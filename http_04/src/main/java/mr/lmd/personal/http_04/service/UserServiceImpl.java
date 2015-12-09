package mr.lmd.personal.http_04.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/13.
 */
public class UserServiceImpl implements UserService {


    @Override
    public Bitmap getImageGet() throws Exception {

        Bitmap bitmap = null;
        InputStream in = null;

        String uri = "http://172.50.183.122:8080/AndroidHttpServer/getImage.jpeg?id=xin";

        URL url;

        //Java SE API当中提供的 ---> 顺便去学习Java的网络编程
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();

            //设置我可以读取数据(读取服务器发给我(客户端)的数据)
            urlConnection.setDoInput(true);
            //这个的意思是客户端可以给服务器发送数据
            //urlConnection.getDoOutput();

            urlConnection.setRequestMethod("GET");

            //设置读取的超时时间啦
            //urlConnection.setReadTimeout(3000);

            //连接服务器
            urlConnection.connect();

            //响应状态码 ---> 404,500,200
            int responseCode = urlConnection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceRulesException("请求服务器出错");
            }

            in = urlConnection.getInputStream();
            if (in != null) {
                /**
                 * 转换类型 InputStream BitMap Drawable byte[] 这几种类型的数据经常会互相转换
                 * 所以你可以封装成一个工具类啦
                 */
                bitmap = BitmapFactory.decodeStream(in);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }

    /**
     * 构造POST参数传递的格式
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static StringBuffer setPostPassParams(Map<String, String> params) throws UnsupportedEncodingException {

        //StringBuffer is synchronized
        //StringBuilder is not synchronized
        StringBuffer stringBuffer = new StringBuffer();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            stringBuffer
                    .append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                    .append("&");
        }

        //去掉最后的&
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        return stringBuffer;
    }

    @Override
    public Bitmap getImageByPost() throws Exception {

        Bitmap bitmap = null;
        InputStream in = null;
        OutputStream out;
        byte[] data;

        URL url;

        HttpURLConnection urlConnection = null;

        try {

            /**************************************************************************/

            //setPostPassParams

            //封装参数 ---> 要向服务器写的数据
            Map<String, String> params = new HashMap<>();
            params.put("id", "2");
            data = setPostPassParams(params).toString().getBytes();

            /**************************************************************************/

            url = new URL("http://172.50.183.122:8080/AndroidHttpServer/getImage.jpeg");

            urlConnection = (HttpURLConnection) url.openConnection();

            //设置参数

            //设置请求的超时时间
            urlConnection.setConnectTimeout(3000);
            //设置响应的超时时间
            urlConnection.setReadTimeout(3000);
            //允许客户端可以写数据到服务端
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            //设置请求方法
            urlConnection.setRequestMethod("POST");
            //设置取消缓存 ---> 因为我每次都想得到服务器发回的数据 ---> 而不要去读取缓存
            urlConnection.setUseCaches(false);
            //设置请求头信息
            /**
             * HTTP协议学习
             */
            //urlConnection.setRequestProperty("Content-Type",newValue);
            //urlConnection.setRequestProperty("Content-Length",newValue);

            /**************************************************************************/

            //连接服务器
            urlConnection.connect();

            /**************************************************************************/

            //输出流，客户端向服务器端发送数据
            out = urlConnection.getOutputStream();
            //向服务器写数据的关键代码
            out.write(data);
            //刷新
            out.flush();

            /**************************************************************************/

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new ServiceRulesException("请求服务器出错");
            }

            //服务端传给客户端
            in = new BufferedInputStream(urlConnection.getInputStream());
            if (in != null) {
                bitmap = BitmapFactory.decodeStream(in);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }
}
