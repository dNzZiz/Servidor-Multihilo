package Cliente_Envia_Servidor_Recibe;

import java.net.*;
import java.io.*;


class EnviarArchivo
{  
         private String nombreArchivo = "";
         private final int PUERTO = 5000;
         
         public EnviarArchivo( String nombreArchivo ){
              this.nombreArchivo = nombreArchivo;
         }
         
         public void enviarArchivo( ){
          try{
            // Creamos la direccion IP de la maquina que recibira el archivo
            InetAddress direccion = InetAddress.getByName( "localhost" );
         
            // Creamos el Socket con la direccion y elpuerto de comunicacion
            Socket socket = new Socket( direccion, PUERTO );
            socket.setSoTimeout( 2000 );
            socket.setKeepAlive( true );
         
            // Creamos el archivo que vamos a enviar
            File archivo = new File( nombreArchivo );
         
            // Obtenemos el tamaño del archivo
            int tamañoArchivo = ( int )archivo.length();
            int opcion = 1;
         
            // Creamos el flujo de salida, este tipo de flujo nos permite 
            // hacer la escritura de diferentes tipos de datos tales como
            // Strings, boolean, caracteres y la familia de enteros, etc.
            DataOutputStream flujo_salida = new DataOutputStream( socket.getOutputStream() );
            
            // Enviamos la opción 
            flujo_salida.writeInt(opcion );
            
            System.out.println( "Esta es la opción"+opcion+ "1, ENVIAR ARCHIVO: ");
         
            System.out.println( "Enviando Archivo: "+archivo.getName() );
         
            // Enviamos el nombre del archivo 
            flujo_salida.writeUTF( archivo.getName() );
         
            // Enviamos el tamaño del archivo
            flujo_salida.writeInt( tamañoArchivo );
         
            // Creamos flujo de entrada para realizar la lectura del archivo en bytes
            FileInputStream fis = new FileInputStream( nombreArchivo );
            BufferedInputStream bis = new BufferedInputStream( fis );
         
            // Creamos el flujo de salida para enviar los datos del archivo en bytes
            BufferedOutputStream bos = new BufferedOutputStream( socket.getOutputStream()          );
         
            // Creamos un array de tipo byte con el tamaño del archivo 
            byte[] buffer = new byte[ tamañoArchivo ];
         
            // Leemos el archivo y lo introducimos en el array de bytes 
            bis.read( buffer ); 
         
            // Realizamos el envio de los bytes que conforman el archivo
            for( int i = 0; i < buffer.length; i++ )
            {
                bos.write( buffer[ i ] ); 
            } 
         
            System.out.println( "Archivo Enviado: "+archivo.getName() );
            // Cerramos socket y flujos
            bis.close();
            bos.close();
            socket.close(); 
          }
          catch( Exception e ){
            System.out.println( e.toString() );
          } 
         }
         
         // Lanzamos nuestro cliente para realizar el envio del archivo calc.exe
         // ubicado en C:\Windows\calc.exe
         public static void main( String args[] ){
            EnviarArchivo ea = new EnviarArchivo( "Marcas.txt" );
            ea.enviarArchivo();
            System.out.println("Cliente envia el archivo Marcas.txt");
         }
         
}