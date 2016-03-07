package ua.moskovkin.autorecorder.preference;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ua.moskovkin.autorecorder.R;
import ua.moskovkin.autorecorder.utils.DBHelper;

public class ExcludedNumbersActivity extends AppCompatActivity{
    private static final int PICK_CONTACT_REQUEST = 1;
    private Toolbar toolbar;
    private EditText numberEditText;
    private Button addButton;
    private ListView listView;
    private ArrayList<String> numbers;
    private NumberAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excluded_numbers);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DBHelper(this);

        numbers = new ArrayList<>();
        numbers = dbHelper.getExcludedNumbers();

        numberEditText = (EditText) findViewById(R.id.add_excluded_number_edit_text);
        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!numberEditText.getText().toString().equals("")) {
                    adapter.add(numberEditText.getText().toString());
                    numberEditText.setText("");
                }
            }
        });

        listView = (ListView) findViewById(R.id.excluded_numbers_list_view);

        adapter = new NumberAdapter(numbers, this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exclude_number_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_excluded_ok: {
                finish();
                break;
            }
            case R.id.action_choose_contact: {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor c = getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            if (c != null) {
                c.moveToFirst();
                String num = c.getString(0);
                num = num.replace("+", "");
                adapter.add(num);
                c.close();
            }
        }
    }

    private class NumberAdapter extends BaseAdapter {
        private ArrayList<String> numbers;
        private LayoutInflater inflater;
        private Context context;

        public NumberAdapter(ArrayList<String> numbers, Context context) {
            this.numbers = numbers;
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void add(String number) {
            Set<String> set = new HashSet<>(numbers);
            if (set.add(number)) {
                numbers.add(number);
                dbHelper.insertExcludedNumber(number);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, context.getString(R.string.number_alredy_in_list), Toast.LENGTH_SHORT).show();
            }
        }

        public void remove(String number) {
            numbers.remove(number);
            dbHelper.removeExcludedNumber(number);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public Object getItem(int position) {
            return numbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.excluded_list_item, parent, false);
            }
            String excludedNumber = numbers.get(position);
            ((TextView) view.findViewById(R.id.excluded_text_view)).setText(excludedNumber);
            view.findViewById(R.id.delete_excluded_number_image_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(numbers.get(position));
                }
            });

            return view;
        }
    }
}
