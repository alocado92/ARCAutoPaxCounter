package com.example.cristina.arc_autopaxcounter;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, StartStudyFragment.OnFragmentInteractionListener, CreateStudyFragment.OnDataPass {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /*
     * Bluetooth Setup Dialog Fragment
     */
    private BluetoothFragmentDialog bluetoothF;
    private CreateStudyFragment studyF;
    public int REQUEST_DISCOVERABLE_CODE = 42;
    private FragmentManager fragmentManager;
    private StartStudyFragment studyFragment;
    private List<Integer> mBuffer = new ArrayList<>();
    private boolean isGPSenabled;

    //ACTION


    //edit study view
    private boolean isEditStudy = false;
    //start study
    private boolean isStartStudy = false;

    /*
    * States of navigation drawer
    * */
    //drawer before creating study or after stopping study or after discarding study
    private boolean study_notC = true;
    //drawer after creating study and before starting study
    private boolean study_C_notS = false;
    //drawer after starting study, and before stopping or before discarding study
    private boolean study_CS_notStop = false;

    private Menu menu;
    private BluetoothDevice bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            bt = savedInstanceState.getParcelable("BTdevice");
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        this.getBundleValues();
        fragmentManager = getSupportFragmentManager();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(aReceiver, filter);
    }

    private void getBundleValues() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            bt = bundle.getParcelable("BTdevice");
        }
    }

    @Override
    protected void onResume() {
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGPSenabled = true;
        } else {
            isGPSenabled = false;
        }

        if(study_notC) {
            mNavigationDrawerFragment.changeDrawerOptions(study_notC, getStudy_notC_menu());
        } else if(study_C_notS) {
            mNavigationDrawerFragment.changeDrawerOptions(study_C_notS, getStudy_C_notS_menu());
        } else if(study_CS_notStop)
            mNavigationDrawerFragment.changeDrawerOptions(study_CS_notStop, getStudy_CS_notStop_menu());

        super.onResume();
    }

    private String[] getStudy_notC_menu() {

        if(!isGPSenabled)
            return new String[]{getString(R.string.new_study), getString(R.string.bluetooth_setup), getString(R.string.enable_gps)};
        else
            return new String[]{getString(R.string.new_study), getString(R.string.bluetooth_setup), getString(R.string.disable_gps)};
    }

    private String[] getStudy_C_notS_menu() {

        if(!isGPSenabled)
            return  new String[]{getString(R.string.start_study), getString(R.string.edit_study), getString(R.string.discard_study), getString(R.string.bluetooth_setup), getString(R.string.enable_gps)};
        else
            return new String[]{getString(R.string.start_study), getString(R.string.edit_study),
                getString(R.string.discard_study), getString(R.string.bluetooth_setup), getString(R.string.disable_gps)};
    }

    private String[] getStudy_CS_notStop_menu() {

        if(!isGPSenabled)
            return new String[]{getString(R.string.stop_study), getString(R.string.edit_study), getString(R.string.discard_study), getString(R.string.bluetooth_setup), getString(R.string.enable_gps)};
        else
            return new String[]{getString(R.string.stop_study), getString(R.string.edit_study),
                getString(R.string.discard_study), getString(R.string.bluetooth_setup), getString(R.string.disable_gps)};
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DISCOVERABLE_CODE) {
            // Bluetooth Discoverable Mode does not return the standard Activity result codes.
            // Instead, the result code is the duration (seconds) of discoverability or a negative number if the user answered "NO".
            if (resultCode == 0) {
                Toast.makeText(this, "Can't proceed until Bluetooth is discoverable", Toast.LENGTH_SHORT).show();
            } else if (resultCode == 300) {
                Toast.makeText(this, "Discoverable mode enabled", Toast.LENGTH_SHORT).show();
                bluetoothF = new BluetoothFragmentDialog();
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                if(!study_notC) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isStartStudyNull", true);
                    bluetoothF.setArguments(bundle);
                }
                ft.add(bluetoothF, null);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        fragmentManager = getSupportFragmentManager();
        Fragment myFragment = null;
        boolean isDialog = false;
        boolean isCreateStudy = false;
        boolean isBluetoothSetup = false;
        boolean isDiscoverable = true;
        boolean isManageGPS = false;
        boolean isDiscardDialog = false;
        boolean isStopDialog = false;

        if(study_notC) {
            switch (position) {
                case 0:     //Create Study
                    studyF = new CreateStudyFragment();
                    isDialog = true;
                    isCreateStudy = true;
                    break;
                case 1:     //Bluetooth Setup
                    isDiscoverable = doBluetoothSetup(isDiscoverable);
                    isDialog = true;
                    isBluetoothSetup = true;
                    break;
                case 2:     //GPS Enable
                    LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if(lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //GPS is enabled
                        String message = "Disable GPS service in your settings.";
                        String title = "Disable GPS";
                        manageGPS(message, title);
                    } else {
                        String message = "Enable GPS to find current location. Click OK to enable location services in your settings.";
                        String title = "Enable GPS";
                        manageGPS(message, title);
                    }
                    isDialog = true;
                    isManageGPS = true;
                    break;
            }
        } else {
            switch (position) {
                case 0:     //Start Study or Stop study
                    if(study_C_notS) {      //Start Study
                        startUpdateMenu();
                        isStartStudy = true;
                        studyFragment.setStart(isStartStudy);
                        studyFragment.startClick();
                    } else if(study_CS_notStop) {       //Stop Study
                        studyFragment.stopClick();
                        isDialog = true;
                        isStopDialog = true;
                    }
                    break;
                case 1:     //Edit Study
                    if(studyFragment != null) {
                        studyFragment.setIsEdit(true);
                        studyFragment.onOptionsItemSelected(menu.findItem(R.id.pencil_icon));
                    }
                    isDialog = false;
                    isEditStudy = true;
                    break;
                case 2:     //Discard Study
                    confirmDiscardStudy();
                    isDialog = true;
                    isDiscardDialog = true;
                    break;
                case 3:     //Bluetooth Setup
                    isDiscoverable = doBluetoothSetup(isDiscoverable);
                    isDialog = true;
                    isBluetoothSetup = true;
                    break;
                case 4:     //Disable GPS
                    LocationManager lManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                    if(lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //GPS is enabled
                        String message = "Disable GPS service in your settings.";
                        String title = "Disable GPS";
                        manageGPS(message, title);
                    } else {
                        String message = "Enable GPS to find current location. Click OK to enable location services in your settings.";
                        String title = "Enable GPS";
                        manageGPS(message, title);
                    }
                    isDialog = true;
                    isManageGPS = true;
                    break;
            }
        }

        if(isDialog) {
            if(isBluetoothSetup) {
                bluetoothF = new BluetoothFragmentDialog();
                if (isDiscoverable) {
                    if(!study_notC) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isStartStudyNull", true);
                        bluetoothF.setArguments(bundle);
                        ft.add(bluetoothF, null);
                        ft.commitAllowingStateLoss();
                    } else {
                        bluetoothF.show(fragmentManager, getString(R.string.bluetooth_setup));
                    }
                }
            }
            else if(isCreateStudy) {
                if(bt != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("BTdevice", bt);
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    studyF.setArguments(bundle);
                    ft.replace(R.id.container, studyF).commit();
                } else
                    Toast.makeText(this, "Must setup Bluetooth connection before creating new study", Toast.LENGTH_SHORT).show();
            } else if(isManageGPS || isDiscardDialog || isStopDialog) {
               ;
            }
        } else {
            if(!isEditStudy && !isStartStudy) {
                // update the main content by replacing fragments
                fragmentManager.beginTransaction().replace(R.id.container, myFragment).commit();
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.new_study);
                break;
            case 2:
                mTitle = getString(R.string.bluetooth_setup);
                break;
            case 3:
                mTitle = getString(R.string.enable_gps);
                break;
        }
    }

    /**
     * Logic of fragments when selected
     */
    public boolean doBluetoothSetup(boolean discoverable) {
        boolean isDiscoverable = discoverable;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null) {
            if (!adapter.isEnabled())
                adapter.enable();
            if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                //The device is not discoverable
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_CODE);
                isDiscoverable = false;
            }
        }

        return isDiscoverable;
    }

    private void manageGPS(String message, String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private void confirmDiscardStudy() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Study")
                .setMessage("Data stored in memory and in remote database will be deleted. Proceed?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Dialog dialog = (Dialog) d;
                                AppService.prepareDiscardStudy(((Dialog) d).getContext(), StartStudyFragment.HTTP_DISCARD,
                                        studyFragment.getStudyName(), studyFragment.getDateCreated(), studyFragment.getTimeCreated());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    public void clean() {
        study_notC = true;
        study_C_notS = false;
        study_CS_notStop = false;
        isEditStudy = false;
        mNavigationDrawerFragment.changeDrawerOptions(study_notC, getStudy_notC_menu());
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(studyF);
        ft.detach(studyFragment);
        getSupportFragmentManager().popBackStack();
        ft.remove(studyFragment).commit();
    }

    public void startUpdateMenu() {
        study_notC = false;
        study_C_notS = false;
        study_CS_notStop = true;
        mNavigationDrawerFragment.changeDrawerOptions(study_CS_notStop, getStudy_CS_notStop_menu());
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        //send ack 'S' to Bluetooth
        Intent intent = new Intent(ARC_Bluetooth.BROADCAST_ACTION_ACK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(ARC_Bluetooth.BT_ACK, AppService.BLUETOOTH_ACK_MESSAGE_STOP);
        intent.putExtra(ARC_Bluetooth.BT_CLOSE, true);
        intent.putExtra(StartStudyFragment.MAP_FLAG, false);
        sendBroadcast(intent);

        //close Bluetooth connected thread
        /*Intent localIntent = new Intent(ARC_Bluetooth.BROADCAST_ACTION_DISCONNECT);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
        localIntent.putExtra(ARC_Bluetooth.BT_CLOSE, true);
        localIntent.putExtra(StartStudyFragment.MAP_FLAG, false);
        sendBroadcast(localIntent);*/

        unregisterReceiver(aReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            this.menu = menu;
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private final BroadcastReceiver aReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                showConnectionLostDialog();
            }
        }
    };

    private void showConnectionLostDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bluetooth Connection Lost")
                .setMessage("Bluetooth connection was lost unexpectedly. Please connect to Bluetooth device.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                            }
                        });
        builder.create().show();
    }

    @Override
    public void onDataPass(Boolean isCreated, Fragment studyFrag) {
        if(isCreated) {
            study_notC = false;
            study_C_notS = true;
            study_CS_notStop = false;
            studyFragment = (StartStudyFragment) studyFrag;
            mNavigationDrawerFragment.changeDrawerOptions(isCreated, getStudy_C_notS_menu());
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

    }

}
