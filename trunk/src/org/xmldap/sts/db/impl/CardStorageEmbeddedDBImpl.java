package org.xmldap.sts.db.impl;

import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.DbDisplayTag;
import org.xmldap.sts.db.DbSupportedClaim;
import org.xmldap.sts.db.DbSupportedClaims;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.exceptions.StorageException;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class CardStorageEmbeddedDBImpl implements CardStorage {

    public String framework = "embedded";
    public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public String protocol = "jdbc:derby:";

    private Connection conn = null;

    private boolean initialized = false;
    
    
    private String claimsDefinition() {
    	List<DbSupportedClaim>dbSupportedClaims = DbSupportedClaims.dbSupportedClaims();
    	if (dbSupportedClaims.size() > 0) {
	    	StringBuffer claimsDefinition = new StringBuffer();
    		claimsDefinition.append(",");
	    	for (int i=0; i<dbSupportedClaims.size()-1; i++) {
	    		claimsDefinition.append(" ");
	    		DbSupportedClaim claim =  dbSupportedClaims.get(i);
	    		claimsDefinition.append(claim.columnName);
	    		claimsDefinition.append(" ");
	    		claimsDefinition.append(claim.columnType);
	    		claimsDefinition.append(",");
	    	}
			claimsDefinition.append(" ");
			claimsDefinition.append(dbSupportedClaims.get(dbSupportedClaims.size()-1).columnName);
			claimsDefinition.append(" ");
			claimsDefinition.append(dbSupportedClaims.get(dbSupportedClaims.size()-1).columnType);
	    	return claimsDefinition.toString();
    	} else {
    		System.out.println("STS supported claims list is empty!!!");
    		return "";
    	}
    }
    
    private void createTableCards(Statement s) throws SQLException {
		String claimsDefinition = claimsDefinition();
//			" givenName varChar(50)," + 
//			" surname varChar(50)," + 
//			" emailAddress varChar(150)," + 
//			" streetAddress varChar(50)," + 
//			" locality varChar(50)," + 
//			" stateOrProvince varChar(50)," + 
//			" postalCode varChar(10)," + 
//			" country varChar(50)," + 
//			" primaryPhone varChar(50)," + 
//			" dateOfBirth varChar(50)," + 
//			" gender  varChar(10))";

		String query = "create table cards(" + 
		"cardid varchar(255) NOT NULL CONSTRAINT CARD_PK PRIMARY KEY," + 
		" cardName varchar(48)," + 
		" cardVersion int," + 
		" timeIssued varChar(50)," + 
		" timeExpires varChar(50)" + 
		claimsDefinition + ")";
		System.out.println(query);
        s.execute(query);
        System.out.println("Created table cards");
    }
    
    public void startup(){

        try {

            Class.forName(driver).newInstance();
            //System.out.println("Loaded the appropriate driver.");

            Properties props = new Properties();
            conn = DriverManager.getConnection(protocol + "cardDB;create=true", props);

            System.out.println("Connected to cardDB");

            conn.setAutoCommit(false);
            Statement s = conn.createStatement();
            ResultSet rs = null;

            try {
                rs = s.executeQuery( "SELECT init FROM initialized");
                if (rs.next()){
                    System.out.println("CardDB initialized");
                    initialized = true;
                }

            } catch (SQLException e) {

                System.out.println("Not initialized - creating card tables");


                s.execute("create table accounts(username varchar(48) NOT NULL CONSTRAINT ACCOUNT_PK PRIMARY KEY, password varchar(48))");
                System.out.println("Created table accounts");

                createTableCards(s);

                s.execute("create table account_cards(username varchar(48), cardid varchar(255))");
                //TODO - figure out the foreign key constraints
                //s.execute("alter table account_cards ADD CONSTRAINT USERNAME_FK FOREIGN KEY (username) REFERENCES accounts (username)");
                //s.execute("alter table account_cards ADD CONSTRAINT CARD_FK FOREIGN KEY (cardid) REFERENCES cards (cardid)");
                System.out.println("Created table account_cards");


                s.execute("create table initialized(init int, version int)");
                s.execute("insert into initialized values (1, 1)");
                System.out.println("Card DB Initialized");


            } finally {
                s.close();
                conn.commit();
                //System.out.println("Closed result set and statement");

            }


       } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void addAccount(String username, String password) throws StorageException{
    	if (conn == null) {
    		startup();
    	}

        Statement s = null;
        try {
            s = conn.createStatement();


            try {

                s.execute("insert into accounts values ('" + username + "', '" + password + "')");
                s.close();
                conn.commit();
            } catch (SQLException e) {

                //eat the exception for now
                //TODO - add an exception pattern
                System.out.println("Account creation failed");
                throw new StorageException("Account creation failed",e);

            }finally {

                s.close();
                conn.commit();

            }

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



    }

    public boolean authenticate(String uid, String password){
    	if (conn == null) {
    		startup();
    	}

        boolean authenticated = false;


        Statement s = null;
        try {
            s = conn.createStatement();


            try {

                ResultSet rs = s.executeQuery("SELECT password FROM accounts where username = '" + uid + "'");
                if (!rs.next()) return authenticated;
                String pw = rs.getString(1);
                if ( pw.equals(password)) authenticated = true;
                s.close();
                conn.commit();
            } catch (SQLException e) {

                //eat the exception for now
                //TODO - add an exception pattern
                e.printStackTrace();

            }finally {

                s.close();
                conn.commit();

            }

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return authenticated;
    }

    public void addCard(String username, ManagedCard card){
    	if (conn == null) {
    		startup();
    	}

       Statement s = null;
       try {

           s = conn.createStatement();

           try {

//cardid, cardName , cardVersion, timeIssued , timeExpires, givenName, surname, emailAddress, streetAddress, locality, stateOrProvince, postalCode, country, primaryPhone,  dateOfBirth, privatePersonalIdentifier, gender


               //System.out.println("insert into cards values ('" + card.getPrivatePersonalIdentifier() +  "', '" + card.getCardName() +  "', " + card.getCardVersion() + ", '" + card.getTimeIssued() + "', '" + card.getTimeExpires() + "', '" + card.getGivenName() + "', '" + card.getSurname() + "', '" + card.getEmailAddress() + "', '" + card.getStreetAddress() + "', '" + card.getLocality() + "', '" + card.getStateOrProvince() + "', '" + card.getPostalCode() + "', '" + card.getCountry() + "', '" + card.getPrimaryPhone()  + "', '" + card.getDateOfBirth()  + "', '"  + card.getGender() + "')");
        	   StringBuffer insert = new StringBuffer("insert into cards values ('"); 
        	   insert.append(card.getPrivatePersonalIdentifier());
        	   insert.append("', '"); 
        	   insert.append(card.getCardName());
        	   insert.append("', "); 
           	   insert.append(card.getCardVersion()); 
        	   insert.append(", '"); 
           	   insert.append(card.getTimeIssued());
        	   insert.append("', '"); 
        	   insert.append(card.getTimeExpires());
        	   insert.append("'");
        	   
        	   List<DbSupportedClaim> dbSupportedClaims = DbSupportedClaims.dbSupportedClaims();
        	   for (int i=0; i<dbSupportedClaims.size(); i++) {
        		   DbSupportedClaim claim = dbSupportedClaims.get(i);
        		   boolean isString = (claim.columnType.indexOf("varChar") > -1);
        		   insert.append(", ");
        		   if (isString) insert.append("'");
        		   insert.append(card.getClaim(claim.uri));
        		   if (isString) insert.append("'");
        	   }

        	   insert.append(")");
        	   System.out.println(insert.toString());
               s.execute(insert.toString());
               //System.out.println("insert into account_cards values ('" + username + "', '" + card.getCardId()  +"')");
               s.execute("insert into account_cards values ('" + username + "', '" + card.getCardId()  +"')");
               s.close();
               conn.commit();

           } catch (SQLException e) {

               //TODO - add an exception pattern
               System.out.println("Card creation failed");
               e.printStackTrace();

           }finally {

               s.close();
               conn.commit();

           }

       } catch (SQLException e) {
           e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
       }


    }

    public List getCards(String username){
    	if (conn == null) {
    		startup();
    	}
        Vector cardIds = new Vector();
        Statement s = null;
        try {
            s = conn.createStatement();


            try {

                ResultSet rs = s.executeQuery("SELECT * FROM account_cards where username = '" + username + "'");
                while (rs.next()){
                    String id = rs.getString(2);
                    cardIds.add(id);
                }
                rs.close();
                s.close();
                conn.commit();
            } catch (SQLException e) {

                //eat the exception for now
                //TODO - add an exception pattern
                e.printStackTrace();

            }finally {
                s.close();
                conn.commit();

            }

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return cardIds;

    }

    public ManagedCard getCard(String cardid){
    	if (conn == null) {
    		startup();
    	}

        ManagedCard card = null;
        Statement s = null;
        try {
            s = conn.createStatement();
            try {

                ResultSet rs = s.executeQuery("SELECT * FROM cards where cardid = '" + cardid + "'");
                while (rs.next()){

                    card = new ManagedCard(rs.getString(1));
                    card.setCardName(rs.getString(2));
                    card.setCardVersion(rs.getInt(3));
                    card.setTimeIssued(rs.getString(4));
                    card.setTimeExpires(rs.getString(5));
                    
                    List<DbSupportedClaim> dbSupportedClaims = DbSupportedClaims.dbSupportedClaims();
                    for (int i=0; i<dbSupportedClaims.size(); i++) {
                    	String value = rs.getString(i+6);
                    	if ((value != null) && (!"".equals(value)) && (!"null".equals(value))) {
                    		System.out.println("cardId:"+cardid+" claim:"+value);
                    		card.setClaim(dbSupportedClaims.get(i).uri, value);
                    	}
                    }
                }
                rs.close();
                s.close();
                conn.commit();
            } catch (SQLException e) {

                //eat the exception for now
                //TODO - add an exception pattern
                e.printStackTrace();

            }finally {
                s.close();
                conn.commit();

            }

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return card;

    }

    public void shutdown() {
    	if (conn == null) return;
    	
        try {
            conn.close();
            System.out.println("Embedded CardDB shutdown");
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    /*
    public static void main(String[] args) {

        CardStorage storage = new CardStorageEmbeddedDBImpl();
        storage.startup();
        storage.addAccount("cmort1", "password");
        boolean authn = storage.authenticate("cmort", "password1");
        System.out.println(authn);


        XSDDateTime issued = new XSDDateTime();
        XSDDateTime expires = new XSDDateTime(525600);


        ManagedCard card = new ManagedCard();
        card.setGivenName("cmort");
        card.setSurname("motimore");
        card.setEmailAddress("cmort@xmldap.org");
        card.setCardName("My Card");
        card.setTimeIssued(issued.getDateTime());
        card.setTimeExpires(expires.getDateTime());
        storage.addCard("cmort", card);

        List cardIds = storage.getCards("cmort");
        Iterator ids = cardIds.iterator();
        while (ids.hasNext()){
            String cardId = (String) ids.next();
            ManagedCard thisCard = storage.getCard(cardId);
            System.out.println(thisCard.getCardId());
        }



        storage.shutdown();

    }
    */

}
