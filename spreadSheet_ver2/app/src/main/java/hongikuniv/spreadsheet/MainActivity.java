package hongikuniv.spreadsheet;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.InsertDimensionRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static java.lang.StrictMath.abs;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    Context context;
    GoogleAccountCredential mCredential;
    private Button nextBtn, prevBtn, saveBtn, newBtn, loadBtn;
    private EditText date_tv;
    int mode = 1, NoResult = 0;

    private EditText record2;
    static final int MAX_LENGTH = 165;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "dilemma4005@gmail.com";
    private static final String[] SCOPES = {SheetsScopes.DRIVE};
    private int pivot = 0; // 2018.03.16 ??????

    List arr;
    ValueRange aa;
    List bb;
    ArrayList<Day> list;

    int ready = 0;

    int cnt = 0, text_size = 10;
    private double touch_interval_X = 0; // X ?????? ?????? ??????
    private double touch_interval_Y = 0; // Y ?????? ?????? ??????

    void refresh() {
        if (ready == 0) {
            while (arr == null || arr.size() < 179);
            ready = 1;
            int n = arr.size();
            for (int i = 0;i < n;i ++) {
                bb = (List)arr.get(i);
                String str1, str2, str3, str4, str5, str6;
                str1 = str2 = str3 = str4 = str5 = str6 = "";

                // ??????
                str1 = bb.get(0).toString();

                // 3 ?????????
                if (bb.size() >= 2) str2 = bb.get(1).toString();

                // ??????
                if (bb.size() >= 3) str4 = bb.get(2).toString();

                // ??????
                if (bb.size() >= 4) str5 = bb.get(3).toString();

                // ??????
                if (bb.size() >= 5) str6 = bb.get(4).toString();

                // ??????
                for (int j = 5;j <= 14;j ++) {
                    if (bb.size() >= j+1) {
                        for (int k = 0;k < bb.get(j).toString().length();k ++) {    // ?????? ????????? ??????
                            if (bb.get(j).toString().charAt(k) == '\n') continue;
                            str3 += bb.get(j).toString().charAt(k);
                        }
                    }
                }
                list.add(new Day(str1, str2, str4, str5, str6, str3));
            }
        }

        record2.setText(list.get(pivot).daily_record);

        // ?????? ??????
        record2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: // ?????? ??????
                        break;
                    case MotionEvent.ACTION_MOVE: // ?????? ??? ?????? ???
                        if(motionEvent.getPointerCount() == 2) { // ?????? ????????? 2?????? ???
                            cnt ++;
                            if (!(cnt % 10 == 0)) break;    // ?????? ????????? ?????? ????????? ?????? 10?????? 1?????? ??????????????? ???
                            System.out.println("??? : " + motionEvent.getX(0) + "  ??? : " + motionEvent.getX(1));

                            double now_interval_X = (double) abs(motionEvent.getX(0) - motionEvent.getX(1)); // ??? ????????? X?????? ?????? ?????????
                            double now_interval_Y = (double) abs(motionEvent.getY(0) - motionEvent.getY(1)); // ??? ????????? Y?????? ?????? ?????????
                            if(touch_interval_X < now_interval_X && touch_interval_Y < now_interval_Y) { // ?????? ?????? ??????
                                if (text_size <= 30) text_size += 3;
                                record2.setTextSize((float)text_size);
                            }
                            if(touch_interval_X > now_interval_X && touch_interval_Y > now_interval_Y) {
                                if (text_size >= 8) text_size -= 3;
                                record2.setTextSize((float)text_size);
                            }
                            touch_interval_X = now_interval_X;
                            touch_interval_Y = now_interval_Y;
                        }
                        break;
                }
                return false;

            }
        });

        // ????????? ?????? ?????? ????????? ????????? ?????? ?????? ??????
        String display_str1 = "";
        if (list.get(pivot).date_string.length() >= 1) {
            for (int k = 0;k < list.get(pivot).date_string.length();k ++) {    // ?????? ????????? ??????
                if (list.get(pivot).date_string.charAt(k) == '\n') {
                    display_str1 += " ";
                    continue;
                }
                display_str1 += list.get(pivot).date_string.charAt(k);
            }
        }
        date_tv.setText(display_str1);
    }





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        list = new ArrayList<Day>(200);

        record2 = (EditText) findViewById(R.id.record2);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        newBtn = (Button) findViewById(R.id.newBtn);
        loadBtn = (Button) findViewById(R.id.loadBtn);

        date_tv = (EditText) findViewById(R.id.textView4);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());

        prevBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        newBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);


        do { // ???????????? ????????? ????????? ?????? ?????? ?????? ??? ?????? ???????????? ?????????
            NoResult = 0; // ??????
            getResultsFromApi();
        } while (NoResult == 1);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pivot > 0) pivot --;
                refresh();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pivot < 100) pivot ++;
                refresh();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //??????
                    }
                });
                alert.setMessage("?????? ??????.");
                alert.show();
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
                prevBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                newBtn.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                loadBtn.setVisibility(View.GONE);

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //??????
                    }
                });
                alert.setMessage("?????? ??????.");
                alert.show();
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List temp = (List)arr.get(0);
                if (temp.get(0).toString().equals("NULL") && temp.get(5).toString().equals("NULL")) return;
                try {
                    mode = 1;
                    putDataToApi(4);
                } catch (Exception e) {
                    System.out.println("no putData");
                    e.printStackTrace();
                }
                Day temp2 = new Day("NULL", "", "", "" , "",  "NULL");
                list.add(0, temp2);
                ready = 0;
                refresh();
            }
        });
    }

    public void save() {
        mode = 0;
        list.get(pivot).daily_record = record2.getText().toString();
        list.get(pivot).date_string = date_tv.getText().toString();
        putDataToApi(pivot);
    }

    private void putDataToApi(int pivot) {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            System.out.println("No network connection available.");
        } else {
            new PostTask(mCredential).execute(String.valueOf(pivot));
        }
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            System.out.println("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(mCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(this,"This app needs to access your Google account (via Contacts).",REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) System.out.println("This app requires Google Play Services. Please install Google Play Services on your device and relaunch this app.");
                else getResultsFromApi();
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) getResultsFromApi();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(MainActivity.this,connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<String, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential).setApplicationName("Google Sheets API Android Quickstart").build();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                getDataFromApi();
                return null;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }


        private void getDataFromApi() throws IOException, GeneralSecurityException {
            String spreadsheetId = "195wifwlqrLelrFp2-Zs52hZPBa-WKlbIZBZPsM-gpkc";
            List<String> ranges = Arrays.asList("?????? ??????!A4:O182");
            Sheets sheetsService = createSheetsService();
            BatchGetValuesResponse result = sheetsService.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();
            aa = result.getValueRanges().get(0);
            arr = aa.getValues();
        }

        private Sheets createSheetsService() throws IOException, GeneralSecurityException {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleAccountCredential  credential = mCredential;
            return new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential).setApplicationName("Google-SheetsSample/0.1").build();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<String> output) {
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
                else if (mLastError instanceof UserRecoverableAuthIOException) startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(),MainActivity.REQUEST_AUTHORIZATION);
                else System.out.println("The following error occurred:\n" + mLastError.getMessage());
            } else System.out.println("Request cancelled.");
        }
    }








    private class PostTask extends AsyncTask<String, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        PostTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential).setApplicationName("Google Sheets API Android Quickstart").build();
        }


        @Override
        protected List<String> doInBackground(String... params) {
            try {
                putData(Integer.parseInt(params[0]));
                return null;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }


        private void putData(int tpivot) throws IOException, GeneralSecurityException {
            if (mode == 0) {    // ????????????
                String spreadsheetId = "195wifwlqrLelrFp2-Zs52hZPBa-WKlbIZBZPsM-gpkc";
                String valueInputOption = "RAW";
                ValueRange requestBody2 = new ValueRange();
                List<Object> row = new ArrayList<>();
                Object obj;

                String str1 = "";
                if (list.get(tpivot).date_string.length() >= 1) {
                    for (int k = 0;k < list.get(tpivot).date_string.length();k ++) {    // ?????? ????????? ??????
                        if (list.get(tpivot).date_string.charAt(k) == ' ') {
                            str1 += "\n";
                            continue;
                        }
                        str1 += list.get(tpivot).date_string.charAt(k);
                    }
                }


                obj = str1; row.add(obj);
                obj = list.get(tpivot).three_keyword; row.add(obj);
                obj = list.get(tpivot).subject; row.add(obj);
                obj = list.get(tpivot).reading; row.add(obj);
                obj = list.get(tpivot).feedback; row.add(obj);
                String str = "";
                int cnt = 0;
                System.out.println(tpivot);
                System.out.println(list.get(tpivot).daily_record);
                for (int i = 0;i < list.get(tpivot).daily_record.length();i ++) {   // ??? ????????? ???????????? ????????? ??????????????? ?????? ?????? ????????? spreadsheet DB??? ???????????????.
                    if (list.get(tpivot).daily_record.charAt(i) == '\n') str += " ";
                    else str += list.get(tpivot).daily_record.charAt(i);
                    if (i > 0 && i % MAX_LENGTH == 0) {
                        if ((i / MAX_LENGTH) > 0 && (i / MAX_LENGTH) % 3 == 0) {
                            obj = str;
                            row.add(obj);
                            System.out.println("????????? ?????? : " + str);
                            str = "";
                            cnt ++;
                        }
                        else str += "\n";
                    }
                }

                if (!str.equals("")) {
                    obj = str;
                    row.add(obj);
                    cnt ++;
                    System.out.println(str);
                }

                String range = "?????? ??????!A" + (tpivot + 4) + ":" + (char)('F'+cnt-1) + (tpivot + 4);

                List<List<Object>> value2 = new ArrayList<>(); value2.add(row);
                requestBody2.setValues(value2);

                Sheets sheetsService2 = createSheetsService();
                Sheets.Spreadsheets.Values.Update request2 = sheetsService2.spreadsheets().values().update(spreadsheetId, range, requestBody2);
                request2.setValueInputOption(valueInputOption);
                UpdateValuesResponse response2 = request2.execute();
                System.out.println(response2);

            } else if (mode == 1) { // ????????? ????????? ?????????
                String spreadsheetId = "195wifwlqrLelrFp2-Zs52hZPBa-WKlbIZBZPsM-gpkc";
                List<Request> requests = new ArrayList<>();
                requests.add(new Request().setInsertDimension(new InsertDimensionRequest().setRange(new DimensionRange().setSheetId(466769518).setDimension("ROWS").setStartIndex(3).setEndIndex(4))));
                BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
                requestBody.setRequests(requests);

                Sheets sheetsService = createSheetsService();
                Sheets.Spreadsheets.BatchUpdate request = sheetsService.spreadsheets().batchUpdate(spreadsheetId, requestBody);
                BatchUpdateSpreadsheetResponse response = request.execute();
                System.out.println(response);

                String range = "?????? ??????!A"+tpivot+":F"+tpivot;
                String valueInputOption = "RAW";
                ValueRange requestBody2 = new ValueRange();
                List<Object> row = new ArrayList<>();
                Object obj;
                obj = "NULL"; row.add(obj);
                obj = ""; row.add(obj);
                obj = ""; row.add(obj);
                obj = ""; row.add(obj);
                obj = ""; row.add(obj);
                obj = "NULL"; row.add(obj);
                List<List<Object>> value2 = new ArrayList<>(); value2.add(row);
                requestBody2.setValues(value2);

                Sheets sheetsService2 = createSheetsService();
                Sheets.Spreadsheets.Values.Update request2 = sheetsService2.spreadsheets().values().update(spreadsheetId, range, requestBody2);
                request2.setValueInputOption(valueInputOption);
                UpdateValuesResponse response2 = request2.execute();
                System.out.println(response2);
            }
        }

        private Sheets createSheetsService() throws IOException, GeneralSecurityException {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleAccountCredential  credential = mCredential;
            return new com.google.api.services.sheets.v4.Sheets.Builder(transport, jsonFactory, credential).setApplicationName("Google Sheets API Android Quickstart").build();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
            } else output.add(0, "Data retrieved using the Google Sheets API:");
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), MainActivity.REQUEST_AUTHORIZATION);
                } else { }
            } else { }
        }
    }

}

/*
-------------------------- 2019. 02. 19 (???)

1. ?????? batchGet??? ????????????.
~ batchGet ????????? ????????? .... ????????? ????????? 11??? ?????? ??????????????? ????????????... ??? 11??? ?????? ?????????????????????
????????? ?????? ??????. ???????????? ????????? ?????? ????????? ????????? ????????? ????????????. ???????????? ????????? ?????? ?????? ??????????????? ??????..


<??????>
1. ?????? ???????????? ?????? ????????????. - ???????????? ???????????? ????????? ??????.
2. ?????? ?????? ?????? ????????? - ?????? ????????? ?????? ?????????, ?????? ????????? ?????? ????????? ?????? ?????? ??????.
3. ?????? api ??????
4. ????????? ????????? ???????????? - ?????? ????????? ????????? ??? ????????? ?????? ??????????????????. v
5. ?????? ?????? ???????????? ?????? ?????? v
6. \n ???????????? ??????????????? ???????????? ????????? ?????? ????????? ???????????????
7. ????????? ??? ?????? ????????????.

-------------------------- 2019. 02. 20 (???)
1. ?????? ?????? ????????? ???????????? ?????? + ?????? ?????? ?????? + textview -> edittext
2. ????????? ?????? ???????????? + ?????? ?????? ??? ????????? ?????? ????????? ???????????????
3. ?????? ?????? (?????? ?????? ????????? ?????????????????? ?????? ???????????? ?????? ???????????? ???????????????)
4. ?????? ?????? (?????? ?????? ??????????????? ????????? ????????? ???????????? ????????????)
5. ???????????? textview??? ???????????? ????????? ????????? ?????? / ?????????????????? ????????? ??????????????? ????????? ??? ????????? ???????????? ???.
6. ?????? ??????, ?????? ?????? ????????? '?????? ??????' ????????? ????????? ?????? ??????.
7. EditText DailyRecord ??? ????????? ??? ???????????? ????????? ???????????? ??????????????? ????????? ?????? ????????? ???.
8. ?????????/?????? ??????
9. ??????, 3?????????, ?????? ????????? ??? ?????? ???????????? ???????????? ????????? ???????????? ????????? ????????? Day Class??? ?????? ???????????? ??????.
10. EditText ?????? ?????? (?????? ??????/??????)

<???????????? ?????? ???>
1. ???????????? ?????? ????????? ???????????? ????????? ?????? ?????? ???????????? ???????????? ????????? ????????? ?????????.. ?????? ?????? ????????? ??????.
??????????????? ????????? ??????.... ????????? ?????? ?????? ??? ?????? ????????? ???????????? ?????????.
2. ?????? ?????? ??????.. ???????????? ??????.. ????????? ??????????????? ????????? ?????? ??????.

???????????? ?????? ????????? ????????? ??????????????? ????????? ???????????? ????????? ????????? ????????????  ????????? ????????? ????????? ????????? ???????????????.
??????????????? ??????????????? ????????? ??????, ?????? ?????? ????????? ????????? ???????????? ?????? ????????? ??? ??? ????????? ??????.
 */