package com.example.jeevan.splash;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MembersActivity extends AppCompatActivity {

    private ListView listView;
    ArrayList<String> contacts;
    private TextView mGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

        mGroupName = (TextView)findViewById(R.id.member_group_name);
        mGroupName.setText(getGroupName());

        contacts = getMembers();
        display();
    }

    public void display() {
        listView = (ListView) findViewById(R.id.member_list_view);
        listView.setAdapter(new MemberCustomizedAdapter(this, contacts));
    }

    public String getGroupName()
    {
        //// TODO: 10/4/16 get group name from database
        return "MY_GROUP";
    }

    public ArrayList<String> getMembers()
    {
        // TODO: 10/4/16 get messages from database for this group
        String msg[] = {
                "Name1 : Phonenumber1 : Location 1",
                "Name2 : Phonenumber2 : Location 2",
                "Name3 : Phonenumber3 : Location 3",
                "Name4 : Phonenumber4 : Location 4",
                "Name5 : Phonenumber5 : Location 5",
                "Name6 : Phonenumber6 : Location 6",
                "Name7 : Phonenumber7 : Location 7",
                "Name8 : Phonenumber8 : Location 8",
                "Name9 : Phonenumber9 : Location 9",
                "Name10 : Phonenumber10 : Location 10",
                "Name11 : Phonenumber11 : Location 11",
                "Name12 : Phonenumber12 : Location 12",
        };
        return new ArrayList<String>(Arrays.asList(msg));
    }
}

class MemberCustomizedAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> data;
    private static LayoutInflater inflater = null;
    private HashMap<String, String> hm = new HashMap<>();

    public MemberCustomizedAdapter(Context context, ArrayList<String> data) {
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
            vi = inflater.inflate(R.layout.members_list_row, null);

        TextView name = (TextView) vi.findViewById(R.id.member_name);
        TextView phone_number = (TextView) vi.findViewById(R.id.member_phone_number);
        TextView location = (TextView) vi.findViewById(R.id.member_location);
        String details[] = data.get(position).trim().split(":");
        String User_name = details[0].trim();
        String user_phone_number = details[1].trim();
        String user_location = details[2].trim();

        name.setText(User_name);
        phone_number.setText(user_phone_number);
        location.setText(user_location);

        //set random color
        int r = 0;
        int g = 0;
        int b = 0;
        if(hm.containsKey(user_phone_number))
        {
            String color[] = hm.get(user_phone_number).split(":");
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
            hm.put(user_phone_number, r+":"+g+":"+b);
        }
        name.setTextColor(Color.rgb(r, g, b));
        return vi;
    }
}
