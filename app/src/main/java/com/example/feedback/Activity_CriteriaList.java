package com.example.feedback;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Activity_CriteriaList extends AppCompatActivity {

    private ProjectInfo project;
    private int indexOfProject;
    private String path;
    private ArrayList<Criteria> defaultCriteriaList;
    private ListView listView_criteriaDefault;
    private ListView listView_marketCriteria;
    private Handler handler;
    private Toolbar mToolbar;
    private AlertDialog dialog;
    private MyAdapter_criteriaListDefault myAdapter1;
    private MyAdapter_criteriaListDefault myAdapter2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE",
    "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__criteria_list);
        Log.d("EEEE", "criteriaList interface onCreate");
        verifyStoragePermissions(Activity_CriteriaList.this);
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("index"));
        init();
    }

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.READ_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_project_criteria);
        mToolbar.setTitle(project.getProjectName());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                discardWarning();
            }
        });
        mToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_CriteriaList.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_CriteriaList.this,
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

    protected void onNewIntent(Intent intent) {
        Log.d("EEEE", "criteriaList interface onNewIntent");
        Intent intent2 = getIntent();
        indexOfProject = Integer.parseInt(intent2.getStringExtra("index"));
        init();
    }

    private void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Intent intent = new Intent(Activity_CriteriaList.this,
                                Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    case 210:
                        Toast.makeText(Activity_CriteriaList.this,
                                "Sync success.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        defaultCriteriaList = DefaultCriteriaList.getDefaultCriteriaList();
        defaultCriteriaList.removeAll(project.getCriteria());
        defaultCriteriaList.removeAll(project.getCommentList());
        listView_criteriaDefault = findViewById(R.id.listView_CriteriaList_inCriteriaList);
        listView_marketCriteria = findViewById(R.id.listView_markingCriteria_inCriteriaList);

        myAdapter1 = new MyAdapter_criteriaListDefault(defaultCriteriaList, this);
        myAdapter2 = new MyAdapter_criteriaListDefault(project.getCriteria(), this);

        listView_criteriaDefault.setAdapter(myAdapter1);
        listView_marketCriteria.setAdapter(myAdapter2);

        listView_criteriaDefault.setOnDragListener(dragListenerForDefaultListview);
        listView_marketCriteria.setOnDragListener(dragListenerForMarkingCriteriaList);

        initToolbar();
    }

    //button next.
    public void next_inCriteriaList(View view) {
        Log.d("EEEE", project.getCriteria() + "");
        if(project.getCriteria().size() == 0){
            Log.d("EEEE", "empty criteria list.");
            Toast.makeText(Activity_CriteriaList.this, "Marking criteria cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, Activity_MarkAllocation.class);
            intent.putExtra("index", String.valueOf(indexOfProject));
            startActivityForResult(intent, 1);
            Log.d("EEEE", "Go to mark allocation.");
        }
    }


    public void addMarkedCriteria(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);//获得layoutInflater对象
        final View view2 = layoutInflater.from(this).inflate(R.layout.dialog_add_criteria, null);//获得view对象

        Dialog dialog = new android.app.AlertDialog.Builder(this).setView(view2).
                setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText_newCriteriaName = view2.findViewById(R.id.editText_criteriaName_dialogAddCriteria);//获取控件
                        String newCriteriaName = editText_newCriteriaName.getText().toString();

                        if (findWhichCriteriaList_itbelongs(newCriteriaName) == -999) {
                            Criteria criteria = new Criteria();
                            criteria.setName(newCriteriaName);
                            project.getCriteria().add(criteria);
                            init();
                        } else {
                            Toast.makeText(Activity_CriteriaList.this, "Criteria " + newCriteriaName + " has been existed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).create();
        dialog.show();
    }

//    public void addCommentCriteria(View view) {
//        LayoutInflater layoutInflater = LayoutInflater.from(this);//获得layoutInflater对象
//        final View view2 = layoutInflater.from(this).inflate(R.layout.dialog_add_criteria, null);//获得view对象
//
//        Dialog dialog = new android.app.AlertDialog.Builder(this).setView(view2).setPositiveButton("Done", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                EditText editText_newCriteriaName = view2.findViewById(R.id.editText_criteriaName_dialogAddCriteria);//获取控件
//                String newCriteriaName = editText_newCriteriaName.getText().toString();
//
//                if (findWhichCriteriaList_itbelongs(newCriteriaName) == -999) {
//                    Criteria criteria = new Criteria();
//                    criteria.setName(newCriteriaName);
//                    project.getCommentList().add(criteria);
//                    init();
//                } else {
//                    Toast.makeText(Activity_CriteriaList.this, "Criteria " + newCriteriaName + " has been existed.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // TODO Auto-generated method stub
//            }
//        }).create();
//        dialog.show();
//    }

    public class MyAdapter_criteriaListDefault extends BaseAdapter {
        private Context mContext;
        private ArrayList<Criteria> criteriaList;

        public MyAdapter_criteriaListDefault(ArrayList<Criteria> criteriaListList, Context context) {
            this.criteriaList = criteriaListList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return criteriaList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_criterialist_default, parent, false);
            TextView textView_criteriaName = convertView.findViewById(R.id.textView_criterialistDefault_inCriteriaList);
            textView_criteriaName.setText(criteriaList.get(position).getName());

            final View dragView = convertView;
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // DND框架要求传递的数据
                    ClipData.Item item1 = new ClipData.Item(criteriaList.get(position).getName());
                    ClipData.Item item2 = new ClipData.Item(String.valueOf(position));

                    ClipData clipData = new ClipData("", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item1);
                    clipData.addItem(item2);

                    // 开始当前View的拖动操作，将当前拖动对象的position当作localState传递到拖动事件中
                    dragView.startDrag(clipData, new View.DragShadowBuilder(dragView), null, 0);
                    return true;
                }
            });
            return convertView;
        }
    }


    private View.OnDragListener dragListenerForDefaultListview = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            Log.d("EEEE", "default listView ondrag listener start");
            // Handles each of the expected events
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    // Gets the item containing the dragged data
                    ClipData.Item item1 = event.getClipData().getItemAt(0);
                    ClipData.Item item2 = event.getClipData().getItemAt(1);
                    String source_criteriaName = item1.getText().toString();
                    int source_criteriaIndex = Integer.parseInt(item2.getText().toString());
                    int whichList = findWhichCriteriaList_itbelongs(source_criteriaName);
                    switch (whichList) {
                        case 0:
                            break;
                        case 1:
                            Log.d("EEEE", "1->0");
                            Criteria criteria_Temporary = project.getCriteria().get(source_criteriaIndex);
                            project.getCriteria().remove(source_criteriaIndex);
                            defaultCriteriaList.add(criteria_Temporary);
                            break;
                        case 2:
                            Log.d("EEEE", "2->0");
                            Criteria criteria_Temporary2 = project.getCommentList().get(source_criteriaIndex);
                            project.getCommentList().remove(source_criteriaIndex);
                            defaultCriteriaList.add(criteria_Temporary2);
                            break;
                        default:
                            break;
                    }
                    init();
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // Returns true. DragEvent.getResult() will return true.
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Turns off any color tinting
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // returns true; the value is ignored.
                    return true;
                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }
    };

    private View.OnDragListener dragListenerForMarkingCriteriaList = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            // Handles each of the expected events
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.parseColor("#dbdbdb"));
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    // Gets the item containing the dragged data
                    ClipData.Item item1 = event.getClipData().getItemAt(0);
                    ClipData.Item item2 = event.getClipData().getItemAt(1);
                    String source_criteriaName = item1.getText().toString();
                    int source_criteriaIndex = Integer.parseInt(item2.getText().toString());
                    int whichList = findWhichCriteriaList_itbelongs(source_criteriaName);
                    switch (whichList) {
                        case 0:
                            Log.d("EEEE", "0->1");
                            Criteria criteria_Temporary = defaultCriteriaList.get(source_criteriaIndex);
                            defaultCriteriaList.remove(source_criteriaIndex);
                            project.getCriteria().add(criteria_Temporary);
                            break;
                        case 1:
                            break;
                        case 2:
                            Log.d("EEEE", "2->1");
                            Criteria criteria_Temporary2 = project.getCommentList().get(source_criteriaIndex);
                            project.getCommentList().remove(source_criteriaIndex);
                            project.getCriteria().add(criteria_Temporary2);
                            break;
                        default:
                            break;
                    }
                    init();
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // Returns true. DragEvent.getResult() will return true.
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Turns off any color tinting
                    // Invalidates the view to force a redraw
                    v.invalidate();
                    // returns true; the value is ignored.
                    return true;
                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }
    };

//    private View.OnDragListener dragListenerForCommentOnlyCriteria = new View.OnDragListener() {
//
//        @Override
//        public boolean onDrag(View v, DragEvent event) {
//
//            // Defines a variable to store the action type for the incoming event
//            final int action = event.getAction();
//
//            // Handles each of the expected events
//            switch (action) {
//
//                case DragEvent.ACTION_DRAG_STARTED:
//                    // Determines if this View can accept the dragged data
//                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
//                        v.invalidate();
//                        return true;
//                    }
//                    return false;
//                case DragEvent.ACTION_DRAG_ENTERED:
//
//                    v.setBackgroundColor(Color.parseColor("#dbdbdb"));
//                    v.invalidate();
//                    return true;
//
//                case DragEvent.ACTION_DRAG_LOCATION:
//                    return true;
//
//                case DragEvent.ACTION_DRAG_EXITED:
//                    v.setBackgroundColor(Color.TRANSPARENT);
//                    v.invalidate();
//                    return true;
//
//                case DragEvent.ACTION_DROP:
//
//                    v.setBackgroundColor(Color.TRANSPARENT);
//                    // Gets the item containing the dragged data
//                    ClipData.Item item1 = event.getClipData().getItemAt(0);
//                    ClipData.Item item2 = event.getClipData().getItemAt(1);
//                    String source_criteriaName = item1.getText().toString();
//                    int source_criteriaIndex = Integer.parseInt(item2.getText().toString());
//
//
//                    int whichList = findWhichCriteriaList_itbelongs(source_criteriaName);
//                    switch (whichList) {
//                        case 0:
//                            Criteria criteria_Temporary = defaultCriteriaList.get(source_criteriaIndex);
//                            defaultCriteriaList.remove(source_criteriaIndex);
//                            project.getCommentList().add(criteria_Temporary);
//                            break;
//                        case 1:
//                            Criteria criteria_Temporary2 = project.getCriteria().get(source_criteriaIndex);
//                            project.getCriteria().remove(source_criteriaIndex);
//                            project.getCommentList().add(criteria_Temporary2);
//                            break;
//                        case 2:
//                            break;
//                        default:
//                            ;
//                    }
//
//                    init();
//
//                    // Invalidates the view to force a redraw
//                    v.invalidate();
//
//                    // Returns true. DragEvent.getResult() will return true.
//                    return true;
//
//                case DragEvent.ACTION_DRAG_ENDED:
//
//                    // Turns off any color tinting
//                    // Invalidates the view to force a redraw
//                    v.invalidate();
//
//                    // returns true; the value is ignored.
//                    return true;
//                // An unknown action type was received.
//                default:
//                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
//                    break;
//            }
//            return false;
//        }
//    };


    //0 means defaultCriteriaList, 1 means marking criteriaList, 2 means commentOnly criteriaList
    private int findWhichCriteriaList_itbelongs(String criteriaName) {
        for (Criteria c : defaultCriteriaList) {
            if (c.getName().equals(criteriaName))
                return 0;
        }
        for (Criteria c : project.getCriteria()) {
            if (c.getName().equals(criteriaName))
                return 1;
        }
        for (Criteria c : project.getCommentList()) {
            if (c.getName().equals(criteriaName))
                return 2;
        }
        return -999;
    }

    public void discardWarning() {
        AllFunctions.getObject().setHandler(handler); // attention!!!!!!!

        LayoutInflater layoutInflater = LayoutInflater.from(Activity_CriteriaList.this);//获得layoutInflater对象
        final View view = layoutInflater.from(Activity_CriteriaList.this).
                inflate(R.layout.dialog_quit_editing, null);//获得view对象
        TextView warning = view.findViewById(R.id.textView_dialog_query_waring_editing);
        warning.setText("Are you sure you want to discard all changes ?");
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CriteriaList.this);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllFunctions.getObject().syncProjectList();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private static final int FILE_SELECT_CODE = 0;

    public void uploadCriteria(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("EEEE", "File Uri: " + uri.toString());
                    // Get the path

                    path = FileUtils.getPath(this, uri);
                    ArrayList<Criteria> uploadCriteriaList = new ArrayList<>();
                    uploadCriteriaList = AllFunctions.getObject().readCriteriaExcel(project, path);
                    Log.d("EEEE", "call the readCriteriaExcel method: " + path);
                    defaultCriteriaList.addAll(uploadCriteriaList);
                    myAdapter1.notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        discardWarning();
    }
}
