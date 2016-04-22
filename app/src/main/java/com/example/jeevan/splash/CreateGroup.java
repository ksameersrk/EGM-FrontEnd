package com.example.jeevan.splash;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateGroup extends AppCompatActivity {

    private ListView listView;
    Bundle b = new Bundle();
    private ArrayAdapter<String> listAdapter;
    private String my_sel_items;
    private ArrayList<String> selected_contacts;
    private ArrayList<Integer> selectedIds;
    private ArrayList<String> contacts;

    private AutoCompleteTextView mDestination;
    private EditText mGroupName;

    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_GROUP_NAME = "GroupName";
    private static final String PREF_GROUP_DEST = "GroupDestination";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //set up form
        mGroupName = (EditText) findViewById(R.id.create_group_name);
        mDestination = (AutoCompleteTextView) findViewById(R.id.create_group_destination);
        mDestination.setAdapter(new AutoCompleteAdapter(this));
        contacts = getContactListFromPhone();
        display();

    }

    public void display() {
        listView = (ListView) findViewById(R.id.create_group_contacts_list);
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                contacts
        ); // sw32
        //set Adapter
        listView.setAdapter(listAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //set the listener for onclick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);

                my_sel_items = "";
                SparseBooleanArray a = listView.getCheckedItemPositions();
                selected_contacts = new ArrayList<String>();
                selectedIds = new ArrayList<Integer>();
                for (int j = 0; j < a.size(); j++) {
                    Log.v("value size : ", a.size() + "");
                    if (a.valueAt(j)) {
                        my_sel_items = (String) listView.getAdapter().getItem(a.keyAt(j));
                        //Toast.makeText(ListOfPlaces.this,my_sel_items,Toast.LENGTH_LONG).show();
                        selected_contacts.add(my_sel_items);
                        selectedIds.add(new Integer(a.keyAt(j)));
                    } else {
                        int tmp = a.keyAt(j);
                        if (selectedIds.contains(tmp)) {
                            selectedIds.remove(new Integer(tmp));
                        }
                    }
                }

                Log.v("First : ", listView.getFirstVisiblePosition() + "");
                Log.v("Last  : ", listView.getLastVisiblePosition() + "");
                Log.v("list", selected_contacts.toString());
                Log.v("list", selectedIds.toString());
            }
        });

    }

    private ArrayList<String> getContactListFromPhone()
    {
        ArrayList<String> allContacts = new ArrayList<String>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Toast.makeText(CreateGroup.this, "Name: " + name
                                //+ ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                        allContacts.add(name+" : "+phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        return allContacts;
    }

    //methods to check the fields
    public boolean noErrors()
    {
        // Reset errors.
        mDestination.setError(null);
        mGroupName.setError(null);

        String gName = mGroupName.getText().toString();
        String gDestination = mDestination.getText().toString();

        boolean allCorrect = true;
        View focusView = null;

        if (TextUtils.isEmpty(gName)) {
            mGroupName.setError("This field cannot be empty!");
            focusView = mGroupName;
            allCorrect = false;
        } else if (TextUtils.isEmpty(gDestination)) {
            mDestination.setError("This field cannot be empty!");
            focusView = mDestination;
            allCorrect = false;
        }

        return allCorrect;
    }

    //methods for onclick
    public void createGroupNow(View v)
    {
        if(noErrors()){
            //TODO: replace MainActivity.class to GroupTrip.class


            String gName = mGroupName.getText().toString();
            String gDest = mDestination.getText().toString();
            Intent i = new Intent(CreateGroup.this, GroupTripMap.class);
            getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_GROUP_NAME, gName)
                    .putString(PREF_GROUP_DEST, gDest)
                    .commit();
            createJson(gName, gDest);
            finish();
            startActivity(i);
        }

    }

    public void createJson(String gName, String gDest)
    {
        ArrayList<String> members = new ArrayList<String>();
        for(String k : selected_contacts)
        {
            members.add(k.split(":")[1].trim());
        }
        try{
            JSONObject obj = new JSONObject();
            obj.put("gname", gName);
            obj.put("gdest", gDest);
            obj.put("members", members.toString());
        }
        catch (Exception e)
        {
            Log.i("error", "createGroup");
        }

    }
    private class AutoCompleteAdapter extends ArrayAdapter<Address> implements Filterable {

        private LayoutInflater mInflater;
        private Geocoder mGeocoder;
        private StringBuilder mSb = new StringBuilder();

        public AutoCompleteAdapter(final Context context) {
            super(context, -1);
            mInflater = LayoutInflater.from(context);
            mGeocoder = new Geocoder(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final TextView tv;
            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            tv.setText(createFormattedAddressFromAddress(getItem(position)));
            return tv;
        }

        private String createFormattedAddressFromAddress(final Address address) {
            mSb.setLength(0);
            final int addressLineSize = address.getMaxAddressLineIndex();
            for (int i = 0; i < addressLineSize; i++) {
                mSb.append(address.getAddressLine(i));
                if (i != addressLineSize - 1) {
                    mSb.append(", ");
                }
            }
            return mSb.toString();
        }
        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    List<Address> addressList = null;
                    if (constraint != null) {
                        try {
                            addressList = mGeocoder.getFromLocationName((String) constraint, 5);
                        } catch (IOException e) {
                        }
                    }
                    if (addressList == null) {
                        addressList = new ArrayList<Address>();
                    }

                    final FilterResults filterResults = new FilterResults();
                    filterResults.values = addressList;
                    filterResults.count = addressList.size();

                    return filterResults;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(final CharSequence contraint, final Filter.FilterResults results) {
                    clear();
                    for (Address address : (List<Address>) results.values) {
                        add(address);
                    }
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    return resultValue == null ? "" : ((Address) resultValue).getAddressLine(0);
                }
            };
            return myFilter;
        }

    }

}
