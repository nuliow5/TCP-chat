package lt.gerasimovas.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clientHandlerList = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    //refactor as Class User
    private String clientUserName;

    //ClientHandler(Socket socket, User user)
    public ClientHandler(Socket socket) {

        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlerList.add(this);
            broadcastMessages("# SERVER: " + clientUserName + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessages(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }

        }
    }

    private void broadcastMessages(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlerList) {
            try {
                if (!clientHandler.equals(clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler (){
        clientHandlerList.remove(this);
        broadcastMessages("# SERVER: " + clientUserName + " left the chat");
    }

    private void closeEverything(
            Socket socket,
            BufferedReader bufferedReader,
            BufferedWriter bufferedWriter) {

        removeClientHandler();

//        if (!socket.isClosed()) {
//            socket.close();
//        }
        try{
            if (socket != null) {
                socket.close();
            }

            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

}
