/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package arduino;

/**
 *
 * @author duduu
 */
import com.fazecast.jSerialComm.*;
import java.util.Scanner;

public class ArduinoSerialCommunication {

    private static final int LED = 13;
    private static SerialPort serialPort;
    private static Scanner scanner;

    public static void main(String[] args) {
        serialPort = SerialPort.getCommPort("COM5"); 
        serialPort.setBaudRate(9600);

        if (serialPort.openPort()) {
            System.out.println("Conexão estabelecida.");

            // Desliga o LED quando inicia
            digitalWrite(LED, false);

            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                        return;

                    byte[] newData = new byte[serialPort.bytesAvailable()];
                    serialPort.readBytes(newData, newData.length);

                    for (byte b : newData) {
                        int statusLed = Character.getNumericValue((char) b);
                        
                        if (statusLed == 0) {
                            digitalWrite(LED, false); // Desliga o LED
                        } else if (statusLed == 1) {
                            digitalWrite(LED, true); // Liga o LED
                        }
                    }
                }
            });

            scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Digite o comando (0 para desligar, 1 para ligar, 3 para encerrar a conexão): ");
                String command = scanner.nextLine().trim(); // Remover espaços em branco extras

                if (command.equals("3")) {
                    serialPort.closePort();
                    System.out.println("Conexão encerrada.");
                    break;
                }

                int value = Integer.parseInt(command);
                digitalWrite(LED, value == 1);
            }
        } else {
            System.out.println("Falha ao estabelecer conexão.");
        }
    }

    private static void digitalWrite(int pin, boolean value) {
        String command = "digitalWrite(" + pin + ", " + (value ? "1" : "0") + ");";
        serialPort.writeBytes(command.getBytes(), command.length());
    }
}