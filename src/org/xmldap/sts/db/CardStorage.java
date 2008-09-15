package org.xmldap.sts.db;

import org.xmldap.exceptions.StorageException;

import java.util.List;


public interface CardStorage {
    void startup();

    void addAccount(String username, String password) throws StorageException;

    boolean authenticate(String uid, String password);

    void addCard(String username, ManagedCard card) throws StorageException;

    List getCards(String username);

    ManagedCard getCard(String cardid);

    void shutdown();
    
    int getVersion();
}
