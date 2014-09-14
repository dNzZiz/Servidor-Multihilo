/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServidorClienteCompleto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jhon
 */
public class Servidor {

    private final int PUERTO = 5000;
    private ServerSocket servidor = null;

    public Servidor() throws IOException {
        // Creamos socket servidor escuchando en el mismo puerto donde se comunica el cliente
        // en este caso el puerto es el 4400
        servidor = new ServerSocket(5000);
        System.out.println("Esperando opcion...");
        this.escogeOpcion();
    }

    public static void main(String[] args) throws IOException {
        Servidor sf = new Servidor();
    }

    public void escogeOpcion() throws IOException {
        int op = -1;
        Socket cliente = servidor.accept(); 
        // Creamos flujo de entrada para leer los datos que envia el cliente 
        DataInputStream flujo_entrada = new DataInputStream(cliente.getInputStream());
        op = flujo_entrada.readInt(); 
        
        if (op == 2) {
            this.escucha();
        } else {
            this.iniciarServidor();
        }
        flujo_entrada.close();
        cliente.close();
    }

    public void escucha() {
        try {
            // Se abre el socket servidor
            ServerSocket socketServidor = new ServerSocket(PUERTO);

            // Se espera un cliente
            Socket cliente = socketServidor.accept();

            // Llega un cliente.
            System.out.println("Aceptado cliente");

            // Cuando se cierre el socket, esta opci�n hara que el cierre se
            // retarde autom�ticamente hasta 10 segundos dando tiempo al cliente
            // a leer los datos.
            cliente.setSoLinger(true, 10);

            // Se lee el mensaje de petici�n de fichero del cliente.
            ObjectInputStream ois = new ObjectInputStream(cliente
                    .getInputStream());
            Object mensaje = ois.readObject();

            // Si el mensaje es de petici�n de fichero
            if (mensaje instanceof Cliente_Recibe_Servidor_Envia.MensajeDameFichero) {
                // Se muestra en pantalla el fichero pedido y se envia
                System.out.println("Me piden: "
                        + ((Cliente_Recibe_Servidor_Envia.MensajeDameFichero) mensaje).nombreFichero);
                enviaFichero(((Cliente_Recibe_Servidor_Envia.MensajeDameFichero) mensaje).nombreFichero,
                        new ObjectOutputStream(cliente.getOutputStream()));
            } else {
                // Si no es el mensaje esperado, se avisa y se sale todo.
                System.err.println(
                        "Mensaje no esperado " + mensaje.getClass().getName());
            }

            // Cierre de sockets 
            cliente.close();
            socketServidor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviaFichero(String fichero, ObjectOutputStream oos) {
        try {
            boolean enviadoUltimo = false;
            // Se abre el fichero.
            FileInputStream fis = new FileInputStream(fichero);

            // Se instancia y rellena un mensaje de envio de fichero
            Cliente_Recibe_Servidor_Envia.MensajeTomaFichero mensaje = new Cliente_Recibe_Servidor_Envia.MensajeTomaFichero();
            mensaje.nombreFichero = fichero;

            // Se leen los primeros bytes del fichero en un campo del mensaje
            int leidos = fis.read(mensaje.contenidoFichero);

            // Bucle mientras se vayan leyendo datos del fichero
            while (leidos > -1) {

                // Se rellena el n�mero de bytes leidos
                mensaje.bytesValidos = leidos;

                // Si no se han leido el m�ximo de bytes, es porque el fichero
                // se ha acabado y este es el �ltimo mensaje
                if (leidos < Cliente_Recibe_Servidor_Envia.MensajeTomaFichero.LONGITUD_MAXIMA) {
                    mensaje.ultimoMensaje = true;
                    enviadoUltimo = true;
                } else {
                    mensaje.ultimoMensaje = false;
                }

                // Se env�a por el socket
                oos.writeObject(mensaje);

                // Si es el �ltimo mensaje, salimos del bucle.
                if (mensaje.ultimoMensaje) {
                    break;
                }

                // Se crea un nuevo mensaje
                mensaje = new Cliente_Recibe_Servidor_Envia.MensajeTomaFichero();
                mensaje.nombreFichero = fichero;

                // y se leen sus bytes.
                leidos = fis.read(mensaje.contenidoFichero);
            }

            if (enviadoUltimo == false) {
                mensaje.ultimoMensaje = true;
                mensaje.bytesValidos = 0;
                oos.writeObject(mensaje);
            }
            // Se cierra el ObjectOutputStream
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciarServidor() {
        while (true) {
            try {
                // Creamos el socket que atendera el servidor
                Socket cliente = servidor.accept();

                // Creamos flujo de entrada para leer los datos que envia el cliente 
                DataInputStream flujo_entrada = new DataInputStream(cliente.getInputStream());

                // Obtenemos el nombre del archivo
                String nombreArchivo = flujo_entrada.readUTF().toString();

                // Obtenemos el tamaño del archivo
                int tam = flujo_entrada.readInt();

                System.out.println("Recibiendo archivo " + nombreArchivo);

               // Creamos flujo de salida, este flujo nos sirve para 
                // indicar donde guardaremos el archivo
                FileOutputStream fos = new FileOutputStream("Repositorio/" + nombreArchivo);
                BufferedOutputStream out = new BufferedOutputStream(fos);
                BufferedInputStream in = new BufferedInputStream(cliente.getInputStream());

                // Creamos el array de bytes para leer los datos del archivo
                byte[] buffer = new byte[tam];

                // Obtenemos el archivo mediante la lectura de bytes enviados
                for (int i = 0; i < buffer.length; i++) {
                    buffer[ i] = (byte) in.read();
                }

                // Escribimos el archivo 
                out.write(buffer);

                // Cerramos flujos
                out.flush();
                in.close();
                out.close();
                cliente.close();

                System.out.println("Archivo Recibido " + nombreArchivo);

            } catch (Exception e) {
                System.out.println("Recibir: " + e.toString());
            }
        }
    }

}
