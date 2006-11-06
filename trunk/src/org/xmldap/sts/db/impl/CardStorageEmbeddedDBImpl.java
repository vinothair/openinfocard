package org.xmldap.sts.db.impl;

import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.util.XSDDateTime;
import org.xmldap.exceptions.StorageException;

import java.sql.*;
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

                s.execute("create table cards(cardid varchar(255) NOT NULL CONSTRAINT CARD_PK PRIMARY KEY, cardName varchar(48), cardVersion int, timeIssued varChar(50), timeExpires varChar(50), givenName varChar(50), surname varChar(50), emailAddress varChar(150), streetAddress varChar(50), locality varChar(50), stateOrProvince varChar(50), postalCode varChar(10), country varChar(50), primaryPhone varChar(50),  dateOfBirth varChar(50), gender  varChar(10))");
                System.out.println("Created table cards");

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

       Statement s = null;
       try {

           s = conn.createStatement();

           try {

//cardid, cardName , cardVersion, timeIssued , timeExpires, givenName, surname, emailAddress, streetAddress, locality, stateOrProvince, postalCode, country, primaryPhone,  dateOfBirth, privatePersonalIdentifier, gender


               //System.out.println("insert into cards values ('" + card.getPrivatePersonalIdentifier() +  "', '" + card.getCardName() +  "', " + card.getCardVersion() + ", '" + card.getTimeIssued() + "', '" + card.getTimeExpires() + "', '" + card.getGivenName() + "', '" + card.getSurname() + "', '" + card.getEmailAddress() + "', '" + card.getStreetAddress() + "', '" + card.getLocality() + "', '" + card.getStateOrProvince() + "', '" + card.getPostalCode() + "', '" + card.getCountry() + "', '" + card.getPrimaryPhone()  + "', '" + card.getDateOfBirth()  + "', '"  + card.getGender() + "')");
               s.execute("insert into cards values ('" + card.getPrivatePersonalIdentifier() +  "', '" + card.getCardName() +  "', " + card.getCardVersion() + ", '" + card.getTimeIssued() + "', '" + card.getTimeExpires() + "', '" + card.getGivenName() + "', '" + card.getSurname() + "', '" + card.getEmailAddress() + "', '" + card.getStreetAddress() + "', '" + card.getLocality() + "', '" + card.getStateOrProvince() + "', '" + card.getPostalCode() + "', '" + card.getCountry() + "', '" + card.getPrimaryPhone()  + "', '" + card.getDateOfBirth()  + "', '" + card.getGender() + "')");
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
                    card.setGivenName(rs.getString(6));
                    card.setSurname(rs.getString(7));
                    card.setEmailAddress(rs.getString(8));
                    card.setStreetAddress(rs.getString(9));
                    card.setLocality(rs.getString(10));
                    card.setStateOrProvince(rs.getString(11));
                    card.setPostalCode(rs.getString(12));
                    card.setCountry(rs.getString(13));
                    card.setPrimaryPhone(rs.getString(14));
                    card.setDateOfBirth(rs.getString(15));
                    card.setGender(rs.getString(16));

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
