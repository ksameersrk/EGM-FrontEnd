package com.example.jeevan.splash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    ArrayList<String> messages;
    private TextView mGroupName;
    private EditText mMsgText;

    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_GROUP_NAME = "GroupName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //set up chat window
        mGroupName = (TextView) findViewById(R.id.chat_group_name);
        mGroupName.setText(getGroupName());
        mMsgText = (EditText) findViewById(R.id.chat_edit_text);

        //get messages
        messages = getMessages();
        messages = formatMessages(messages);
        display();
    }

    public void display() {
        listView = (ListView) findViewById(R.id.chat_list_view);
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_expandable_list_item_1,
                messages
        );

        //set Adapter
        listView.setAdapter(listAdapter);

        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(listAdapter.getCount() - 1);
            }
        });
    }

    public String getGroupName()
    {
        //// TODO: 10/4/16 get group name from database
        return "MY_GROUP";
    }

    public ArrayList<String> getMessages()
    {
        // TODO: 10/4/16 get messages from database for this group
        String msg[] = {
                "+ : User_name2 : Message 1",
                "- : User_name1 : Message 2",
                "+ : User_name3 : Message 3",
                "+ : User_name2 : Message 4",
                "- : User_name1 : Message 5",
                "+ : User_name4 : Message 6",
                "+ : User_name2 : Message 7",
                "- : User_name1 : Message 8",
                "+ : User_name3 : Message 9",
                "+ : User_name2 : Message 10",
                "- : User_name1 : Message 11",
                "+ : User_name4 : Message 12",
                "+ : User_name2 : Message 13",
                "- : User_name1 : Message 14",
                "+ : User_name3 : Message 15",
                "+ : User_name2 : Message 16",
                "- : User_name1 : Message 17",
                "+ : User_name4 : Message 18",
        };
        return new ArrayList<String>(Arrays.asList(msg));
    }

    public String getUserName()
    {
        return "User_name1";
    }

    public ArrayList<String> formatMessages(ArrayList<String> msg)
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for(String str : msg)
        {
            String details[] = str.split(":");
            tmp.add(details[1].trim()+"\n"+details[2]);
        }
        return tmp;
    }

    //add user text
    public void addMessage(View v)
    {
        messages.add(getUserName()+"\n"+mMsgText.getText().toString());
        mMsgText.setText("");
        View focusView = (ListView)findViewById(R.id.chat_list_view);
        focusView.requestFocus();
    }
}




