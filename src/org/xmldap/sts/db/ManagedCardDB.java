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


package org.xmldap.sts.db;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import java.util.Collection;
import java.util.Iterator;

public class ManagedCardDB {
    private static ManagedCardDB ourInstance = new ManagedCardDB();
    private static ObjectContainer db;
    private static boolean initialized = false;

    public static ManagedCardDB getInstance() {
        if (!initialized)  init();
        return ourInstance;
    }

    private ManagedCardDB() {
    }


    private static void init(){

        db = Db4o.openFile("/home/cmort/carddb.dbo");
        initialized = true;

    }

    public void shutdown(){
        db.close();
        initialized = false;
    }


    public void updateAccount(Account account) {
        db.set(account);
    }

    public void addAccount(Account account ) {

        db.set(account);

    }


    public Account authenticate(String uid, String password) {

        Account proto = new Account(uid,password);
        ObjectSet result = db.get(proto);
        Account toReturn = null;
        if (result.hasNext()) {
            toReturn = (Account) result.next();
        }
        return toReturn;

    }


    public static void main(String[] args) {

        ManagedCardDB db = ManagedCardDB.getInstance();

        Account account = new Account("cmort", "password");
        ManagedCard card = new ManagedCard();
        ManagedCard card2 = new ManagedCard();
        account.addCard(card);
        account.addCard(card2);

        db.addAccount(account);
        db.shutdown();

        db = ManagedCardDB.getInstance();
        Account account1 = db.authenticate("cmort","password");
        Collection cards = account1.getCards();
        Iterator iter = cards.iterator();
        while (iter.hasNext()) {

            ManagedCard thisCard = (ManagedCard)iter.next();
            System.out.println(thisCard.getCardId());

        }
        db.shutdown();


    }


}
