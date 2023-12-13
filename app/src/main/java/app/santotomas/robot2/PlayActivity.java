package app.santotomas.robot2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Looper;

public class PlayActivity extends AppCompatActivity {
    Looper mainLooper = Looper.getMainLooper();
    Handler bluetoothIn = new Handler(mainLooper);
    //Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    //private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = null;

    ////////////////////////////////////////////
    private String DEVICE_ADDRESS = "00:21:11:01:86:20";
    private static final UUID UUID_BT = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean connected = false;

    //////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        Button btnON = findViewById(R.id.btnON);
        Button btnOFF = findViewById(R.id.btnOFF);
        Button btnDesconectar = findViewById(R.id.btnDesconectar);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        EditText edtTextoOut = findViewById(R.id.edtTextoOut);
        TextView txtMensaje = findViewById(R.id.txtMensaje);
//////////////////////////////////////
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no está disponible en este dispositivo", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Por favor, habilita Bluetooth y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                connectBluetooth();
            }
        }
        ///////////////////////////////////////
        /** bluetoothIn = new Handler(mainLooper) {
         public void handleMessage(@NonNull android.os.Message msg) {
         if (msg.what == handlerState) {
         char MyCaracter = (char) msg.obj;
         if (MyCaracter == '1') {
         txtMensaje.setText(R.string.led_on);
         }
         if (MyCaracter == '2') {
         txtMensaje.setText(R.string.led_off);
         }

         }
         }
         };


         btnON.setOnClickListener(v -> MyConexionBT.write("1"));
         btnOFF.setOnClickListener(v -> MyConexionBT.write("2"));

         */
        btnON.setOnClickListener(v -> {
            sendData("1");
            txtMensaje.setText("Haz prendido el LED");
        });
        btnOFF.setOnClickListener(v -> {
            sendData("0");
            txtMensaje.setText("Haz apagado el LED");
        });
        btnEnviar.setOnClickListener(v -> {
            sendData("3");
            txtMensaje.setText("Haz enviado un mensaje al display :o!");
            sendData(edtTextoOut.getText().toString());
        });

        btnDesconectar.setOnClickListener(v -> {
            sendData("2");
            txtMensaje.setText("@string/txt");
            if (btSocket != null) {
                try {
                    btSocket.close();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

    }

    ///////////////////////////////////////////
    private void connectBluetooth() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID_BT);
            bluetoothSocket.connect();
            connected = true;
            Toast.makeText(this, "Conexión Bluetooth establecida", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            connected = false;
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar al dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (connected) {
            // Inicializar InputStream y OutputStream si es necesario
            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendData(String data) {
        if (connected && outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                Toast.makeText(this, "Datos enviados: " + data, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al enviar datos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No estás conectado al dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculadosActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_SHORT).show();
        }
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void VerificarEstadoBT() {
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (btAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "El Bluetooth está activado", Toast.LENGTH_SHORT).show();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            // Aquí puedes manejar el resultado de la actividad
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // La actividad se completó exitosamente
                                Intent data = result.getData();
                                // Procesar los datos según sea necesario
                            }
                        });
                launcher.launch(enableBtIntent);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] byte_in = new byte[1];

            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write (String input)
        {
            try {
                mmOutStream.write(input.getBytes());

            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}