package com.example.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class Assessment_Preparation_Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private AllFunctions allFunctions = AllFunctions.getObject();
    private ArrayList<ProjectInfo> projectList;
    private MyAdapter_for_listView myAdapter;
    private MyAdapterForAssessors adapterForAssessors;
    private int index_to_send = -999;
    private ProjectInfo project;
    private Handler handler;
    private Toolbar mToolbar;
    private CheckBox mDeleteCheckbox;
    private Button button_about;
    private Button button_criteria;
    private Button button_student;
    private Button button_assessor;
    private Button button_project_add;
    private Button button_sync_projectlist;
    private TextView textView_projectName;
    private TextView textView_aboutDetail;
    private TextView textView_criteriaDetail;
    private TextView textView_asseccorDetail;
    private TextView student_detail__inpreparation;
    private EditText editText_assessorName;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment__preparation_);
        init();
        Log.d("EEEE", "Preparation: onCreate has been called!");

    }

    protected void onNewIntent(Intent intent) {
//        init();
        Log.d("EEEE", "Preparation: onNewIntent has been called!");
    }

    private void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 201: //创建新项目成功
                        break;
                    case 210:
                        Toast.makeText(Assessment_Preparation_Activity.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        updateProjectList();
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
        textView_projectName = findViewById(R.id.project_name_inpreparation);
        textView_aboutDetail = findViewById(R.id.about_detail__inpreparation);
        textView_criteriaDetail = findViewById(R.id.criteria_detail__inpreparation);
        textView_asseccorDetail = findViewById(R.id.asseccor_detail__inpreparation);
        student_detail__inpreparation = findViewById(R.id.student_detail__inpreparation);
        button_about = findViewById(R.id.button_about_inpreparation);
        button_criteria = findViewById(R.id.button_criteria_inpreparation);
        button_student = findViewById(R.id.button_studentmanagement__inpreparation);
        button_assessor = findViewById(R.id.button_asseccor__inpreparation);
        button_sync_projectlist = findViewById(R.id.button_sync_projectlist);
        resetDetailView();
        initToolbar();
        projectList = allFunctions.getProjectList();

        button_project_add = findViewById(R.id.button_plus_inpreparation);
        MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                (Assessment_Preparation_Activity.this, AllFunctions.getObject().getProjectList());
        listView = findViewById(R.id.listView_inpreparation);
        listView.setAdapter(myAdapterDefaultlistView);
        listView.setOnItemClickListener(this);

        myAdapter = new MyAdapter_for_listView(projectList, Assessment_Preparation_Activity.this);
        mDeleteCheckbox = findViewById(R.id.cb_delete_project);
        mDeleteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    projectList = allFunctions.getProjectList();
                    myAdapter = new MyAdapter_for_listView(projectList, Assessment_Preparation_Activity.this);
                    Log.d("EEEE", this.getClass().getSimpleName());

                    listView.setAdapter(myAdapter);
                    listView.setOnItemClickListener(Assessment_Preparation_Activity.this);
                    button_about.setEnabled(false);
                    button_criteria.setEnabled(false);
                    button_student.setEnabled(false);
                    button_assessor.setEnabled(false);
                    button_project_add.setEnabled(false);
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_add_disabled, null);
                    button_project_add.setBackground(drawable);
                    button_sync_projectlist.setEnabled(false);
                    button_sync_projectlist.setBackgroundResource(R.drawable.ic_sync_disabled);
                } else {
                    resetDetailView();
                    MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                            (Assessment_Preparation_Activity.this, AllFunctions.getObject().getProjectList());
                    listView = findViewById(R.id.listView_inpreparation);
                    listView.setAdapter(myAdapterDefaultlistView);
                    listView.setOnItemClickListener(Assessment_Preparation_Activity.this);
                    button_project_add.setEnabled(true);
                    button_project_add.setBackgroundResource(R.drawable.ripple_add_project);
                    button_sync_projectlist.setEnabled(true);
                    button_sync_projectlist.setBackgroundResource(R.drawable.ripple_sync_project);
                }
            }
        });
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_assessment_presentation);
        mToolbar.setTitle("Projects");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
//        mToolbar.inflateMenu(R.menu.menu_toolbar);
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Assessment_Preparation_Activity.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Assessment_Preparation_Activity.this,
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        index_to_send = position;
        for (int i = 0; i < parent.getChildCount(); i++)
            parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
        view.setBackgroundColor(Color.parseColor("#dbdbdb"));
        showOtherInfo(position);
        button_about.setEnabled(true);
        button_criteria.setEnabled(true);
        button_student.setEnabled(true);
        button_assessor.setEnabled(true);
    }

    public void showOtherInfo(int index) {
        ProjectInfo projectInfo = allFunctions.getProjectList().get(index);
        textView_projectName = findViewById(R.id.project_name_inpreparation);
        textView_projectName.setText(projectInfo.getProjectName());
        textView_aboutDetail = findViewById(R.id.about_detail__inpreparation);
        textView_aboutDetail.setText("Subject Name: " + projectInfo.getSubjectName() + "\n" +
                "Subject Code: " + projectInfo.getSubjectCode() + "\n" +
                "Description: " + projectInfo.getDescription() + "\n" +
                "Assessment duration: " + projectInfo.getDurationMin() + ":" + projectInfo.getDurationSec() + "\n" +
                "Warning time: " + projectInfo.getWarningMin() + ":" + projectInfo.getWarningSec() + "\n");
        textView_criteriaDetail = findViewById(R.id.criteria_detail__inpreparation);
        String criteriaDetailString = "Mark Section\n";
        for (Criteria c : projectInfo.getCriteria()) {
            criteriaDetailString = criteriaDetailString + c.getName() + "\n";
            criteriaDetailString = criteriaDetailString + "Maximum mark: " + c.getMaximunMark() + "\n";
            criteriaDetailString = criteriaDetailString + "Mark increments: " + c.getMarkIncrement() + "\n\n";
        }
        criteriaDetailString += "\nComments Only\n";
        for (Criteria c : projectInfo.getCommentList()) {
            criteriaDetailString = criteriaDetailString + c.getName() + "\n";
        }
        textView_criteriaDetail.setText(criteriaDetailString);
        textView_asseccorDetail = findViewById(R.id.asseccor_detail__inpreparation);
        String assessorDetailString = new String();
        for (int i = 0; i < projectInfo.getAssistant().size(); i++)
            assessorDetailString = assessorDetailString + projectInfo.getAssistant().get(i) + "\n";
        textView_asseccorDetail.setText(assessorDetailString);
        project = AllFunctions.getObject().getProjectList().get(index);
        Log.d("EEEE", "project's head marker: " + project.getUsername());
        Log.d("EEEE", "username: " + AllFunctions.getObject().getUsername());
        if(!project.getUsername().equals(AllFunctions.getObject().getMyEmail())) {
            button_about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Assessment_Preparation_Activity.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
            button_criteria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Assessment_Preparation_Activity.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
            button_assessor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Assessment_Preparation_Activity.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
            button_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(Assessment_Preparation_Activity.this,
                            "You have no right to edit.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            button_about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    about_AssessmentPreparation(view);
                }
            });
            button_criteria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    criteriaManagement_AssessmentPreparation(view);
                }
            });
            button_assessor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assessors_AssessmentPreparation(view);
                }
            });
            button_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    studentManagement_AssessmentPreparation(view);
                }
            });
        }
    }

    private void resetDetailView() {
        textView_projectName.setText("Please select or add a project");
        textView_aboutDetail.setText("Project details");
        textView_criteriaDetail.setText("Criteria details");
        textView_asseccorDetail.setText("Click the subtitle to manage markers");
        student_detail__inpreparation.setText("Click the subtitle to manage students");
        button_about.setEnabled(false);
        button_criteria.setEnabled(false);
        button_student.setEnabled(false);
        button_assessor.setEnabled(false);
    }

    //plus button click function
    public void plus_AssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_About.class);
        intent.putExtra("index", "-999");
//        startActivity(intent);
        startActivityForResult(intent, 2);
        Log.d("EEEE", "strat activity for result 2");
    }

    public void about_AssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_About.class);
        Log.d("EEEE", "old project index_to_send: " + index_to_send);
        intent.putExtra("index", String.valueOf(index_to_send));
        startActivityForResult(intent, 3);
        Log.d("EEEE", "strat activity for result 3");
    }

    public void studentManagement_AssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_Student_Group.class);
        intent.putExtra("index", String.valueOf(index_to_send));
        startActivity(intent);
    }

    public void criteriaManagement_AssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_CriteriaList.class);
        intent.putExtra("index", String.valueOf(index_to_send));
        startActivity(intent);
    }

    public void assessors_AssessmentPreparation(View view) {
        Intent intent = new Intent(this, Activity_Marker_Management.class);
        intent.putExtra("index", String.valueOf(index_to_send));
        startActivityForResult(intent, 1);

//        AllFunctions.getObject().setHandler(handler);
//        LayoutInflater layoutInflater = LayoutInflater.from(this);//获得layoutInflater对象
//        final View view2 = layoutInflater.from(this).inflate(R.layout.dialog_asseccors, null);//获得view对象
//
//        ListView listView_assessors = view2.findViewById(R.id.listView_assessors_dialogAssessor);
//        adapterForAssessors = new MyAdapterForAssessors(projectList.get(index_to_send).getAssistant(), this);
//        listView_assessors.setAdapter(adapterForAssessors);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        dialog = builder.create();
//        dialog.setCancelable(false);
//        dialog.setView(view2);
//        dialog.show();
//
//        editText_assessorName = view2.findViewById(R.id.editText_inviteAssessor_dialogAssessor);
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (editText_assessorName.getText().toString().equals(""))
//                    Toast.makeText(Assessment_Preparation_Activity.this,
//                            "The email of an invitee cannot be empty.", Toast.LENGTH_SHORT).show();
//                else {
//                    //projectList.get(index_to_send).getAssistant().add(editText_assessorName.getText().toString());
//                    allFunctions.inviteAssessor(projectList.get(index_to_send), editText_assessorName.getText().toString());
//                    // adapterForAssessors.notifyDataSetChanged();
//                }
//            }
//        });
//        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editText_assessorName.setText("");
//                // adapterForAssessors.notifyDataSetChanged();
//                showOtherInfo(index_to_send);
//                dialog.dismiss();
//            }
//        });
    }

    public class MyAdapterDefaultlistView extends BaseAdapter {

        private ArrayList<ProjectInfo> mProjectList;
        private Context mContext;

        public MyAdapterDefaultlistView(Context context, ArrayList<ProjectInfo> projectList) {
            this.mProjectList = projectList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mProjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_default, parent, false);
            TextView textView_listItem = convertView.findViewById(R.id.textView_defaultView);
            textView_listItem.setText(mProjectList.get(position).getProjectName());
            return convertView;
        }
    }


    public class MyAdapter_for_listView extends BaseAdapter {

        private ArrayList<ProjectInfo> mProjectList;
        private Context mContext;

        public MyAdapter_for_listView(ArrayList<ProjectInfo> projectList, Context context) {
            this.mProjectList = projectList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mProjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_projectlist_withdelete, parent, false);
            TextView textView_listItem = (TextView) convertView.findViewById(R.id.textView_inlistView);
            textView_listItem.setText(mProjectList.get(position).getProjectName());
            Button button = convertView.findViewById(R.id.Bt_delete_inlist);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myAdapter.notifyDataSetChanged();
                    allFunctions.deleteProject(position);
                }
            });
            return convertView;
        }
    }


    public class MyAdapterForAssessors extends BaseAdapter {
        private ArrayList<String> mAssessorList;
        private Context mContext;

        public MyAdapterForAssessors(ArrayList<String> assessorList, Context context) {
            this.mAssessorList = assessorList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mAssessorList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_markers, parent, false);
            TextView textView_listItem = convertView.findViewById(R.id.textView_markerName);
            textView_listItem.setText(mAssessorList.get(position));
            Button button = convertView.findViewById(R.id.button_deleteMarker);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    allFunctions.deleteAssessor(projectList.get(index_to_send), mAssessorList.get(position));
                    mAssessorList.remove(position);
                    adapterForAssessors.notifyDataSetChanged();
                }
            });
            if (position == 0) {
                button.setVisibility(View.INVISIBLE);
                button.setEnabled(false);
            }
            return convertView;
        }
    }

    public void syncProjectList(View view) {
        allFunctions.syncProjectList();
    }

    public void updateProjectList(){
        projectList = allFunctions.getProjectList();
        Log.d("EEEE", projectList.toString() + "wtf");

        MyAdapterDefaultlistView mSyncAdapter = new MyAdapterDefaultlistView
                (Assessment_Preparation_Activity.this, projectList);
        listView.setAdapter(mSyncAdapter);
        listView.setOnItemClickListener(this);

        Log.d("EEEE", this.getClass().getSimpleName());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                showOtherInfo(index_to_send);
            }
        } else if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d("EEEE", "new project in assessment preparation.");
                MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                        (Assessment_Preparation_Activity.this, AllFunctions.getObject().getProjectList());
                listView.setAdapter(myAdapterDefaultlistView);
                listView.setOnItemClickListener(this);
                if(!(index_to_send == -999)) {
                    showOtherInfo(index_to_send);
                } else {
                    resetDetailView();
                }
            }
        } else if(requestCode == 3) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d("EEEE", "edit about of old project.");
                MyAdapterDefaultlistView myAdapterDefaultlistView = new MyAdapterDefaultlistView
                        (Assessment_Preparation_Activity.this, AllFunctions.getObject().getProjectList());
                listView.setAdapter(myAdapterDefaultlistView);
                listView.setOnItemClickListener(this);
                resetDetailView();
            }
        }
    }
}
