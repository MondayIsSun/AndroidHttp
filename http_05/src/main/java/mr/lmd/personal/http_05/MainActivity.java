package mr.lmd.personal.http_05;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import mr.lmd.personal.http_05.adapter.StudentAdapter;
import mr.lmd.personal.http_05.bean.Student;
import mr.lmd.personal.http_05.service.ServiceRulesException;
import mr.lmd.personal.http_05.service.UserService;
import mr.lmd.personal.http_05.service.UserServiceImpl;


public class MainActivity extends Activity {

    private Button btn_load;
    private ListView listView;

    //本地数据源
    private List<Student> stuList;

    private StudentAdapter adapter;

    private static ProgressDialog dialog;

    private static final int FLAG_STU_SUCCESS = 1;
    private static final String MSG_STUJSON_ERROR = "加载数据出错";
    public static final String MSG_SERVER_ERROR = "服务器出错";

    private UserService userService = new UserServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_load = (Button) findViewById(R.id.btn_load);
        listView = (ListView) findViewById(R.id.listView);

        /*************************************************************************************************/

        //初始化数据源
        stuList = new ArrayList<>();
        stuList.add(new Student(100L, "Tom", 20));
        stuList.add(new Student(101L, "Lisa", 21));
        stuList.add(new Student(102L, "Jack", 23));

        this.adapter = new StudentAdapter(this, R.layout.student_item, this.stuList);

        this.listView.setAdapter(adapter);

        /*************************************************************************************************/

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("请等待");
                dialog.setMessage("加载数据中...");
                dialog.setCancelable(false);
                dialog.show();

                //
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //获取数据
                            stuList = userService.getStudents();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    adapter = new StudentAdapter(MainActivity.this, R.layout.student_item, stuList);
                                    listView.setAdapter(adapter);
                                }
                            });

                            //handler.sendEmptyMessage(FLAG_STU_SUCCESS);
                        } catch (ServiceRulesException e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", e.getMessage());
                            msg.setData(data);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putSerializable("ErrorMsg", MSG_STUJSON_ERROR);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }
                }).start();

            }
        });

        /*************************************************************************************************/

    }

    private void showTip(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

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

            int flag = msg.what;
            switch (flag) {
                case FLAG_STU_SUCCESS:
                    System.out.print("yes 啦");
                    ((MainActivity) mActivity.get()).loadDataListView();
                    ((MainActivity) mActivity.get()).showTip("成功更新数据");
                    break;
                case 0:
                    String errorMsg = msg.getData().getSerializable("ErrorMsg").toString();
                    ((MainActivity) mActivity.get()).showTip(errorMsg);
                    break;
            }
        }
    }

    private IHandler handler = new IHandler(this);

    private void loadDataListView() {
        System.out.print("size ---> " + this.stuList.size());
        this.adapter = new StudentAdapter(this, R.layout.student_item, this.stuList);
        this.listView.setAdapter(adapter);
//        this.adapter.notifyDataSetChanged();
    }
}
