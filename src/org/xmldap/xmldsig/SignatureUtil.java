/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



package org.xmldap.xmldsig;

import nu.xom.Builder;
import nu.xom.Document;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.SigningException;
import org.xmldap.util.KeystoreUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class SignatureUtil {
    public static void main(String[] args) {
        SigningFrame frame = new SigningFrame();
    }
}


class SigningFrame extends JFrame implements ActionListener {

    JPanel signingPanel;
    JPanel inputs;
    JLabel fileLabel;
    JTextField file;
    JLabel jksLabel;
    JTextField jks;
    JLabel storePassLabel;
    JTextField storePass;
    JLabel aliasLabel;
    JTextField alias;
    JLabel keyPassLabel;
    JTextField keyPass;
    JTextArea signedXMLTextArea;
    JTextArea xmlTextArea;
    JButton signButton;
    JButton loadButton;
    Document xml;


    public SigningFrame() {
        super("Enveloped Signature Utility");

        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        signingPanel = new JPanel(new BorderLayout());
        inputs = new JPanel(new GridLayout(3, 1));

        file = new JTextField(40);
        file.setText("./test.xml");
        fileLabel = new JLabel("File: ");

        jks = new JTextField(40);
        jks.setText("./xmldap.jks");
        jksLabel = new JLabel("Keystore: ");

        storePass = new JTextField(40);
        storePass.setText("storepassword");
        storePassLabel = new JLabel("Keystore Password: ");

        alias = new JTextField(40);
        alias.setText("xmldap");
        aliasLabel = new JLabel("Alias: ");

        keyPass = new JTextField(40);
        keyPass.setText("keypassword");
        keyPassLabel = new JLabel("Key Password: ");


        xmlTextArea = new JTextArea(20, 20);
        signedXMLTextArea = new JTextArea(20, 20);
        signButton = new JButton("Sign XML");
        signButton.addActionListener(this);
        loadButton = new JButton("Load XML");
        loadButton.addActionListener(this);

        Panel box = new Panel(new BorderLayout());
        box.add(fileLabel, BorderLayout.WEST);
        box.add(file, BorderLayout.CENTER);
        inputs.add(box);

        box = new Panel(new BorderLayout());
        box.add(jksLabel, BorderLayout.WEST);
        box.add(jks, BorderLayout.CENTER);
        inputs.add(box);


        box = new Panel(new BorderLayout());
        box.add(storePassLabel, BorderLayout.WEST);
        box.add(storePass, BorderLayout.CENTER);
        inputs.add(box);


        box = new Panel(new BorderLayout());
        box.add(aliasLabel, BorderLayout.WEST);
        box.add(alias, BorderLayout.CENTER);
        inputs.add(box);


        box = new Panel(new BorderLayout());
        box.add(keyPassLabel, BorderLayout.WEST);
        box.add(keyPass, BorderLayout.CENTER);
        inputs.add(box);


        box = new Panel(new BorderLayout());
        box.add(loadButton, BorderLayout.WEST);
        box.add(signButton, BorderLayout.EAST);
        inputs.add(box);

        signingPanel.add(inputs, BorderLayout.NORTH);
        signingPanel.add(new JScrollPane(xmlTextArea), BorderLayout.CENTER);
        signingPanel.add(new JScrollPane(signedXMLTextArea), BorderLayout.SOUTH);
        signingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setContentPane(signingPanel);
        this.pack();

        this.setVisible(true);
        this.setSize(800, 600);


    }


    public void actionPerformed(ActionEvent event) {


        if (event.getSource() == loadButton) {

            Builder xmlParser = new Builder();
            try {

                xml = xmlParser.build(new File(file.getText()));
                xmlTextArea.setText(xml.toXML());
                signedXMLTextArea.setText("");

            } catch (Exception e) {
                xmlTextArea.setText("Error:\n" + e.toString());
            }

        }

        if (event.getSource() == signButton) {

            KeystoreUtil keystore = null;
            try {
                keystore = new KeystoreUtil(jks.getText(), storePass.getText());
            } catch (KeyStoreException e) {
                signedXMLTextArea.setText("Error:\n" + e.toString());
            }

            EnvelopedSignature signer = new EnvelopedSignature(keystore, alias.getText(), keyPass.getText());
            try {
                Document signedDoc = signer.sign(xml);
                signedXMLTextArea.setText(signedDoc.toXML());

            } catch (SigningException e) {
                signedXMLTextArea.setText("Error:\n" + e.toString());
            }

        }
    }

}
