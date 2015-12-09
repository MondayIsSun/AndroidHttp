package mr.lmd.personal.http_03;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import mr.lmd.personal.http_03.service.ServiceRulesException;
import mr.lmd.personal.http_03.service.UserService;
import mr.lmd.personal.http_03.service.UserServiceImpl;


public class MainActivity extends Activity {

    //登录
    private EditText txtLoginName;
    private EditText txtLoginPassword;
    private Button btnLoginGet;
    private Button btnLoginPost;
    private Button btnReset;

    //注册
    private EditText txtLoginNameRegister;
    private CheckBox chkGame;
    private CheckBox chkMusic;
    private CheckBox chkSport;
    private Button btnRegister;

    private UserService userService = new UserServiceImpl();

    /**
     * Ctrl + Shift + U 大小写转换快捷键
     */

    //登录
    private static final int FLAG_LOGIN_SUCCESS = 1;
    private static final String MSG_LOGIN_ERROR = "登录出错";
    private static final String MSG_LOGIN_SUCCESS = "登录成功";
    public static final String MSG_LOGIN_FAILED = "登录名|登录密码错误";
    public static final String MSG_SERVER_ERROR = "服务器错误";
    public static final String MSG_REQUEST_TIMEOUT = "请求服务器超时";
    public static final String MSG_RESPONSE_TIMEOUT = "服务器响应超时";

    //注册
    private static final int FLAG_REGISTER_SUCCESS = 2;
    private static final String MSG_REGISTER_SUCCESS = "注册成功";
    public static final String MSG_REGISTER_ERROR = "注册出错";

    private static ProgressDialog dialog;

    private void init() {
        //登录
        txtLoginName = (EditText) findViewById(R.id.txt_login_name);
        txtLoginPassword = (EditText) findViewById(R.id.txt_login_password);
        btnLoginGet = (Button) findViewById(R.id.btn_login_get);
        btnLoginPost = (Button) findViewById(R.id.btn_login_post);
        btnReset = (Button) findViewById(R.id.btn_reset);

        //注册
        txtLoginNameRegister = (EditText) findViewById(R.id.txt_login_name_register);
        chkGame = (CheckBox) findViewById(R.id.chkGame);
        chkMusic = (CheckBox) findViewById(R.id.chkMusic);
        chkSport = (CheckBox) findViewById(R.id.chkSport);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        init();

        //点击登录，以Get方式
        btnLoginGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 在Activity里面做的只是取到这两个值而已，不要做这个层额外的事
                 */
                final String username = txtLoginName.getText().toString();
                final String password = txtLoginPassword.getText().toString();

                /**
                 * 输入值的验证 ---> 这些东西就不要传到服务器进行验证了，因为这样做是很消耗资源的
                 */

                /**
                 * loading...
                 */
                if (dialog == null) {
                    dialog = new ProgressDialog(MainActivity.this);
                }
                dialog.setTitle("请等待");
                dialog.setMessage("登录中...");
                dialog.setCancelable(false);
                dialog.show();
                /**
                 * 通过副线程进行客户端与服务端通信
                 */
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userService.userLoginGet(username, password);
                            //能运行到这里说明在交互过程当总并没有出现任何的异常，也就是成功了
                            handler.sendEmptyMessage(FLAG_LOGIN_SUCCESS);
                        } catch (ServiceRulesException e) {
                            /**
                             * 业务异常的捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", e.getMessage());
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            /**
                             * 系统异常的捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_LOGIN_ERROR);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }
                });
                thread.start();
            }
        });

        //点击登录，以Post方式
        btnLoginPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = txtLoginName.getText().toString();
                final String password = txtLoginPassword.getText().toString();

                if (dialog == null) {
                    dialog = new ProgressDialog(MainActivity.this);
                }
                dialog.setTitle("请等待");
                dialog.setMessage("登录中...");
                dialog.setCancelable(false);
                dialog.show();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            userService.userLoginPost(username, password);
                            handler.sendEmptyMessage(FLAG_LOGIN_SUCCESS);
                        } catch (ConnectTimeoutException e) {
                            /**
                             * 请求服务器超时异常捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_REQUEST_TIMEOUT);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (SocketTimeoutException e) {
                            /**
                             * 服务器响应超时异常捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_RESPONSE_TIMEOUT);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (ServiceRulesException e) {
                            /**
                             * 业务异常的捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", e.getMessage());
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            /**
                             * 系统异常的捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_LOGIN_ERROR);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }
                });
                thread.start();
            }
        });

        //点击重置
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLoginName.setText("");
                txtLoginPassword.setText("");
            }
        });


        //点击注册
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 获取页面的用户数据
                 */
                final String loginName = txtLoginNameRegister.getText().toString();

                final List<String> interesting = new ArrayList<>();
                if (chkGame.isChecked()) {
                    interesting.add(chkGame.getText().toString());
                }
                if (chkMusic.isChecked()) {
                    interesting.add(chkMusic.getText().toString());
                }
                if (chkSport.isChecked()) {
                    interesting.add(chkSport.getText().toString());
                }

                /**
                 * loading...
                 */
                if (dialog == null) {
                    dialog = new ProgressDialog(MainActivity.this);
                }
                dialog.setTitle("请等待");
                dialog.setMessage("登录中...");
                dialog.setCancelable(false);
                dialog.show();

                /**
                 * 副线程进行注册操作
                 */
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //该方法抛出各种异常，统一在这里捕获处理
                            userService.userRegister(loginName, interesting);

                            //没有抛出异常，说明注册成功
                            handler.sendEmptyMessage(FLAG_REGISTER_SUCCESS);
                        } catch (ServiceRulesException e) {
                            /**
                             * 业务异常的捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", e.getMessage());
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (ConnectTimeoutException e) {
                            /**
                             * 请求服务器超时异常捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_REQUEST_TIMEOUT);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (SocketTimeoutException e) {
                            /**
                             * 服务器响应超时异常捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_RESPONSE_TIMEOUT);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            /**
                             * 系统异常的捕获
                             */
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_REGISTER_ERROR);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }
                });
                thread.start();
            }
        });

    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void showTip(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handler只是一个消息的中转站
     */

    /**
     * 使用内部类的时候要注意避免内部类持有外部类引用可能造成的内存泄漏
     */
    private static class IHandler extends Handler {

        //避免内部类持有外部类引用而造成的内存泄露
        //1、不要使用内部类，独立到包里面
        //2、使用静态内部类，但是静态内部类无法持有外部类的应用，所以配合WeakReference的使用
        private final WeakReference<Activity> mActivity;

        public IHandler(MainActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if (dialog != null) {
                dialog.dismiss();
            }

            //msg的what不指定，那么就是int的默认值:0啦
            int flag = msg.what;
            switch (flag) {
                /**
                 * 登录
                 */
                case FLAG_LOGIN_SUCCESS:
                    ((MainActivity) mActivity.get()).showTip(MSG_LOGIN_SUCCESS);
                    break;
                /**
                 * 错误处理
                 */
                case 0:
                    String errorMsg = msg.getData().getSerializable("ErrorMsg").toString();
                    ((MainActivity) mActivity.get()).showTip(errorMsg);
                    break;
                /**
                 * 注册
                 */
                case FLAG_REGISTER_SUCCESS:
                    ((MainActivity) mActivity.get()).showTip(MSG_REGISTER_SUCCESS);
                    break;
            }
        }
    }

    private IHandler handler = new IHandler(this);

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}
