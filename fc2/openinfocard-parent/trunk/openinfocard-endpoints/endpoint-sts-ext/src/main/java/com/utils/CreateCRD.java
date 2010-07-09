package com.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.awl.rd.fc2.CreateInfoCard;
import com.awl.rd.fc2.data.connectors.exceptions.CardNotFoundExecption;

public class CreateCRD {
	static  Logger log = Logger.getLogger(CreateCRD.class);
	static public void trace(Object msg){
		log.info(msg);
	}
	/**
	 * @param args
	 */
	public static void saveInFile(String strFilename,String xmlCard){
		File file = new File(strFilename);
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			out.write(xmlCard.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trace("Card saved in " + strFilename);
	}
	public static void exportCard(String userId) throws CardNotFoundExecption{
		String xmlCard=CreateInfoCard.getCRD(userId,0,CreateInfoCard.METHOD_BOTH);
		trace(xmlCard);
		
		saveInFile("C:/tempp/cards/testCard-"+userId+".crd", xmlCard);
		saveInFile("C:/tempp/cards/testCard-"+userId+".xml", xmlCard);
	}
	public static void run(){
		//exportCard("stef");
		try {
			exportCard("alex09");
		} catch (CardNotFoundExecption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		CreateCRD.run();

	}

}
