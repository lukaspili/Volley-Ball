package com.siu.android.volleyball.samples.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.siu.android.volleyball.samples.R;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario10Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario1Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario2Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario3Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario4Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario5Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario6Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario7Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario8Activity;
import com.siu.android.volleyball.samples.activity.scenarios.Scenario9Activity;

/**
 * Created by lukas on 9/9/13.
 */
public class ScenariosActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scenarios_activity);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.scenarios)));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        startActivity(new Intent(ScenariosActivity.this, Scenario1Activity.class));
                        break;
                    case 1:
                        startActivity(new Intent(ScenariosActivity.this, Scenario2Activity.class));
                        break;
                    case 2:
                        startActivity(new Intent(ScenariosActivity.this, Scenario3Activity.class));
                        break;
                    case 3:
                        startActivity(new Intent(ScenariosActivity.this, Scenario4Activity.class));
                        break;
                    case 4:
                        startActivity(new Intent(ScenariosActivity.this, Scenario5Activity.class));
                        break;
                    case 5:
                        startActivity(new Intent(ScenariosActivity.this, Scenario6Activity.class));
                        break;
                    case 6:
                        startActivity(new Intent(ScenariosActivity.this, Scenario7Activity.class));
                        break;
                    case 7:
                        startActivity(new Intent(ScenariosActivity.this, Scenario8Activity.class));
                        break;
                    case 8:
                        startActivity(new Intent(ScenariosActivity.this, Scenario9Activity.class));
                        break;
                    case 9:
                        startActivity(new Intent(ScenariosActivity.this, Scenario10Activity.class));
                        break;

                }
            }
        });
    }

}
