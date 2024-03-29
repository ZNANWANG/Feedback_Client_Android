package com.example.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import dbclass.Criteria;
import dbclass.Mark;
import dbclass.ProjectInfo;
import dbclass.ShortText;
import dbclass.SubSection;
import main.AllFunctions;


public class Activity_Assessment extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    MyAdapter myAdapter;
    MyAdapter3 myAdapter3;
    int indexOfProject;
    int indexOfStudent;
    int indexOfGroup;
    ArrayList<Integer> studentList;
    private static ProjectInfo project;
    ArrayList<Criteria> criteriaList;
    ArrayList<Criteria> commentList;
    ListView lv_individual;
    ListView lv_otherComment;
    TextView tv_time;
    Button btn_assessment_start;
    Button btn_assessment_refresh;
    TextView tv_assessment_student;
    TextView tv_assessment_total_mark;
    SeekBar sb_mark;
    TextView tv_mark;
    Double totalMark = 0.0;
    int totalWeighting = 0;
    EditText et_other_comment;
    private long durationTime = 0;
    private long warningTime = 0;
    private boolean isPause = false;
    private CountDownTimer countDownTimer;
    private long leftTime = 0;
    private int flag = 0;
    static private int matrixOfMarkedCriteria[][];
    static private int matrixOfCommentOnly[][];
    static private int matrixCriteriaLongtext[][];
    static private int matrixCommentLongText[][];
    private AllFunctions allFunctions;
    private Handler handler;
    private AlertDialog dialog;
    private String from;
    public static final String FROMREALTIME = "realtime";
    public static final String FROMREVIEW = "review";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);

        tv_assessment_student = findViewById(R.id.tv_assessment_student);
        studentList = new ArrayList<>();

        if (indexOfGroup == -999) {
            tv_assessment_student.setText(project.getStudentInfo().get(indexOfStudent).getNumber() + " --- " +
                    project.getStudentInfo().get(indexOfStudent).getFirstName() + " " +
                    project.getStudentInfo().get(indexOfStudent).getMiddleName() + " " +
                    project.getStudentInfo().get(indexOfStudent).getSurname());
            studentList.add(indexOfStudent);
            Log.d("EEEE", "index of student " + indexOfStudent);
        } else {
            tv_assessment_student.setText("Group " + indexOfGroup);
            for (int i = 0; i < project.getStudentInfo().size(); i++) {
                if (project.getStudentInfo().get(i).getGroup() == indexOfGroup) {
                    studentList.add(i);
                }
            }
            Log.d("EEEE", "student list" + studentList);
        }

        tv_assessment_total_mark = findViewById(R.id.tv_assessment_total_mark);

        String json = new Gson().toJson(project.getStudentInfo().get(studentList.get(0)).getMark());

        Log.d("EEEE", "student's mark: " + json);

        if (project.getStudentInfo().get(studentList.get(0)).getMark() != null) {

            markObjectToMatrix(project.getStudentInfo().get(studentList.get(0)).getMark());
            for (int m = 0; m < studentList.size(); m++) {
                for (int n = 0; n < project.getCriteria().size(); n++) {
                    Mark mark = project.getStudentInfo().get(studentList.get(m)).getMark();
                    mark.getCriteriaList().get(n).getSubsectionList().clear();
                }
                for (int n = 0; n < project.getCommentList().size(); n++) {
                    project.getStudentInfo().get(studentList.get(m)).getMark().getCommentList().get(n).getSubsectionList().clear();
                }
            }

            for (int j = 0; j < project.getCriteria().size(); j++) {
                totalWeighting = totalWeighting + project.getCriteria().get(j).getMaximunMark();
            }

            for (int k = 0; k < project.getCriteria().size(); k++) {
                totalMark = project.getStudentInfo().get(studentList.get(0)).getMark().getTotalMark();
            }

            tv_assessment_total_mark.setText(String.format("%.2f", project.getStudentInfo().get(studentList.get(0)).getMark().getTotalMark()) + "%");

        } else {
            initMatrix();
            tv_assessment_total_mark.setText("0%");
            for (int m = 0; m < studentList.size(); m++) {
                project.getStudentInfo().get(studentList.get(m)).setMark(new Mark());
                for (int n = 0; n < project.getCriteria().size(); n++) {
                    project.getStudentInfo().get(studentList.get(m)).getMark().getCriteriaList().add(new Criteria());
                    project.getStudentInfo().get(studentList.get(m)).getMark().getCriteriaList().get(n).setName(project.getCriteria().get(n).getName());
                    project.getStudentInfo().get(studentList.get(m)).getMark().getCriteriaList().get(n).setMaximunMark(project.getCriteria().get(n).getMaximunMark());
                    project.getStudentInfo().get(studentList.get(m)).getMark().getMarkList().add(0.0);
                }
                for (int n = 0; n < project.getCommentList().size(); n++) {
                    project.getStudentInfo().get(studentList.get(m)).getMark().getCommentList().add(new Criteria());
                    project.getStudentInfo().get(studentList.get(m)).getMark().getCommentList().get(n).setName(project.getCommentList().get(n).getName());
                }
            }

            for (int j = 0; j < project.getCriteria().size(); j++) {
                totalWeighting = totalWeighting + project.getCriteria().get(j).getMaximunMark();
            }
        }

        lv_individual = findViewById(R.id.lv_individual);
        lv_otherComment = findViewById(R.id.lv_otherComment);
        init();

        tv_time = findViewById(R.id.tv_time);
        tv_time.setText(String.format("%02d", durationTime / 1000 / 60) + ":" + String.format("%02d", durationTime / 1000 % 60));

        btn_assessment_start = findViewById(R.id.btn_assessment_start);
        btn_assessment_refresh = findViewById(R.id.btn_assessment_refresh);

        findViewById(R.id.btn_assessment_start).setOnClickListener(this);
        findViewById(R.id.btn_assessment_refresh).setOnClickListener(this);

        btn_assessment_start.setEnabled(true);
        btn_assessment_refresh.setEnabled(false);
        initTimer(durationTime);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        allFunctions = AllFunctions.getObject();
        allFunctions.setHandler(handler);
        lv_individual.setAdapter(myAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_assessment);
        mToolbar.setTitle("Assessment -- Welcome, " + AllFunctions.getObject().getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                allFunctions.syncProjectList();
            }
        });
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Assessment.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Assessment.this,
                                Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 210:
                        Toast.makeText(Activity_Assessment.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        if (from.equals(FROMREALTIME)) {
                            finish();
                        } else if (from.equals(Activity_Editable_Individual_Report.FROMREALTIMEEDIT)
                                || from.equals(Activity_Editable_Individual_Report.FROMREVIEWEDIT)) {
                            if (checkAllCriteria()) {
                                addSubsectionToMarkObject();
                                for (int i = 0; i < studentList.size(); i++) {
                                    project.getStudentInfo().get(studentList.get(i)).setTotalMark(project.getStudentInfo().get(studentList.get(i)).getMark().getTotalMark());
                                    AllFunctions.getObject().sendMark(project, project.getStudentInfo().get(studentList.get(i)).getNumber(), project.getStudentInfo().get(studentList.get(i)).getMark());
                                }
                            } else {
                                Toast.makeText(Activity_Assessment.this, "You have one or more comments not selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case 211:
                        Toast.makeText(Activity_Assessment.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    case 351:
                        Toast.makeText(Activity_Assessment.this,
                                "Record mark success", Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
                        if (from.equals(Activity_Realtime_Assessment.FROMREALTIME)) {
                            Intent intent = new Intent(Activity_Assessment.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("from", FROMREALTIME);
                            startActivity(intent);
                            finish();
                        } else if (from.equals(Activity_Editable_Individual_Report.FROMREALTIMEEDIT)) {
                            Intent intent = new Intent(Activity_Assessment.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("from", FROMREALTIME);
                            startActivity(intent);
                            finish();
                        } else if (from.equals(Activity_Editable_Individual_Report.FROMREVIEWEDIT)) {
                            Intent intent = new Intent(Activity_Assessment.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                            intent.putExtra("from", FROMREVIEW);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 352:
                        Toast.makeText(Activity_Assessment.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        allFunctions = AllFunctions.getObject();
        allFunctions.setHandler(handler);
        criteriaList = project.getCriteria();
        commentList = project.getCommentList();
        durationTime = project.getDurationMin() * 60000 + project.getDurationSec() * 1000;
        warningTime = project.getWarningMin() * 60000 + project.getWarningSec() * 1000;
        myAdapter = new MyAdapter(criteriaList, this);
        myAdapter3 = new MyAdapter3(studentList, this);

        lv_individual.setAdapter(myAdapter);
        setListViewHeightBasedOnChildren(lv_individual);
        lv_otherComment.setAdapter(myAdapter3);
        setListViewHeightBasedOnChildren(lv_otherComment);
    }

    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Criteria> criteriaList;
        private Double increment = 0.0;

        public MyAdapter(ArrayList<Criteria> criteriaList, Context context) {
            this.criteriaList = criteriaList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return criteriaList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_individual_assessment, parent, false);
            final View view10 = convertView;
            TextView tv_criteria_name = convertView.findViewById(R.id.tv_criteria_name);
            tv_criteria_name.setText(project.getCriteria().get(position).getName());

            if (project.getCriteria().get(position).getMarkIncrement().equals("1")) {
                increment = 1.0;
            } else if (project.getCriteria().get(position).getMarkIncrement().equals("1/2")) {
                increment = 0.5;
            } else if (project.getCriteria().get(position).getMarkIncrement().equals("1/4")) {
                increment = 0.25;
            }

            TextView tv_red = view10.findViewById(R.id.tv_red);
            TextView tv_yellow = view10.findViewById(R.id.tv_yellow);
            TextView tv_green = view10.findViewById(R.id.tv_green);

            ArrayList<Integer> weightList = new ArrayList<>();

            weightList.add(0, 0);
            weightList.add(1, 0);
            weightList.add(2, 0);


            if (getMatrixMarkedCriteria(position).size() != 0) {
                for (int i = 0; i < getMatrixMarkedCriteria(position).size(); i++) {

                    int j = getMatrixMarkedCriteria(position).get(i).get(0);
                    int m = getMatrixMarkedCriteria(position).get(i).get(1);

                    if (project.getCriteria().get(position).getSubsectionList().get(j).getShortTextList().get(m).getGrade() == 1) {
                        weightList.set(0, (weightList.get(0) + 1));
                    } else if (project.getCriteria().get(position).getSubsectionList().get(j).getShortTextList().get(m).getGrade() == 2) {
                        weightList.set(1, (weightList.get(1) + 1));
                    } else if (project.getCriteria().get(position).getSubsectionList().get(j).getShortTextList().get(m).getGrade() == 3) {
                        weightList.set(2, (weightList.get(2) + 1));
                    }
                }
            }

            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, weightList.get(0));

            tv_red.setLayoutParams(param1);

            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, weightList.get(1));

            tv_yellow.setLayoutParams(param2);

            LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, weightList.get(2));

            tv_green.setLayoutParams(param3);

            Button btn_assessment_comment = convertView.findViewById(R.id.btn_assessment_comment_back);
            btn_assessment_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Activity_Assessment.this, Activity_Assessment_Comment.class);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfCriteria", String.valueOf(position));
                    intent.putExtra("indexOfComment", String.valueOf(-999));

                    startActivity(intent);
                }
            });

            sb_mark = convertView.findViewById(R.id.sb_mark);
            tv_mark = convertView.findViewById(R.id.tv_mark);
            sb_mark.setMax((int) (project.getCriteria().get(position).getMaximunMark() / increment));
            final View view2 = convertView;
            sb_mark.setProgress((int) (project.getStudentInfo().get(studentList.get(0)).getMark().getMarkList().get(position) / increment));
            tv_mark.setText((project.getStudentInfo().get(studentList.get(0)).getMark().getMarkList().get(position) + " / " + Double.valueOf(project.getCriteria().get(position).getMaximunMark())));

            sb_mark.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    if (project.getCriteria().get(position).getMarkIncrement().equals("1")) {
                        increment = 1.0;
                    } else if (project.getCriteria().get(position).getMarkIncrement().equals("1/2")) {
                        increment = 0.5;
                    } else if (project.getCriteria().get(position).getMarkIncrement().equals("1/4")) {
                        increment = 0.25;
                    }

                    Double progressDisplay = progress * increment;
                    tv_mark = view2.findViewById(R.id.tv_mark);
                    tv_mark.setText(String.valueOf(progressDisplay) + " / " + project.getCriteria().get(position).getMaximunMark());

                    for (int i = 0; i < studentList.size(); i++) {
                        if (project.getStudentInfo().get(studentList.get(i)).getMark().getMarkList() == null) {
                            project.getStudentInfo().get(studentList.get(i)).getMark().getMarkList().add(position, progressDisplay);
                        } else {
                            project.getStudentInfo().get(studentList.get(i)).getMark().getMarkList().set(position, progressDisplay);
                        }
                    }

                    totalMark();
                    tv_assessment_total_mark.setText(String.format("%.2f", project.getStudentInfo().get(studentList.get(0)).getMark().getTotalMark()) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return convertView;
        }

    }

    public void totalMark() {
        for (int i = 0; i < studentList.size(); i++) {
            Double sum = 0.0;
            for (int k = 0; k < project.getStudentInfo().get(studentList.get(i)).getMark().getMarkList().size(); k++) {
                sum = sum + project.getStudentInfo().get(studentList.get(i)).getMark().getMarkList().get(k) * (100.0 / totalWeighting);
                project.getStudentInfo().get(studentList.get(i)).getMark().setTotalMark(sum);
            }
        }
    }


    public void initTimer(long millisUntilFinished) {

        btn_assessment_start.setEnabled(true);

        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                if (leftTime < warningTime) {
                    tv_time.setTextColor(android.graphics.Color.RED);
                }
                tv_time.setText(String.format("%02d", millisUntilFinished / 1000 / 60) + ":" + String.format("%02d", millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                tv_time.setText("00:00");
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_assessment_start:
                if (flag == 0) {
                    isPause = false;
                    countDownTimer.start();
                    btn_assessment_start.setBackgroundResource(R.drawable.ic_pause);
                    flag = 1;
                    btn_assessment_refresh.setEnabled(false);
                    break;
                } else if (flag == 1) {
                    if (!isPause) {
                        isPause = true;
                        countDownTimer.cancel();
                    }

                    btn_assessment_start.setBackgroundResource(R.drawable.ic_start);
                    flag = 2;
                    btn_assessment_refresh.setEnabled(true);
                    break;

                } else {
                    if (leftTime != 0 && isPause) {
                        countDownTimer.start();
                        isPause = false;

                    }
                    btn_assessment_refresh.setEnabled(false);
                    btn_assessment_start.setBackgroundResource(R.drawable.ic_pause);

                    flag = 1;
                    break;
                }

            case R.id.btn_assessment_refresh:
                countDownTimer.cancel();
                btn_assessment_refresh.setEnabled(false);
                tv_time.setText(String.format("%02d", durationTime / 1000 / 60) + ":" + String.format("%02d", durationTime / 1000 % 60));
                initTimer(durationTime);
                flag = 0;
                break;
            default:
                break;
        }
    }

    public class MyAdapter3 extends BaseAdapter {

        private Context mContext;
        private ArrayList<Integer> studentList;

        public MyAdapter3(ArrayList<Integer> studentList,
                          Context context) {
            this.studentList = studentList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return studentList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_other_comment, parent, false);

            TextView tv_other_comment = convertView.findViewById(R.id.tv_other_comment);
            tv_other_comment.setText("For " + project.getStudentInfo().get(studentList.get(position)).getFirstName() + " "
                    + project.getStudentInfo().get(studentList.get(position)).getMiddleName() + " "
                    + project.getStudentInfo().get(studentList.get(position)).getSurname());
            Button btn_assessment_save = convertView.findViewById(R.id.btn_assessment_save);
            et_other_comment = convertView.findViewById(R.id.et_other_comment);

            if (project.getStudentInfo().get(studentList.get(position)).getMark() != null) {
                et_other_comment.setText(project.getStudentInfo().get(studentList.get(position)).getMark().getComment());
            }
            final View view4 = convertView;
            btn_assessment_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    et_other_comment = view4.findViewById(R.id.et_other_comment);
                    String otherComment = et_other_comment.getText().toString();
                    project.getStudentInfo().get(studentList.get(position)).getMark().setComment(otherComment);
                }
            });
            return convertView;
        }
    }

    public void finishAssessment(View view) {

        if (checkAllCriteria()) {
            addSubsectionToMarkObject();

            for (int i = 0; i < studentList.size(); i++) {
                project.getStudentInfo().get(studentList.get(i)).setTotalMark(project.getStudentInfo().get(studentList.get(i)).getMark().getTotalMark());
                AllFunctions.getObject().sendMark(project, project.getStudentInfo().get(studentList.get(i)).getNumber(), project.getStudentInfo().get(studentList.get(i)).getMark());
            }

//            LayoutInflater layoutInflater = LayoutInflater.from(Activity_Assessment.this);
//            final View view2 = layoutInflater.from(Activity_Assessment.this).inflate(R.layout.dialog_record_mark, null);
//            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Assessment.this);
//            dialog = builder.create();
//            dialog.setCancelable(false);
//            dialog.setView(view2);
//            dialog.show();
        } else {
            Toast.makeText(this, "You have one or more comments not selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private void initMatrix() {
        matrixOfMarkedCriteria = new int[project.getCriteria().size()][10];
        matrixCriteriaLongtext = new int[project.getCriteria().size()][10];
        for (int i = 0; i < project.getCriteria().size(); i++)
            for (int j = 0; j < 10; j++)
                matrixOfMarkedCriteria[i][j] = -999;
        matrixOfCommentOnly = new int[project.getCommentList().size()][10];
        matrixCommentLongText = new int[project.getCommentList().size()][10];
        for (int i = 0; i < project.getCommentList().size(); i++)
            for (int j = 0; j < 10; j++)
                matrixOfCommentOnly[i][j] = -999;
    }

    static public void saveCommentToMatrixCriteria(int criteriaIndex, int subsectionIndex, int shortIndex, int longIndex) {
        matrixOfMarkedCriteria[criteriaIndex][subsectionIndex] = shortIndex;
        matrixCriteriaLongtext[criteriaIndex][subsectionIndex] = longIndex;
    }

    static public void saveCommentToMatrixCommentOnly(int criteriaIndex, int subsectionIndex, int shortIndex, int longIndex) {
        matrixOfCommentOnly[criteriaIndex][subsectionIndex] = shortIndex;
        matrixCommentLongText[criteriaIndex][subsectionIndex] = longIndex;
    }

    static public ArrayList<ArrayList<Integer>> getMatrixMarkedCriteria(int criteriaIndex) {
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < project.getCriteria().get(criteriaIndex).getSubsectionList().size(); i++) {
            if (matrixOfMarkedCriteria[criteriaIndex][i] != -999) {
                ArrayList<Integer> arrayList_ls = new ArrayList<Integer>();
                arrayList_ls.add(i);
                arrayList_ls.add(matrixOfMarkedCriteria[criteriaIndex][i]);
                arrayList_ls.add(matrixCriteriaLongtext[criteriaIndex][i]);
                arrayLists.add(arrayList_ls);
            }
        }
        return arrayLists;
    }

    static public ArrayList<ArrayList<Integer>> getMatrixCommentOnly(int criteriaIndex) {
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < project.getCommentList().get(criteriaIndex).getSubsectionList().size(); i++) {
            if (matrixOfCommentOnly[criteriaIndex][i] != -999) {
                ArrayList<Integer> arrayList_ls = new ArrayList<Integer>();
                arrayList_ls.add(i);
                arrayList_ls.add(matrixOfCommentOnly[criteriaIndex][i]);
                arrayList_ls.add(matrixCommentLongText[criteriaIndex][i]);
                arrayLists.add(arrayList_ls);
            }
        }
        return arrayLists;
    }

    static public boolean markedCriteriaSelectedAll(int criteriaIndex) {
        for (int i = 0; i < project.getCriteria().get(criteriaIndex).getSubsectionList().size(); i++) {
            if (matrixOfMarkedCriteria[criteriaIndex][i] == -999)
                return false;
        }
        return true;
    }

    static public boolean commentOnlySelectedAll(int criteriaIndex) {
        for (int i = 0; i < project.getCommentList().get(criteriaIndex).getSubsectionList().size(); i++) {
            if (matrixOfCommentOnly[criteriaIndex][i] == -999)
                return false;
        }
        return true;
    }

    private void addSubsectionToMarkObject() {
        for (int i = 0; i < studentList.size(); i++) {
            ArrayList<Criteria> criteriaArrayList = project.getStudentInfo().get(studentList.get(i)).getMark().getCriteriaList();
            for (int j = 0; j < criteriaArrayList.size(); j++) {
                for (int k = 0; k < project.getCriteria().get(j).getSubsectionList().size(); k++) {
                    String longText_ls = project.getCriteria().get(j).getSubsectionList().get(k).getShortTextList().get(matrixOfMarkedCriteria[j][k]).getLongtext().get(matrixCriteriaLongtext[j][k]);
                    ShortText shortText_ls = new ShortText();
                    shortText_ls.setName(project.getCriteria().get(j).getSubsectionList().get(k).getShortTextList().get(matrixOfMarkedCriteria[j][k]).getName());
                    shortText_ls.setGrade(project.getCriteria().get(j).getSubsectionList().get(k).getShortTextList().get(matrixOfMarkedCriteria[j][k]).getGrade());
                    shortText_ls.getLongtext().add(longText_ls);
                    SubSection subSection_ls = new SubSection();
                    subSection_ls.setName(project.getCriteria().get(j).getSubsectionList().get(k).getName());
                    subSection_ls.getShortTextList().add(shortText_ls);
                    criteriaArrayList.get(j).getSubsectionList().add(subSection_ls);
                }
            }

            ArrayList<Criteria> commentOnlyList = project.getStudentInfo().get(studentList.get(i)).getMark().getCommentList();
            for (int j = 0; j < commentOnlyList.size(); j++) {
                for (int k = 0; k < project.getCommentList().get(j).getSubsectionList().size(); k++) {
                    String longText_ls = project.getCommentList().get(j).getSubsectionList().get(k).getShortTextList().get(matrixOfCommentOnly[j][k]).getLongtext().get(matrixCommentLongText[j][k]);
                    ShortText shortText_ls = new ShortText();
                    shortText_ls.setName(project.getCommentList().get(j).getSubsectionList().get(k).getShortTextList().get(matrixOfCommentOnly[j][k]).getName());
                    shortText_ls.setGrade(project.getCommentList().get(j).getSubsectionList().get(k).getShortTextList().get(matrixOfCommentOnly[j][k]).getGrade());

                    shortText_ls.getLongtext().add(longText_ls);
                    SubSection subSection_ls = new SubSection();
                    subSection_ls.setName(project.getCommentList().get(j).getSubsectionList().get(k).getName());
                    subSection_ls.getShortTextList().add(shortText_ls);
                    commentOnlyList.get(j).getSubsectionList().add(subSection_ls);
                }
            }
        }
    }

    private boolean checkAllCriteria() {
        for (int i = 0; i < project.getCriteria().size(); i++) {
            if (matrixOfMarkedCriteria[i][0] == -999)
                return false;
        }
        for (int i = 0; i < project.getCommentList().size(); i++) {
            if (matrixOfCommentOnly[i][0] == -999)
                return false;
        }
        return true;
    }

    private void markObjectToMatrix(Mark mark) {
        initMatrix();
        for (int i = 0; i < mark.getCriteriaList().size(); i++) {
            //criteria layer
            for (int j = 0; j < project.getCriteria().get(i).getSubsectionList().size(); j++) {
                //subsection layer
                OUT:
                for (int k = 0; k < project.getCriteria().get(i).getSubsectionList().get(j).getShortTextList().size(); k++) {
                    //shortText layer
                    if (project.getCriteria().get(i).getSubsectionList().get(j).getShortTextList().get(k).getName().
                            equals(mark.getCriteriaList().get(i).getSubsectionList().get(j).getShortTextList().get(0).getName())) {
                        for (int p = 0; p < project.getCriteria().get(i).getSubsectionList().get(j).getShortTextList().get(k).getLongtext().size(); p++) {
                            if (project.getCriteria().get(i).getSubsectionList().get(j).getShortTextList().get(k).getLongtext().get(p).
                                    equals(mark.getCriteriaList().get(i).getSubsectionList().get(j).getShortTextList().get(0).getLongtext().get(0))) {
                                matrixOfMarkedCriteria[i][j] = k;
                                matrixCriteriaLongtext[i][j] = p;
                                break OUT;
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < mark.getCommentList().size(); i++) {
            //criteria layer
            for (int j = 0; j < project.getCommentList().get(i).getSubsectionList().size(); j++) {
                //subsection layer
                OUT:
                for (int k = 0; k < project.getCommentList().get(i).getSubsectionList().get(j).getShortTextList().size(); k++) {
                    //shortText layer
                    if (project.getCommentList().get(i).getSubsectionList().get(j).getShortTextList().get(k).getName().
                            equals(mark.getCommentList().get(i).getSubsectionList().get(j).getShortTextList().get(0).getName())) {
                        for (int p = 0; p < project.getCommentList().get(i).getSubsectionList().get(j).getShortTextList().get(k).getLongtext().size(); p++) {
                            if (project.getCommentList().get(i).getSubsectionList().get(j).getShortTextList().get(k).getLongtext().get(p).
                                    equals(mark.getCommentList().get(i).getSubsectionList().get(j).getShortTextList().get(0).getLongtext().get(0))) {
                                matrixOfCommentOnly[i][j] = k;
                                matrixCommentLongText[i][j] = p;
                                break OUT;
                            }
                        }
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        allFunctions.syncProjectList();
    }
}
