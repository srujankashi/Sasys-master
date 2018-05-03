package com.attendancesystem.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.attendancesystem.R;
import com.attendancesystem.adapter.AttendanceHistoryListAdapter;
import com.attendancesystem.database.DatabaseMain;
import com.attendancesystem.database.entity.Attendance;
import com.attendancesystem.utils.DateConverter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by shraddha on 3/25/2018.
 */

public class AttendanceHistoryActivity extends BaseActivity {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvAttendanceHistory)
    RecyclerView rvAttendanceHistory;

    private List<Attendance> lsAttendance, lsAttendanceForDay;
    private AttendanceHistoryListAdapter attendanceListAdapter;
    private View notDataView;
    private String facultyEmail;


    @Override
    public int getContentView() {
        return R.layout.activity_attendance_history;
    }

    @Override
    public void initData() {
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        tvTitle.setText("Attendance History");
        rvAttendanceHistory.setHasFixedSize(true);
        rvAttendanceHistory.setLayoutManager(new LinearLayoutManager(this));
        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rvAttendanceHistory.getParent(), false);
        notDataView.setOnClickListener(v -> getAttendanceHistory());
        initAdapter(lsAttendance);
    }

    private void initAdapter(List<Attendance> lsAttendance) {
        attendanceListAdapter = new AttendanceHistoryListAdapter(lsAttendance);
        attendanceListAdapter.openLoadAnimation();
        attendanceListAdapter.isFirstOnly(false);
        attendanceListAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        rvAttendanceHistory.setAdapter(attendanceListAdapter);

        attendanceListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivEdit){
                Intent i = new Intent(AttendanceHistoryActivity.this, AttendanceListActivity.class);
                i.putExtra("isEdit", true);
                i.putExtra("subjectCode", attendanceListAdapter.getData().get(position).getSubCode());
                i.putExtra("date", DateConverter.dateToTimestamp(attendanceListAdapter.getData().get(position).getDate()));
                startActivity(i);
            }else if (view.getId() == R.id.ivShare){
                getAttendanceForDay(DateConverter.dateToTimestamp(attendanceListAdapter.getData().get(position).getDate()),
                        attendanceListAdapter.getData().get(position).getSubCode());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAttendanceHistory();
    }

    private void setListData(List<Attendance> lsAttendance){
        if(lsAttendance !=null && lsAttendance.size()>0)
            attendanceListAdapter.setNewData(lsAttendance);
        else
            attendanceListAdapter.setEmptyView(notDataView);
    }

    private void getAttendanceHistory(){
        Observable.fromCallable(() -> DatabaseMain.getDbInstance(this).getAttendanceDao().getAttendanceHistory())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(attendances -> {
                    lsAttendance = new ArrayList<>();
                    lsAttendance.addAll(attendances);

                }, throwable -> {
                    Log.e("ERROR", throwable.getMessage());
                }, () -> {
                    //initAdapter(lsStudent);
                    setListData(lsAttendance);
                });

    }

    private void getAttendanceForDay(String date, String subCode){
        Observable.fromCallable(() -> DatabaseMain.getDbInstance(this).getAttendanceDao().getAttendance(date, subCode))
                .subscribeOn(Schedulers.computation())
                .subscribe(attendances -> {
                    lsAttendanceForDay = new ArrayList<>();
                    lsAttendanceForDay.addAll(attendances);
                    Log.e("edit", attendances.size()+"");
                }, throwable -> {
                    Log.e("ERROR", throwable.getMessage());
                }, () -> {
                    //setListData(lsStudent);
                    //writeDatatoCSVFile(lsAttendanceForDay, subCode+"_");
                    getFacultyEmail(lsAttendanceForDay, date, subCode);
                });
    }

    private void getFacultyEmail(List<Attendance> lsAttendanceForDay,String date,String subCode){
        Observable.fromCallable(() -> DatabaseMain.getDbInstance(this).getUnitDao().getFacultyEmail(subCode))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    facultyEmail = s;
                    Log.e("email", facultyEmail);
                }, throwable -> {
                    Log.e("ERROR", throwable.getMessage());
                }, () -> {
                    //setListData(lsStudent);
                    writeDatatoCSVFile(lsAttendanceForDay, date, subCode, facultyEmail);
                });
    }

    public void writeDatatoCSVFile(List<Attendance> lsAttendance, String date,String subCode, String facultyEmail) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/AttendanceFiles");
            if (!folder.exists()) {
                folder.mkdir();
            }

            final String filepath = folder.toString() + "/" + DateConverter.dateToString(lsAttendance.get(0).getDate())+ "_" +subCode + ".csv";
            File f = new File(filepath);
            CSVWriter writer;

            /*if (f.exists() && !f.isDirectory()) {
                FileWriter mFileWriter = new FileWriter(filepath, true);
                writer = new CSVWriter(mFileWriter);
            } else {

            }*/
            writer = new CSVWriter(new FileWriter(filepath));
            List<String[]> data = new ArrayList<String[]>();
            /*if (f.exists() && !f.isDirectory()) {
                FileWriter mFileWriter = new FileWriter(filepath, true);
                writer = new CSVWriter(mFileWriter);
            } else {

            }*/
            writer = new CSVWriter(new FileWriter(filepath));


            data.add(new String[] {"", "","","" ,"DATE ", DateConverter.dateToString(lsAttendance.get(0).getDate())});
            data.add(new String[] {"", "","","" ,"UNIT", lsAttendance.get(0).getSubCode()});
            data.add(new String[] {"", "","","" ,"LECTURER NAME", lsAttendance.get(0).getFacultyName()});
            data.add(new String[] {"",""});
            data.add(new String[] {"",""});
            data.add(new String[] {"",""});

            data.add(new String[]{"","","","","STUDENT ID","STUDENT NAME", "STATUS"});
            //writer.writeAll(data, false);

            for (int i = 0; i < lsAttendance.size(); i++){
                String status ="";
                if(lsAttendance.get(i).isPresent())
                    status = "Present";
                else
                    status = "Absent";

                data.add(new String[]{"","","","",lsAttendance.get(i).getRollNumber(), lsAttendance.get(i).getStudentName(), status});
            }

            writer.writeAll(data, false);
            writer.close();
            Toast.makeText(AttendanceHistoryActivity.this, "File stored in Storage/AttendanceFiles",Toast.LENGTH_LONG).show();



            Uri path = Uri.fromFile(f);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent .setType("text/csv");
            String to[] = {facultyEmail};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_STREAM, path);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Attendance of "+subCode + " for date " + date);
            startActivity(Intent.createChooser(emailIntent , "Send email..."));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
