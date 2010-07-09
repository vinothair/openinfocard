/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
 * Copyright (c) 2006, Axel Nennker - http://axel.nennker.de/
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
 *     * The names of the contributors may NOT be used to endorse or promote products
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
 * 
 */
package org.xmldap.sts.db;

import org.xmldap.exceptions.StorageException;

import java.util.List;


public interface CardStorage {
    void startup();

    public SupportedClaims getSupportedClaimsByCard(String cardId);
    void addAccount(String username, String password) throws StorageException;

    boolean authenticate(String uid, String password);

    void addCard(String username, ManagedCard card) throws StorageException;

    @SuppressWarnings("unchecked")
	List getCards(String username);

    ManagedCard getCard(String cardid);

    void shutdown();
    
    int getVersion();
}





//
//
//
//
//package org.xmldap.sts.db;
//
//import org.xmldap.exceptions.StorageException;
//
//import java.util.List;
//
//
//public interface CardStorage {
//    void startup();
//
//    void addAccount(String username, String password) throws StorageException;
//
//    boolean authenticate(String uid, String password);
//
//    void addCard(String username, ManagedCard card) throws StorageException;
//
//    List getCards(String username);
//
//    ManagedCard getCard(String cardid);
//
//    void shutdown();
//    
//    int getVersion();
//}
