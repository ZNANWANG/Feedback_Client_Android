package com.example.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dbclass.Mark;
import dbclass.ProjectInfo;
import dbclass.StudentInfo;
import main.AllFunctions;

public class Activity_SendReport_Individual extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfStudent;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_individual);

        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_send_report_individual);
        mToolbar.setTitle("Assessment");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_SendReport_Individual.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_SendReport_Individual.this,
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

    private void init() {
        Log.d("EEEE", "send report individually");
        ProjectInfo project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        StudentInfo student = AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentInfo().get(indexOfStudent);
        ArrayList<Mark> markList = AllFunctions.getObject().getMarkListForMarkPage();
        Button button_record = findViewById(R.id.button_record_individual);
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_SendReport_Individual.this, Activity_Record_Voice.class);
                intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                startActivity(intent);
            }
        });
        Button button_sendSingle = findViewById(R.id.button_sendStudent_sendReportIndividual);
        button_sendSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFunctions.getObject().sendPDF(project, student.getNumber(), 1);
                student.setSendEmail(true);
                Intent intent = new Intent(Activity_SendReport_Individual.this, Activity_Reaper_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        Button button_sendBoth = findViewById(R.id.button_sendBoth_sendReportIndividual);
        button_sendBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFunctions.getObject().sendPDF(project, student.getNumber(), 2);
                student.setSendEmail(true);
                Intent intent = new Intent(Activity_SendReport_Individual.this, Activity_Reaper_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        Button button_finish = findViewById(R.id.btn_finish_sendReportIndividual);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_SendReport_Individual.this, Activity_Reaper_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        TextView textView_totalMark = findViewById(R.id.textView_totalMark_sendReportIndividual);
        textView_totalMark.setText("Mark:" + (int) markList.get(0).getTotalMark() + "%");

        String htmlString =
                "<html>" +
                        "<body>" +
                        "<h1 style=\"font-weight: normal\">" + project.getProjectName() + "</h1>" +
                        "<hr>" +
                        "<p>" + student.getFirstName() + " " + student.getMiddleName() + " " + student.getSurname() + " --- " + student.getNumber() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getProjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark Attained</h2>" +
                        "<p>" + markList.get(0).getTotalMark() + "%</p >" +
                        "<h2 style=\"font-weight: normal\">Marker</h2>" + "<p>";
        for (int i = 0; i < project.getAssistant().size(); i++) {
            htmlString = htmlString + project.getAssistant().get(i) + "<br>";
        }
        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>";

        htmlString += "<h2 style=\"font-weight: normal\">MarkedCriteria</h2>" + "<p>";
        for (int i = 0; i < markList.get(0).getCriteriaList().size(); i++) {
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + markList.get(0).getCriteriaList().get(i).getName() + "</span>" +
                    "<span style=\"float:right\">" + "  ---  " + markList.get(0).getMarkList().get(i) + "/" + Double.valueOf(markList.get(0).getCriteriaList().get(i).getMaximunMark()) + "</span></h3>";
            for (int j = 0; j < markList.size(); j++) {
                htmlString += "<h4 style=\"font-weight: normal;color: #014085\">" + markList.get(j).getLecturerName() + ":</h4>";
                if (markList.get(j).getCriteriaList().size() > 0)
                    for (int k = 0; k < markList.get(j).getCriteriaList().get(i).getSubsectionList().size(); k++) {
                        htmlString += "<p>" + markList.get(j).getCriteriaList().get(i).getSubsectionList().get(k).getName() + " : "
                                + markList.get(j).getCriteriaList().get(i).getSubsectionList().get(k).getShortTextList().get(0).getLongtext() + "</p >";
                    }
            }
            htmlString += "<br>";
        }

        htmlString +=
                "</div>" +
                        "</body>" +
                        "</html>";
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_sendReportIndividual);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
    }
}
