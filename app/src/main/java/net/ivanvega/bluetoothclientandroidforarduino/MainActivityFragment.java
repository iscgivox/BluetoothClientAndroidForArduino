package net.ivanvega.bluetoothclientandroidforarduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements Switch.OnCheckedChangeListener {
    BluetoothAdapter mBluetoothAdapter ;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    Button btnConnect;
    OutputStream mOutputStream;
    Switch swtLed1, swtLed2, swtLed3;
    private View UI;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View disMain = inflater.inflate(R.layout.fragment_main, container, false);
        setUI(disMain);

        return disMain              ;
    }

    public void setUI(View UI) {
        swtLed1 = (Switch) UI.findViewById(R.id.swtLed1); swtLed1.setOnCheckedChangeListener(this);
        swtLed2 = (Switch) UI.findViewById(R.id.swtLed2);swtLed2.setOnCheckedChangeListener(this);
        swtLed3 = (Switch) UI.findViewById(R.id.swtLed3); swtLed3.setOnCheckedChangeListener(this);

        btnConnect = (Button)UI.findViewById(R.id.btnConect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                    btnConnect.setText("Conectar BT");
                    try {
                        endBT();
                    } catch (IOException e) {
                        showMessage(e.getMessage(), Toast.LENGTH_LONG);
                    }
                } else {
                    btnConnect.setText("Desconectar BT");
                    try {
                        startBT();
                    } catch (IOException e) {
                        showMessage(e.getMessage(), Toast.LENGTH_LONG);
                    }
                }

            }


        });
    }

    private void startBT() throws IOException{
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            showMessage("Dispositivo No tiene Blueetooth", Toast.LENGTH_LONG);
            return;
        }

        if(!mBluetoothAdapter.isEnabled()){
            Intent intentBTEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intentBTEnable,1001);
        }

        Set<BluetoothDevice>  devicesVinculados =  mBluetoothAdapter.getBondedDevices();
        boolean blnVinculado = false;
        for(BluetoothDevice item : devicesVinculados)
        {
            if(item.getName().equals("HC-05")){
                mBluetoothDevice = item;
                blnVinculado = true;
                showMessage("Dispositivo Vinculado", Toast.LENGTH_SHORT);
            }
        }

        if(blnVinculado){
            openBT();
        }else{
                showMessage("No se ha encontrado el dispositivo Arduino", Toast.LENGTH_SHORT);
        }
    }

    private void openBT() throws IOException{
//        UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//        UUID  uuid = UUID.fromString("00001101-0000-8000-00805f9b34fb");
        UUID  uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mBluetoothSocket =  mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        mBluetoothSocket.connect();
        mOutputStream=mBluetoothSocket.getOutputStream();
        enableSwitxh(mBluetoothSocket.isConnected());
        showMessage("Conectado a Arduino", Toast.LENGTH_LONG);

    }

    private void enableSwitxh(boolean connected) {
        swtLed1.setEnabled(connected);
        swtLed2.setEnabled(connected);
        swtLed3.setEnabled(connected);
    }

    private void showMessage(String message, int lengthLong) {
        Toast.makeText(getActivity(),message,lengthLong).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001 && requestCode==1){
            showMessage("Se Activo Bluetoot", Toast.LENGTH_SHORT);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void endBT() throws IOException{
        mOutputStream.close();
        mBluetoothSocket.close();
        enableSwitxh(mBluetoothSocket.isConnected());
    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.getId()== R.id.swtLed1){
            sendBT(0,b);
        }
        if(compoundButton.getId()== R.id.swtLed2){
            sendBT(1,b);
        }
        if(compoundButton.getId()== R.id.swtLed3){
            sendBT(2,b);
        }
    }

    private void sendBT(int i, boolean b) {
        String msj =    i + (b ? "H":"L");
        msj += "\n";
        try {
            mOutputStream.write(msj.getBytes());
            showMessage("Datos enviados", Toast.LENGTH_LONG);
        }catch (IOException e){
            showMessage(e.getMessage(), Toast.LENGTH_LONG);
        }


    }
}
