package com.example.feedback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Activity_Student_Group extends AppCompatActivity {

    private MyAdapter myAdapter;
    private ArrayList<StudentInfo> students;
    private ListView listView;
    private int indexOfStudent = -999;
    private int indexOfProject;
    private ProjectInfo project;
    private String path;
    private String studentID;
    private String firstName;
    private String middleName;
    private String surname;
    private String email;
    private String groupNumber;
    private Toolbar mToolbar;
    private AlertDialog dialog;
    private EditText editTextStudentID;
    private EditText editTextGivenname;
    private EditText editTextMiddleName;
    private EditText editTextFamilyname;
    private EditText editTextEmail;
    private EditText editTextGroup;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__student__group);

        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("index"));

        init();
        initToolbar();
    }

    public void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 221:
                        for (int i = 0; i < students.size(); i++) {
                            if (students.get(i).getNumber().equals(studentID)) {
                                if (!groupNumber.equals("")) {
                                    students.get(i).setGroup(Integer.parseInt(groupNumber));
                                    AllFunctions.getObject().groupStudent(project, studentID, Integer.parseInt(groupNumber));
                                }
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Successfully add a student.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Log.d("EEEE", "add student");
                        myAdapter.notifyDataSetChanged();
                        Collections.sort(project.getStudentInfo(), new SortByGroup());
                        break;
                    case 222:
                        Toast.makeText(getApplicationContext(), "Fail to add the student. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 223:
                        if (!groupNumber.equals("")) {
                            AllFunctions.getObject().groupStudent(project, studentID, Integer.parseInt(groupNumber));
                            students.get(indexOfStudent).setGroup(Integer.parseInt(groupNumber));
                        }
                        Toast.makeText(getApplicationContext(), "Successfully edit the info of a student.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Log.d("EEEE", "edit student");
                        myAdapter.notifyDataSetChanged();
                        Collections.sort(project.getStudentInfo(), new SortByGroup());
                        break;
                    case 224:
                        Toast.makeText(getApplicationContext(), "Fail to edit the student. Please try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 225:
                        Toast.makeText(getApplicationContext(), "Successfully upload the student list.", Toast.LENGTH_SHORT).show();
                        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
                        students = project.getStudentInfo();
                        myAdapter.notifyDataSetChanged();
                        break;
                    case 226:
                        Toast.makeText(getApplicationContext(), "One or more students already exist. Please check and try again.", Toast.LENGTH_SHORT).show();
                        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
                        students = project.getStudentInfo();
                        myAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        listView = findViewById(R.id.listView_ingroupStudent);
        students = project.getStudentInfo();
        myAdapter = new MyAdapter(students, this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_studetn_group);
        mToolbar.setTitle(project.getProjectName());
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
                        Toast.makeText(Activity_Student_Group.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Student_Group.this,
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

    //button delete click.
    public void deleteStudent(View view) {
        if (listView.getCheckedItemCount() == 1) {
            SparseBooleanArray checkedItemsStudents = listView.getCheckedItemPositions();
            int studentIndex = -1;
            if (checkedItemsStudents != null) {
                for (int i = 0; i < project.getStudentInfo().size(); i++) {
                    if (checkedItemsStudents.get(i) == true) {
                        studentIndex = i;
                        break;
                    }
                }
                AllFunctions.getObject().deleteStudent(project, students.get(studentIndex).getNumber());
                students.remove(studentIndex);
                init();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Please choose only 1 student to delete.", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<StudentInfo> studentList;

        public MyAdapter(ArrayList<StudentInfo> studentList, Context context) {
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_student_group, parent, false);
            TextView textView_groupNum = convertView.findViewById(R.id.textView_groupnum_instudentlist);
            if (studentList.get(position).getGroup() == -999) {
                textView_groupNum.setText("");
            } else {
                textView_groupNum.setText(String.valueOf(studentList.get(position).getGroup()));
            }

            TextView textView_studentID = convertView.findViewById(R.id.textView_studentID_instudentlist);
            textView_studentID.setText(studentList.get(position).getNumber());
            TextView textView_studentName = convertView.findViewById(R.id.textView_fullname_instudentlist);
            textView_studentName.setText(studentList.get(position).getFirstName() + " " + studentList.get(position).getMiddleName() + " " + studentList.get(position).getSurname());
            TextView textView_studentEmail = convertView.findViewById(R.id.textView_email_instudentlist);
            textView_studentEmail.setText(studentList.get(position).getEmail());

            if (listView.isItemChecked(position)) {
                convertView.setBackgroundColor(Color.parseColor("#D2EBF7"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }
    }

    private static final int FILE_SELECT_CODE = 0;

    public void import_StudentManagement(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
        }

    }

    private static final String TAG = "ChooseFile";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    path = FileUtils.getPath(this, uri);
                    AllFunctions.getObject().readStudentsExcel(project, path);
                    System.out.println("call the readExcel method: " + path);
                    init();
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //button addStudent click.
    public void addStudent(View v) {

        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Student_Group.this);//获得layoutInflater对象
        final View view = layoutInflater.from(Activity_Student_Group.this).inflate(R.layout.dialog_add_student, null);//获得view对象

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Student_Group.this);
        builder.setView(view);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setTitle("Add Student");
        dialog.show();

        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        students = project.getStudentInfo();
        editTextStudentID = view.findViewById(R.id.editText_studentID_addStudent);
        editTextGivenname = view.findViewById(R.id.editText_firstName_addStudent);
        editTextMiddleName = view.findViewById(R.id.editText_middleName_addStudent);
        editTextFamilyname = view.findViewById(R.id.editText_surname_addStudent);
        editTextEmail = view.findViewById(R.id.editText_email_addStudent);
        editTextGroup = view.findViewById(R.id.editText_group_addStudent);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                studentID = editTextStudentID.getText().toString().trim();
                firstName = editTextGivenname.getText().toString().trim();
                middleName = editTextMiddleName.getText().toString().trim();
                surname = editTextFamilyname.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                groupNumber = editTextGroup.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";

                if (studentID.equals("")) {
                    Toast.makeText(getApplicationContext(), "Student ID cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (firstName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Given name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (surname.equals("")) {
                    Toast.makeText(getApplicationContext(), "Family name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(getApplicationContext(), "Please input a valid Email", Toast.LENGTH_SHORT).show();
                } else {
                    if (AllFunctions.getObject().searchStudent(project, studentID) == -999) {
                        AllFunctions.getObject().addStudent(project, studentID, firstName, middleName, surname, email);
                    } else {
                        Toast.makeText(getApplicationContext(), "student with ID:" + studentID + " is already exits.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void editStudent_inStudentManagement(View v) {

        if (listView.getCheckedItemCount() == 1) {
            SparseBooleanArray checkedItemsStudents = listView.getCheckedItemPositions();
            if (checkedItemsStudents != null) {
                for (int i = 0; i < project.getStudentInfo().size(); i++) {
                    if (checkedItemsStudents.get(i) == true) {
                        indexOfStudent = i;
                        break;
                    }
                }

                LayoutInflater layoutInflater = LayoutInflater.from(Activity_Student_Group.this);
                View view = layoutInflater.from(Activity_Student_Group.this).inflate(R.layout.dialog_add_student, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Student_Group.this);
                builder.setView(view);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setTitle("Edit Student");
                dialog.show();

                project = AllFunctions.getObject().getProjectList().get(indexOfProject);
                students = project.getStudentInfo();
                editTextStudentID = view.findViewById(R.id.editText_studentID_addStudent);
                editTextStudentID.setEnabled(false);
                editTextStudentID.setText(students.get(indexOfStudent).getNumber());
                editTextGivenname = view.findViewById(R.id.editText_firstName_addStudent);
                editTextGivenname.setText(students.get(indexOfStudent).getFirstName());
                editTextMiddleName = view.findViewById(R.id.editText_middleName_addStudent);
                editTextMiddleName.setText(students.get(indexOfStudent).getMiddleName());
                editTextFamilyname = view.findViewById(R.id.editText_surname_addStudent);
                editTextFamilyname.setText(students.get(indexOfStudent).getSurname());
                editTextEmail = view.findViewById(R.id.editText_email_addStudent);
                editTextEmail.setText(students.get(indexOfStudent).getEmail());
                editTextGroup = view.findViewById(R.id.editText_group_addStudent);
                if (students.get(indexOfStudent).getGroup() == -999) {
                    editTextGroup.setText("");
                } else {
                    editTextGroup.setText(students.get(indexOfStudent).getGroup() + "");
                }

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        studentID = editTextStudentID.getText().toString().trim();
                        firstName = editTextGivenname.getText().toString().trim();
                        middleName = editTextMiddleName.getText().toString().trim();
                        surname = editTextFamilyname.getText().toString().trim();
                        email = editTextEmail.getText().toString().trim();
                        groupNumber = editTextGroup.getText().toString().trim();

                        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";

                        if (studentID.equals("")) {
                            Toast.makeText(getApplicationContext(), "StudentID cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (firstName.equals("")) {
                            Toast.makeText(getApplicationContext(), "FirstName cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (surname.equals("")) {
                            Toast.makeText(getApplicationContext(), "LastName cannot be empty", Toast.LENGTH_SHORT).show();
                        } else if (!email.matches(emailPattern)) {
                            Toast.makeText(getApplicationContext(), "Please input a valid Email", Toast.LENGTH_SHORT).show();
                        } else {
                            AllFunctions.getObject().editStudent(project, studentID, firstName, middleName, surname, email);
                            students.get(indexOfStudent).setStudentInfo(studentID, firstName, middleName, surname, email);
                        }
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please choose only 1 student to edit.", Toast.LENGTH_SHORT).show();
        }
    }

    public class SortByGroup implements Comparator {

        public int compare(Object o1, Object o2) {
            StudentInfo s1 = (StudentInfo) o1;
            StudentInfo s2 = (StudentInfo) o2;
            if (s1.getGroup() > s2.getGroup() && s2.getGroup() == -999) {
                return -1;
            } else if (s1.getGroup() < s2.getGroup() && s1.getGroup() == -999) {
                return 1;
            } else if (s1.getGroup() > s2.getGroup()) {
                return 1;
            } else if (s1.getGroup() == s2.getGroup()) {
                return 1;
            } else return -1;
        }

    }
}