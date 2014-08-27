package javaapplication1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Servidor {

    private int puerto;
    private ServerSocket server;
    private ObjectInputStream input;
    private Socket socket;
    private ObjectOutputStream out;
    private String volumeValue;
    private String url;

    public Servidor(int puerto) {
        this.puerto = puerto;
    }

    public void listen() throws IOException {
        boolean done = false;
        try {
            server = new ServerSocket(puerto);
            System.out.println("Servidor inciado en el puerto: " + puerto
                    + "; Direccion ip: " + Inet4Address.getLocalHost());
            socket = server.accept();
            input = new ObjectInputStream(socket.getInputStream());
            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()),1024);
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Cliente Aceptado" + socket);
            enviarMensaje("Servidor Conectado");

            while (!done) {
                String mensaje = getMessageFrontClient();
                System.out.println("Mensaje de Cliente: " + mensaje);
                if (!mensaje.equals("")) {
                    if (mensaje.equals("bye")) {
                        done = true;
                    } else if (mensaje.equals("play")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Spotify\" to playpause"};
                        ejecutaAppleScriptComand(args);
                        sendArtistAndTrackName();
                    } else if (mensaje.equals("next")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Spotify\" to next track"};
                        ejecutaAppleScriptComand(args);
                        sendArtistAndTrackName();
                    } else if (mensaje.equals("back")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Spotify\" to previous track"};
                        ejecutaAppleScriptComand(args);
                        sendArtistAndTrackName();
                    } else if (mensaje.equals("volumen")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Spotify\" to set sound volume to " + volumeValue};
                        ejecutaAppleScriptComand(args);
                    } else if (mensaje.equalsIgnoreCase("url")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Safari\" to active"};
                        ejecutaAppleScriptComand(args);
                        args = new String[]{"osascript", "-e",
                            "tell app \"Safari\" to open location  \"" + url + "\""};
                        ejecutaAppleScriptComand(args);
                    } else if (mensaje.equalsIgnoreCase("safariBack")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Safari\" to do JavaScript \"history.go(-1)\" in the document of window 1"};
                        ejecutaAppleScriptComand(args);
                    } else if (mensaje.equalsIgnoreCase("safariForward")) {
                        String[] args = {"osascript", "-e",
                            "tell app \"Safari\" to do JavaScript \"history.go(+1)\" in the document of window 1"};
                        ejecutaAppleScriptComand(args);
                    } else if (mensaje.equalsIgnoreCase("safariRefresh")) {
                        String s = getSafariActualUrl();
                        if (s != null) {
                            String[] args = {"osascript", "-e",
                                "tell app \"Safari\" to open location  \"" + s + "\""};
                            ejecutaAppleScriptComand(args);
                            System.out.println("URL  to refresh " + s);
                        }
                    } else if (mensaje.equalsIgnoreCase("safariScrollDown")) {
                        try {
                            Robot r = new Robot();
                            r.mouseWheel(15);

                        } catch (Exception e) {
                        }

                    } else if (mensaje.equalsIgnoreCase("safariScrollUp")) {

                        try {
                            Robot r = new Robot();
                            r.mouseWheel(-15);
        

                        } catch (Exception e) {
                        }
                        // r.mousePress(InputEvent.);
                    } else if (mensaje.equalsIgnoreCase("nextSlide")) {

                        try {
                            Robot r = new Robot();


                            r.keyPress(KeyEvent.VK_DOWN);

                            r.keyRelease(KeyEvent.VK_DOWN);

                        } catch (Exception e) {
                        }

                    }else if (mensaje.equalsIgnoreCase("previousSlide")) {

                        try {
                            Robot r = new Robot();


                            r.keyPress(KeyEvent.VK_UP);

                            r.keyRelease(KeyEvent.VK_UP);

                        } catch (Exception e) {
                        }

                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Hubo un Error: " + e.toString());
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    public String getMessageFrontClient() {
        String orden = "";
        try {
            String s = (String) input.readObject();

            orden = s;
            for (int i = 0; i < s.length(); i++) {
                if (Character.toString(s.charAt(i)).equals(";")) {
                    orden = s.substring(0, i);
                    if (orden.equals("volumen")) {
                        volumeValue = s.substring(i + 1, s.length() - 1);
                        System.out.println("Volumen to " + volumeValue);
                    } else if (orden.equals("url")) {
                        url = s.substring(i + 1, s.length() - 1);
                        System.out.println("URL " + url);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
        } catch (IOException e) {
        }

        return orden;

    }

    public void sendArtistAndTrackName() {
        String args[];
        args = new String[]{"osascript", "-e",
            "tell app \"Spotify\" to set trackname to name of current track"};
        String response = ejecturarAppleScriptComandConRespuesta(args);
        if (response != null) {
            String x = "Cancion: " + response;
            response = x;

        }
        args = new String[]{"osascript", "-e",
            "tell app \"Spotify\" to set trackname to artist of current track"};
        String x = ejecturarAppleScriptComandConRespuesta(args);
        if (x != null) {
            response += "; Artista: " + x;
        }
        if (response != null) {
            enviarMensaje(response);
        }

    }

    public String getSafariActualUrl() {
        String[] args = new String[]{"osascript", "-e",
            "tell app \"Safari\" to set docUrl to URL of document 1"};
        String x = ejecturarAppleScriptComandConRespuesta(args);
        return x;

    }

    public void ejecutaAppleScriptComand(String[] commands) {
        String m = null;
        Process process;
        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(commands);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String ejecturarAppleScriptComandConRespuesta(String[] commands) {
        Process process;
        String response = null;
        Runtime runtime = Runtime.getRuntime();

        try {
            process = runtime.exec(commands);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            BufferedReader bufferedReaderError = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            response = bufferedReader.readLine();
            String Error = bufferedReaderError.readLine();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }

    public void enviarMensaje(String s) {
        try {

            out.writeObject(s);
            // TODO Auto-generated catch block
            System.out.println("Mensaje a Enviar a CLiente : " + s);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Hubo un Error al enviar mensaje: "
                    + e.toString());
            e.printStackTrace();
        }
    }
}
