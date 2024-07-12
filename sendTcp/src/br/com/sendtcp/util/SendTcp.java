package br.com.sendtcp.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SendTcp {

    private static final String filePath = "sendTcp.txt"; 
    private static final String ipAddress = "127.0.0.1";
    private static final int port = 38800; // Porta do destinatário

    private static long lastModifiedTime = 0;
    private static int lastSentCharCount = 0;
    private static Socket socket = null;
    private static DataOutputStream outputStream = null;

    public void executeSendTcp() {

        try {
            // Abre a conexão TCP
            socket = new Socket(ipAddress, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Abrindo a conexao TCP: " + ipAddress + ":" + port );
            System.out.println("");

            // Loop de verificação contínua
            while (true) {
                long currentModifiedTime = new File(filePath).lastModified();
                int currentCharCount = getCharacterCount(filePath);
                if (currentModifiedTime != lastModifiedTime || currentCharCount != lastSentCharCount) {
                    sendChanges(lastSentCharCount);
                    lastModifiedTime = currentModifiedTime;
                    lastSentCharCount = currentCharCount;
                } else {
                    System.out.println("Arquivo nao alterado. Aguardando alteracoes...");
                    Thread.sleep(2500); // Espera 2.5 segundos antes de verificar novamente
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERRO: " + e);
        } finally {
            // Fecha a conexão TCP
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERRO: " + e);
            }
        }
    }

    private static void sendChanges(int startIndex) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Ir para a posição correta no arquivo
            for (int i = 0; i < startIndex; i++) {
                bufferedReader.read();
            }

            // Enviar as alterações
            StringBuilder changes = new StringBuilder();
            int charCode;
            while ((charCode = bufferedReader.read()) != -1) {
                changes.append((char) charCode);
            }

            String message = changes.toString();
            outputStream.writeBytes(message);

            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();

            // Adiciona data e hora de envio
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm:ss"));
            System.out.println(currentTime + " - Enviado: " + message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERRO: " + e);
        }
    }

    private static int getCharacterCount(String filePath) throws IOException {
        int count = 0;
        FileInputStream fileInputStream = new FileInputStream(filePath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        while (inputStreamReader.read() != -1) {
            count++;
        }

        inputStreamReader.close();
        fileInputStream.close();

        return count;
    }
}
