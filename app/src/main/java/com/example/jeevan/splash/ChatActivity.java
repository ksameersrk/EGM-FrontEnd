package com.example.jeevan.splash;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    ArrayList<String> messages;
    private TextView mGroupName;
    private EditText mMsgText;
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_USERNAME = "username";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //set up chat window
        mGroupName = (TextView) findViewById(R.id.chat_group_name);
        mGroupName.setText(getGroupName());
        mMsgText = (EditText) findViewById(R.id.chat_edit_text);


        try{
            String phoneNumber = getUserName();
            JSONObject obj = new JSONObject();
            obj.put("phone_number", phoneNumber);
        }
        catch (Exception e)
        {
            Log.i("error", "get list of messages");
        }

        //get messages
        messages = getMessages();
        //messages = formatMessages(messages);
        display();
    }

    public void display() {
        listView = (ListView) findViewById(R.id.chat_list_view);
        listView.setAdapter(new CustomizedAdapter(this, messages));
        updateFocus();
    }

    public void updateFocus()
    {
        listView.setSelection(listView.getAdapter().getCount()-1);
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
        return getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_USERNAME, null);
    }

    //add user text
    public void addMessage(View v)
    {
        if(noErrors())
        {
            messages.add("- : " + getUserName() + " : " + mMsgText.getText().toString());
            mMsgText.setText("");
            updateFocus();
            try{
                String phoneNumber = getUserName();
                JSONObject obj = new JSONObject();
                obj.put("phone_number", phoneNumber);
                obj.put("message_text", mMsgText.getText().toString());
            }
            catch (Exception e)
            {
                Log.i("error", "get list of messages");
            }
        }
    }

    //methods to check the fields
    public boolean noErrors()
    {
        // Reset errors.
        mMsgText.setError(null);

        String msgText = mMsgText.getText().toString();

        boolean allCorrect = true;
        View focusView = null;

        if (TextUtils.isEmpty(msgText)) {
            mMsgText.setError("Please Enter some message");
            focusView = mGroupName;
            allCorrect = false;
        }

        return allCorrect;
    }
}


class CustomizedAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> data;
    private static LayoutInflater inflater = null;
    private HashMap<String, String> hm = new HashMap<>();

    public CustomizedAdapter(Context context, ArrayList<String> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.chat_list_row, null);

        TextView name = (TextView) vi.findViewById(R.id.chat_person_name);
        TextView text = (TextView) vi.findViewById(R.id.chat_person_text);
        String details[] = data.get(position).trim().split(":");
        Boolean currentUser = details[0].trim().equals("-");
        Log.e("cuur", currentUser.toString());
        String user_name = details[1].trim();
        String user_text = details[2].trim();

        name.setText(user_name);
        text.setText(user_text);

        //set random color
        int r = 0;
        int g = 0;
        int b = 0;
        if(hm.containsKey(user_name))
        {
            String color[] = hm.get(user_name).split(":");
            r = Integer.parseInt(color[0]);
            g = Integer.parseInt(color[1]);
            b = Integer.parseInt(color[2]);
        }
        else
        {
            Random rand = new Random();
            r = rand.nextInt();
            g = rand.nextInt();
            b = rand.nextInt();
            hm.put(user_name, r+":"+g+":"+b);
        }
        name.setTextColor(Color.rgb(r, g, b));

        if(currentUser)
        {
            name.setGravity(Gravity.RIGHT);
            text.setGravity(Gravity.RIGHT);
        }
        else
        {
            name.setGravity(Gravity.START);
            text.setGravity(Gravity.START);
        }

        return vi;
    }
}




