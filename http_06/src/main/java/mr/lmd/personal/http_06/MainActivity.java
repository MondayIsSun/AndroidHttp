package mr.lmd.personal.http_06;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import mr.lmd.personal.http_06.service.ServiceRulesException;
import mr.lmd.personal.http_06.service.UserService;
import mr.lmd.personal.http_06.service.UserServiceImpl;


public class MainActivity extends Activity {

    public static final String MSG_SERVER_ERROR = "服务器错误";

    private Button btn_select;
    private Button btn_download;

    private static final int FLAG_LOAD_IMAGE = 1;

    private String pathName;

    private UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //上传操作
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, FLAG_LOAD_IMAGE);
            }
        });

        //下载操作
        btn_download = (Button) findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            //执行下载操作
                            userService.userDownload();

                            //下载成功后提示UI线程
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "下载完毕", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (final ServiceRulesException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "下载出错", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

    }

    /**
     * *************************************************************************************************************
     */

    /**
     * 上传业务逻辑
     */

    private void doUpload() {
        //开线程 ---> 服务器访问操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = new FileInputStream(new File(pathName));
                    Map<String, String> data = new HashMap<>();
                    data.put("Name", "Mr.Lin");
                    data.put("Gender", "man");
                    final String result = userService.userUpload(in, data);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (ServiceRulesException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FLAG_LOAD_IMAGE) {

            if (data == null) {
                Toast.makeText(this, "你没有选择任何图片", Toast.LENGTH_LONG).show();
            } else {
                Uri uri = data.getData();

                if (uri == null) {
                    Toast.makeText(this, "你没有选择任何图片", Toast.LENGTH_LONG).show();
                } else {
                    String path = null;
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, pojo, null, null, null);

                    //Toast.makeText(this, cursor.getColumnCount(), Toast.LENGTH_LONG).show();

                    if (cursor != null) {
                        int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                        cursor.moveToFirst();
                        path = cursor.getString(columnIndex);
                        cursor.close();
                    }

                    if (path == null) {
                        Toast.makeText(this, "未能获取图片的物理路径", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(this, "图片的物理路径:" + path, Toast.LENGTH_LONG).show();
                        pathName = path;

                        //弹出对话框
                        new AlertDialog.Builder(this)
                                .setTitle("提示")
                                .setMessage("你要上传选择的图片吗?")
                                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doUpload();
                                    }
                                })
                                .create()
                                .show();
                    }
                }
            }
        }
    }

    /**
     * *************************************************************************************************************
     */
}
