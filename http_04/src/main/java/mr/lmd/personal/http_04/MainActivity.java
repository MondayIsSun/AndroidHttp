package mr.lmd.personal.http_04;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import mr.lmd.personal.http_04.service.ServiceRulesException;
import mr.lmd.personal.http_04.service.UserService;
import mr.lmd.personal.http_04.service.UserServiceImpl;


public class MainActivity extends Activity {

    private ImageView imageView;

    private UserService userservice = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView) findViewById(R.id.image_2);

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //代码指定该控件加载一幅图片

        //1、
        imageView.setImageResource(R.drawable.man);

        //2、
        imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.man));

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        //HttpURLConnection

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //final Bitmap bitMap = userservice.getImageGet();
                    final Bitmap bitMap = userservice.getImageByPost();
                    if (bitMap != null) {

                        //更新UI方式1、
                        //imageView.post();

                        //更新UI方式2、通过Handler

                        //更新UI方式3、
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitMap);
                            }
                        });
                    }
                } catch (final ServiceRulesException e) {
                    /**
                     * 业务异常
                     */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    /**
                     * 系统异常
                     */
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "载入远程图片失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    }
}
