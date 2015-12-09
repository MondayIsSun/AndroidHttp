package mr.lmd.personal.http_05.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mr.lmd.personal.http_05.R;
import mr.lmd.personal.http_05.bean.Student;


/**
 * Created by Administrator on 2015/4/17.
 */
public class StudentAdapter extends ArrayAdapter<Student> {

    private LayoutInflater inflater;
    private int mResource;

    public StudentAdapter(Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout view;
        if (convertView == null) {
            view = (LinearLayout) inflater.inflate(mResource, null);
        } else {
            view = (LinearLayout) convertView;
        }

        Student student = getItem(position);

        TextView txtId = (TextView) view.findViewById(R.id.txt_id);
        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        TextView txtAge = (TextView) view.findViewById(R.id.txt_age);

        txtId.setText(String.valueOf(student.getId()));
        txtName.setText(student.getName());
        txtAge.setText(String.valueOf(student.getAge()));

        return view;
    }
}
