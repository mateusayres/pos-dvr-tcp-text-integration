package br.com.receivertcp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverTcp {

    private static final int port = 38800; // Porta na qual o servidor irá escutar

    public void executeReceiverTcp() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        BufferedReader inputStream = null;

        try {
            // Abre o socket do servidor
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor TCP em execucao e aguardando conexoes na porta " + port + "...");

            // Aceita conexões de clientes
            socket = serverSocket.accept();
            System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());

            // Cria um leitor para receber os dados do cliente
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lê os dados recebidos e imprime no console por caracteres
            System.out.println(" ");
            System.out.println("MENSAGEM RECEBIDA:");
            System.out.println(" ");
            int receivedChar;
            while ((receivedChar = inputStream.read()) != -1) {
                System.out.print((char) receivedChar); // imprime caractere recebido sem quebra de linha
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERRO: " + e);
        } finally {
            // Fecha as conexões
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERRO: " + e);
            }
        }
    }
}
