package com.example.krata.treninky_denik;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.intentservice.chatui.models.ChatMessage;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author krata
 */
public class ClientSocket extends Thread
{
    Socket clientSocket;
    public String from = "";
    public String to = "";
    
    public ClientSocket(String from, String to) throws Exception
    {
        this.from = from;
        this.to = to;
        this.start();
    }

    public void writeToServer(String text)
    {
        ServerRequests serverRequests = new ServerRequests();
        serverRequests.sendMessage(text, clientSocket);
    }
    
    @Override
    public void run()
    {
        try {
            clientSocket = new Socket("40.127.191.146", 2343);
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeToServer(from + "/" + to);
        while(true)
        {
            try {
                BufferedReader inFromServer = null;
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String id = inFromServer.readLine();
                ChatMessage message = new ChatMessage(id, 0, ChatMessage.Type.RECEIVED);
//                JOptionPane.showMessageDialog(null, id, "InfoBox", JOptionPane.INFORMATION_MESSAGE);
                String subString = "";
            }
            catch (IOException ex)
            {
                Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
