package com.atrativa.beacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScrollingActivity extends AppCompatActivity implements BeaconConsumer {

    protected static final String TAG = "MonitoringScrollingActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BeaconManager beaconManager;
    private Context context;
    private BeaconPet beaconPet = new BeaconPet();
    private List<Beacon> beacons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        verificarBluetooth();
        logToDisplay("Busca por Beacons iniciada!");
        logToDisplay("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Esta aplicação necessita de acesso a localização");
                builder.setMessage("Por favor, permita que a aplicação tenha acesso a localização para que ela detecte Beacons..");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*scannerTimer = new Timer();
                scannerTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        onBeaconServiceConnect();
                    }

                }, 0, 2000);*/
                if(beaconPet.getBeacon() != null){
                   /* final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Distancia " + String.valueOf(beaconLocalizado.getDistance()));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                            System.exit(0);
                        }
                    });
                    builder.show();*/

                    Intent intent = new Intent(ScrollingActivity.this, MapActivity.class);
                    Bundle bundle = intent.getExtras();
                    double x = beaconPet.getBeacon().getDistance();
                    String w = String.valueOf(x);
                    float y = Float.parseFloat(w);
                    intent.putExtra("distancia",Float.parseFloat(String.valueOf(beaconPet.getBeacon().getDistance())) );
                    intent.putExtra("x",Float.parseFloat(beaconPet.getBeacon().getId2().toString()) );
                    intent.putExtra("y",Float.parseFloat(beaconPet.getBeacon().getId3().toString()) );
                    intent.putExtra("nomePet",Float.parseFloat(beaconPet.getNome()) );
                    startActivity(intent);
                }else{
                    Snackbar.make(view, "Nenhum Beacon localizado!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
        });
        //beaconManager.setBackgroundScanPeriod(2000);
        createTimedSimulatedBeacons();
       /* new CountDownTimer(10000, 2000) {
            public void onTick(long millisUntilFinished) {
                //logToDisplay("addRangeNotifier: " + new Date().toString());
                //Toast.makeText(context, "addRangeNotifier: " + new Date().getSeconds()  , Toast.LENGTH_SHORT).show();
                onBeaconServiceConnect();
            }

            public void onFinish() {
                Toast.makeText(context, "Done ", Toast.LENGTH_SHORT).show();

            }
        }.start();*/
    }


    private void verificarBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth não está ativo!");
                builder.setMessage("Por favor ligue o bluetooth e reinicie a aplicação.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Telefone sem Bluetooth LE.");
            builder.setMessage("Desculpe, essa aplicação suporta somente Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    private ArrayList<Beacon> createBeacons(int number_of_beacons) {
        if (number_of_beacons <= 0) number_of_beacons = 4;
        ArrayList<Beacon> b = new ArrayList<>();
        for (int i = 1; i <= number_of_beacons; i++) {
            beacons.add(new AltBeacon.Builder().setId1("11111111-2222-3333-4444-555555555555")
                    .setId2("1").setId3(Integer.toString(i)).setRssi(-55).setTxPower(-55).build());
        }
        return b;
    }
    private ScheduledExecutorService scheduleTaskExecutor;

    public void createTimedSimulatedBeacons() {
            beacons.addAll(createBeacons(4));

            final List<Beacon> finalBeacons = new ArrayList<>(beacons);

            beacons.clear();

            scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    try {
                        if (finalBeacons.size() > beacons.size())
                            beacons.add(finalBeacons.get(beacons.size()));
                        else
                            scheduleTaskExecutor.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10, TimeUnit.SECONDS);

    }
    @Override
    public void onBeaconServiceConnect() {

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                if(beacons.size() > 0) {

                    logToDisplay("Region onBeaconServiceConnect: " + region.getUniqueId().toUpperCase());
                    logToDisplay("\n");
                }
                for (final Beacon beacon: beacons) {

                    beaconPet.setBeacon( beacon);
                    logToDisplay("ID1: " + beacon.getId1());
                    logToDisplay("Distância: " + new DecimalFormat("#0.000").format(beacon.getDistance()));
                    /*Beacon b = new Beacon.Builder()
                            .setId1(beacon.getId1().toString())
                            .setId2(beacon.getId2().toString())
                            .setId3(beacon.getId3().toString())
                            .setManufacturer(beacon.getManufacturer()) // Radius Networks.  Change this for other beacon layouts
                            .setTxPower(beacon.getTxPower())
                            .setDataFields(beacon.getDataFields()) // Remove this for beacon layouts without d: fields
                            .build();
                    BeaconParser beaconParser = new BeaconParser()
                            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
                    BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                    beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

                        @Override
                        public void onStartFailure(int errorCode) {
                            Toast.makeText(context, "Advertisement start failed with code: "+errorCode, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onStartSuccess(AdvertiseSettings settingsInEffect) {

                            Toast.makeText(context, "timer" + settingsInEffect.getTimeout(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "dist" + beacon.getDistance(), Toast.LENGTH_SHORT).show();

                            Toast.makeText(context, "Advertisement start succeeded.", Toast.LENGTH_SHORT).show();

                        }
                    });*/

                    /*logToDisplay("Informações do Beacon localizado");
                    logToDisplay("");
                    logToDisplay("Nome da Rede Bluetooth: " + beacon.getBluetoothName());
                    logToDisplay("Endereço Bluetooth: " + beacon.getBluetoothAddress());
                    logToDisplay("ID1: " + beacon.getId1());
                    logToDisplay("ID2: " + beacon.getId2());
                    logToDisplay("ID3: " + beacon.getId3());
                    logToDisplay("Distância: " + new DecimalFormat("#0.000").format(beacon.getDistance()));
                    logToDisplay("");*/
                }
            }
        });
        try {
            //logToDisplay("onBeaconServiceConnect: " + new Date().toString());
            beaconManager.startRangingBeaconsInRegion(new Region("REGIÃO MAPEADA", null, null, null));
            Toast.makeText(context, "onBeaconServiceConnect: " + new Date().getSeconds()  , Toast.LENGTH_SHORT).show();

        } catch (RemoteException e) {   }
    }




    public void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                LinearLayout linearLayout = (LinearLayout) ScrollingActivity.this.findViewById(R.id.layout);
                EditText editText = (EditText)linearLayout
                        .findViewById(R.id.monitoringText);
                editText.append(line+"\n");
                EditText editTextNome = (EditText)linearLayout
                        .findViewById(R.id.namePet);




                /*editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DATEINIT_DIALOG);
                    }

                });*/
            }
        });
    }

}
