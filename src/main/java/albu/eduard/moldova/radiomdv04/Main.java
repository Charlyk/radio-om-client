package albu.eduard.moldova.radiomdv04;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class Main extends ActionBarActivity implements Toolbar.OnMenuItemClickListener {

    private SQLiteDatabase sqLiteDatabase;
    private DBHelper dbHelper;
    private Button mPlayStopBtn;
    private Intent playIntent;
    private Boolean isPlaying = false;
    private int textID;
    private static final String STATE_PL_BTN = "btn";
    private static final String RESOURCE = "resouce";
    private String name;
    private String url;
    private int resID;
    private Bitmap bm;
    private NotificationManager nm;
    private static final int notificationId = 2415;
    private SharedPreferences preferences;
    private static final String CHANEL_NAME = "name";
    private static final String CHANEL_URL = "url";
    private static final String CHANEL_ICON = "bitmap";
    private static final String ICON_PATH = "path";
    private String path;
    private String tempName = null;
    private ListView mChanelListView;
    private int highlighted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        mChanelListView = (ListView) findViewById(R.id.chanel_list_view);
        dbHelper = new DBHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ArrayList<String> chanelList = dbHelper.getArrayList(sqLiteDatabase);
        final android.widget.ListAdapter theList = new ListAdapter(this, chanelList);
        mChanelListView.setAdapter(theList);
        playIntent = new Intent(this, PlayService.class);
        if (isPlaying) {
            Bitmap tmp = BitmapFactory.decodeResource(getResources(), resID);
            initNotification(name, tmp);
            playIntent.putExtra("sentChanel", url);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.radiomd_icon_edited);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(this);
        initViews();
        setListeners();
        mChanelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name = String.valueOf(parent.getItemAtPosition(position));
                highlighted = R.color.highlighted_text_material_light;
                mChanelListView.setSelector(highlighted);
                url = dbHelper.chanelAdrress(name, sqLiteDatabase);
                playIntent.putExtra("sentChanel", url);
                path = dbHelper.chanelIcon(name, sqLiteDatabase);
                resID = getResources().getIdentifier(path, "drawable", getPackageName());
                bm = BitmapFactory.decodeResource(getResources(), resID);
                if (isPlaying) {
                    buttonPlayStopClick();
                }
                buttonPlayStopClick();
                Toast.makeText(Main.this, getString(R.string.buffering) + " " + name + " ...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        mPlayStopBtn = (Button) findViewById(R.id.play_stop_btn);
        mPlayStopBtn.setText(getString(textID));
    }

    private void setListeners() {
        mPlayStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPlayStopClick();
            }
        });
    }

    public void buttonPlayStopClick() {

        if (name == null) {
            Toast.makeText(Main.this, getString(R.string.not_selected), Toast.LENGTH_SHORT).show();
        } else {
            if (!isPlaying) {
                playAudio();
                textID = R.string.stop_button_text;
                tempName = name;
                mPlayStopBtn.setText(getString(textID));
            } else {
                stopPlayingAudio();
                textID = R.string.play_button_text;
                tempName = null;
                mPlayStopBtn.setText(getString(textID));
            }
        }
    }

    private void playAudio() {
        try {
            initNotification(name, bm);
            startService(playIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        isPlaying = true;
    }


    private void stopPlayingAudio() {
        try {
            cancelNotification();
            stopService(playIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        isPlaying = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sendIntent = new Intent(), chooser;
                sendIntent.setAction(Intent.ACTION_SEND);
                chooser = Intent.createChooser(sendIntent, getString(R.string.share_question));
                if (tempName != null) {
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.share_text_1) + " " + String.valueOf(name) + " "
                                    + getString(R.string.share_text_2)+ "\n" + " https://play.google.com/store/apps/details?id=albu.eduard.moldova.radiomdv04");
                } else {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app)+ "\n" + " https://play.google.com/store/apps/details?id=albu.eduard.moldova.radiomdv04");
                }
                sendIntent.setType("text/plain");
                startActivity(chooser);
        }
        return false;
    }

    public void initNotification (String chanelName, Bitmap bm) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_play_circle_filled_white_18dp);
        builder.setLargeIcon(bm);
        builder.setContentTitle(chanelName);
        builder.setContentText(getString(R.string.notification_context));
        builder.setTicker(getString(R.string.ticker) + " " + chanelName + "...");

        Intent intent = new Intent(this, Main.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pi);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        nm.notify(notificationId, notification);
    }

    public void cancelNotification () {
        nm.cancel(notificationId);
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    void saveData () {
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean(STATE_PL_BTN, isPlaying);
        ed.putString(CHANEL_NAME, name);
        ed.putString(CHANEL_URL, url);
        ed.putString(ICON_PATH, path);
        ed.putInt(RESOURCE, textID);
        ed.putInt(CHANEL_ICON, resID);
        ed.putInt("color", highlighted);
        ed.apply();
    }

    void loadData() {
        preferences = getPreferences(MODE_PRIVATE);
        isPlaying = preferences.getBoolean(STATE_PL_BTN, false);
        textID = preferences.getInt(RESOURCE, R.string.play_button_text);
        name = preferences.getString(CHANEL_NAME, "");
        url = preferences.getString(CHANEL_URL, "");
        resID = preferences.getInt(CHANEL_ICON, R.mipmap.radiomd_icon);
        path = preferences.getString(ICON_PATH, "");
        highlighted = preferences.getInt("color", 0);
    }
}