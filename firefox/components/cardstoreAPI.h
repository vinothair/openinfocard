/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM cardstoreAPI.idl.idl
 */

#ifndef __gen_xpidl-idl20091127-9473-aue1ef-0_h__
#define __gen_xpidl-idl20091127-9473-aue1ef-0_h__


#ifndef __gen_nsISupports_h__
#include "nsISupports.h"
#endif

#ifndef __gen_nsIX509Cert_h__
#include "nsIX509Cert.h"
#endif

/* For IDL files that don't want to include root IDL files. */
#ifndef NS_NO_VTABLE
#define NS_NO_VTABLE
#endif

/* starting interface:    IInformationCardStore */
#define IINFORMATIONCARDSTORE_IID_STR "ddd9bc02-c964-4bd5-b5bc-943e483c6c58"

#define IINFORMATIONCARDSTORE_IID 
  {0xddd9bc02, 0xc964, 0x4bd5, 
    { 0xb5, 0xbc, 0x94, 0x3e, 0x48, 0x3c, 0x6c, 0x58 }}

class NS_NO_VTABLE IInformationCardStore : public nsISupports {
 public: 

  NS_DEFINE_STATIC_IID_ACCESSOR(IINFORMATIONCARDSTORE_IID)

  /* boolean login (in string credentials); */
  NS_IMETHOD Login(const char *credentials, PRBool *_retval) = 0;

  /* void logout (); */
  NS_IMETHOD Logout(void) = 0;

  /* boolean loggedIn (); */
  NS_IMETHOD LoggedIn(PRBool *_retval) = 0;

  /* void clearCardStore (); */
  NS_IMETHOD ClearCardStore(void) = 0;

  /* void addCard (in string informationCardXml); */
  NS_IMETHOD AddCard(const char *informationCardXml) = 0;

  /* void removeCard (in string cardId); */
  NS_IMETHOD RemoveCard(const char *cardId) = 0;

  /* void addCardsFromRoamingStore (in string roamingStoreXml); */
  NS_IMETHOD AddCardsFromRoamingStore(const char *roamingStoreXml) = 0;

  /* void updateCard (in string informationCardXml, in string cardId); */
  NS_IMETHOD UpdateCard(const char *informationCardXml, const char *cardId) = 0;

  /* void getAllCardIds (out unsigned long count, [array, size_is (count)] out string cardIds); */
  NS_IMETHOD GetAllCardIds(PRUint32 *count, char ***cardIds) = 0;

  /* unsigned long getCardCount (); */
  NS_IMETHOD GetCardCount(PRUint32 *_retval) = 0;

  /* string cardIdIterator (); */
  NS_IMETHOD CardIdIterator(char **_retval) = 0;

  /* string cardidIteratorNext (in string iterator); */
  NS_IMETHOD CardidIteratorNext(const char *iterator, char **_retval) = 0;

  /* boolean cardIdIteratorHasNext (in string iterator); */
  NS_IMETHOD CardIdIteratorHasNext(const char *iterator, PRBool *_retval) = 0;

  /* string cardStoreExportAllCards (in wstring password); */
  NS_IMETHOD CardStoreExportAllCards(const PRUnichar *password, char **_retval) = 0;

  /* string cardStoreExportCards (in wstring password, in unsigned long count, [array, size_is (count)] in string cardIds); */
  NS_IMETHOD CardStoreExportCards(const PRUnichar *password, PRUint32 count, const char **cardIds, char **_retval) = 0;

  /* string getMasterSecretForCard (in string cardId); */
  NS_IMETHOD GetMasterSecretForCard(const char *cardId, char **_retval) = 0;

  /* string getRpIdentifier (in string cardId, in nsIX509Cert relyingPartyCertificate); */
  NS_IMETHOD GetRpIdentifier(const char *cardId, nsIX509Cert *relyingPartyCertificate, char **_retval) = 0;

  /* string getCardByPPID (in string PPID, in nsIX509Cert relyingPartyCertificate); */
  NS_IMETHOD GetCardByPPID(const char *PPID, nsIX509Cert *relyingPartyCertificate, char **_retval) = 0;

  /* string getCardStoreName (); */
  NS_IMETHOD GetCardStoreName(char **_retval) = 0;

  /* string getCardStoreVersion (); */
  NS_IMETHOD GetCardStoreVersion(char **_retval) = 0;

  /* readonly attribute wstring errorstring; */
  NS_IMETHOD GetErrorstring(PRUnichar * *aErrorstring) = 0;

  /* readonly attribute long errornumber; */
  NS_IMETHOD GetErrornumber(PRInt32 *aErrornumber) = 0;

};

/* Use this macro when declaring classes that implement this interface. */
#define NS_DECL_IINFORMATIONCARDSTORE 
  NS_IMETHOD Login(const char *credentials, PRBool *_retval); 
  NS_IMETHOD Logout(void); 
  NS_IMETHOD LoggedIn(PRBool *_retval); 
  NS_IMETHOD ClearCardStore(void); 
  NS_IMETHOD AddCard(const char *informationCardXml); 
  NS_IMETHOD RemoveCard(const char *cardId); 
  NS_IMETHOD AddCardsFromRoamingStore(const char *roamingStoreXml); 
  NS_IMETHOD UpdateCard(const char *informationCardXml, const char *cardId); 
  NS_IMETHOD GetAllCardIds(PRUint32 *count, char ***cardIds); 
  NS_IMETHOD GetCardCount(PRUint32 *_retval); 
  NS_IMETHOD CardIdIterator(char **_retval); 
  NS_IMETHOD CardidIteratorNext(const char *iterator, char **_retval); 
  NS_IMETHOD CardIdIteratorHasNext(const char *iterator, PRBool *_retval); 
  NS_IMETHOD CardStoreExportAllCards(const PRUnichar *password, char **_retval); 
  NS_IMETHOD CardStoreExportCards(const PRUnichar *password, PRUint32 count, const char **cardIds, char **_retval); 
  NS_IMETHOD GetMasterSecretForCard(const char *cardId, char **_retval); 
  NS_IMETHOD GetRpIdentifier(const char *cardId, nsIX509Cert *relyingPartyCertificate, char **_retval); 
  NS_IMETHOD GetCardByPPID(const char *PPID, nsIX509Cert *relyingPartyCertificate, char **_retval); 
  NS_IMETHOD GetCardStoreName(char **_retval); 
  NS_IMETHOD GetCardStoreVersion(char **_retval); 
  NS_IMETHOD GetErrorstring(PRUnichar * *aErrorstring); 
  NS_IMETHOD GetErrornumber(PRInt32 *aErrornumber); 

/* Use this macro to declare functions that forward the behavior of this interface to another object. */
#define NS_FORWARD_IINFORMATIONCARDSTORE(_to) 
  NS_IMETHOD Login(const char *credentials, PRBool *_retval) { return _to Login(credentials, _retval); } 
  NS_IMETHOD Logout(void) { return _to Logout(); } 
  NS_IMETHOD LoggedIn(PRBool *_retval) { return _to LoggedIn(_retval); } 
  NS_IMETHOD ClearCardStore(void) { return _to ClearCardStore(); } 
  NS_IMETHOD AddCard(const char *informationCardXml) { return _to AddCard(informationCardXml); } 
  NS_IMETHOD RemoveCard(const char *cardId) { return _to RemoveCard(cardId); } 
  NS_IMETHOD AddCardsFromRoamingStore(const char *roamingStoreXml) { return _to AddCardsFromRoamingStore(roamingStoreXml); } 
  NS_IMETHOD UpdateCard(const char *informationCardXml, const char *cardId) { return _to UpdateCard(informationCardXml, cardId); } 
  NS_IMETHOD GetAllCardIds(PRUint32 *count, char ***cardIds) { return _to GetAllCardIds(count, cardIds); } 
  NS_IMETHOD GetCardCount(PRUint32 *_retval) { return _to GetCardCount(_retval); } 
  NS_IMETHOD CardIdIterator(char **_retval) { return _to CardIdIterator(_retval); } 
  NS_IMETHOD CardidIteratorNext(const char *iterator, char **_retval) { return _to CardidIteratorNext(iterator, _retval); } 
  NS_IMETHOD CardIdIteratorHasNext(const char *iterator, PRBool *_retval) { return _to CardIdIteratorHasNext(iterator, _retval); } 
  NS_IMETHOD CardStoreExportAllCards(const PRUnichar *password, char **_retval) { return _to CardStoreExportAllCards(password, _retval); } 
  NS_IMETHOD CardStoreExportCards(const PRUnichar *password, PRUint32 count, const char **cardIds, char **_retval) { return _to CardStoreExportCards(password, count, cardIds, _retval); } 
  NS_IMETHOD GetMasterSecretForCard(const char *cardId, char **_retval) { return _to GetMasterSecretForCard(cardId, _retval); } 
  NS_IMETHOD GetRpIdentifier(const char *cardId, nsIX509Cert *relyingPartyCertificate, char **_retval) { return _to GetRpIdentifier(cardId, relyingPartyCertificate, _retval); } 
  NS_IMETHOD GetCardByPPID(const char *PPID, nsIX509Cert *relyingPartyCertificate, char **_retval) { return _to GetCardByPPID(PPID, relyingPartyCertificate, _retval); } 
  NS_IMETHOD GetCardStoreName(char **_retval) { return _to GetCardStoreName(_retval); } 
  NS_IMETHOD GetCardStoreVersion(char **_retval) { return _to GetCardStoreVersion(_retval); } 
  NS_IMETHOD GetErrorstring(PRUnichar * *aErrorstring) { return _to GetErrorstring(aErrorstring); } 
  NS_IMETHOD GetErrornumber(PRInt32 *aErrornumber) { return _to GetErrornumber(aErrornumber); } 

/* Use this macro to declare functions that forward the behavior of this interface to another object in a safe way. */
#define NS_FORWARD_SAFE_IINFORMATIONCARDSTORE(_to) 
  NS_IMETHOD Login(const char *credentials, PRBool *_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->Login(credentials, _retval); } 
  NS_IMETHOD Logout(void) { return !_to ? NS_ERROR_NULL_POINTER : _to->Logout(); } 
  NS_IMETHOD LoggedIn(PRBool *_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->LoggedIn(_retval); } 
  NS_IMETHOD ClearCardStore(void) { return !_to ? NS_ERROR_NULL_POINTER : _to->ClearCardStore(); } 
  NS_IMETHOD AddCard(const char *informationCardXml) { return !_to ? NS_ERROR_NULL_POINTER : _to->AddCard(informationCardXml); } 
  NS_IMETHOD RemoveCard(const char *cardId) { return !_to ? NS_ERROR_NULL_POINTER : _to->RemoveCard(cardId); } 
  NS_IMETHOD AddCardsFromRoamingStore(const char *roamingStoreXml) { return !_to ? NS_ERROR_NULL_POINTER : _to->AddCardsFromRoamingStore(roamingStoreXml); } 
  NS_IMETHOD UpdateCard(const char *informationCardXml, const char *cardId) { return !_to ? NS_ERROR_NULL_POINTER : _to->UpdateCard(informationCardXml, cardId); } 
  NS_IMETHOD GetAllCardIds(PRUint32 *count, char ***cardIds) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetAllCardIds(count, cardIds); } 
  NS_IMETHOD GetCardCount(PRUint32 *_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetCardCount(_retval); } 
  NS_IMETHOD CardIdIterator(char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->CardIdIterator(_retval); } 
  NS_IMETHOD CardidIteratorNext(const char *iterator, char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->CardidIteratorNext(iterator, _retval); } 
  NS_IMETHOD CardIdIteratorHasNext(const char *iterator, PRBool *_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->CardIdIteratorHasNext(iterator, _retval); } 
  NS_IMETHOD CardStoreExportAllCards(const PRUnichar *password, char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->CardStoreExportAllCards(password, _retval); } 
  NS_IMETHOD CardStoreExportCards(const PRUnichar *password, PRUint32 count, const char **cardIds, char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->CardStoreExportCards(password, count, cardIds, _retval); } 
  NS_IMETHOD GetMasterSecretForCard(const char *cardId, char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetMasterSecretForCard(cardId, _retval); } 
  NS_IMETHOD GetRpIdentifier(const char *cardId, nsIX509Cert *relyingPartyCertificate, char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetRpIdentifier(cardId, relyingPartyCertificate, _retval); } 
  NS_IMETHOD GetCardByPPID(const char *PPID, nsIX509Cert *relyingPartyCertificate, char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetCardByPPID(PPID, relyingPartyCertificate, _retval); } 
  NS_IMETHOD GetCardStoreName(char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetCardStoreName(_retval); } 
  NS_IMETHOD GetCardStoreVersion(char **_retval) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetCardStoreVersion(_retval); } 
  NS_IMETHOD GetErrorstring(PRUnichar * *aErrorstring) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetErrorstring(aErrorstring); } 
  NS_IMETHOD GetErrornumber(PRInt32 *aErrornumber) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetErrornumber(aErrornumber); } 

#if 0
/* Use the code below as a template for the implementation class for this interface. */

/* Header file */
class _MYCLASS_ : public IInformationCardStore
{
public:
  NS_DECL_ISUPPORTS
  NS_DECL_IINFORMATIONCARDSTORE

  _MYCLASS_();

private:
  ~_MYCLASS_();

protected:
  /* additional members */
};

/* Implementation file */
NS_IMPL_ISUPPORTS1(_MYCLASS_, IInformationCardStore)

_MYCLASS_::_MYCLASS_()
{
  /* member initializers and constructor code */
}

_MYCLASS_::~_MYCLASS_()
{
  /* destructor code */
}

/* boolean login (in string credentials); */
NS_IMETHODIMP _MYCLASS_::Login(const char *credentials, PRBool *_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void logout (); */
NS_IMETHODIMP _MYCLASS_::Logout()
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* boolean loggedIn (); */
NS_IMETHODIMP _MYCLASS_::LoggedIn(PRBool *_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void clearCardStore (); */
NS_IMETHODIMP _MYCLASS_::ClearCardStore()
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void addCard (in string informationCardXml); */
NS_IMETHODIMP _MYCLASS_::AddCard(const char *informationCardXml)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void removeCard (in string cardId); */
NS_IMETHODIMP _MYCLASS_::RemoveCard(const char *cardId)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void addCardsFromRoamingStore (in string roamingStoreXml); */
NS_IMETHODIMP _MYCLASS_::AddCardsFromRoamingStore(const char *roamingStoreXml)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void updateCard (in string informationCardXml, in string cardId); */
NS_IMETHODIMP _MYCLASS_::UpdateCard(const char *informationCardXml, const char *cardId)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void getAllCardIds (out unsigned long count, [array, size_is (count)] out string cardIds); */
NS_IMETHODIMP _MYCLASS_::GetAllCardIds(PRUint32 *count, char ***cardIds)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* unsigned long getCardCount (); */
NS_IMETHODIMP _MYCLASS_::GetCardCount(PRUint32 *_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string cardIdIterator (); */
NS_IMETHODIMP _MYCLASS_::CardIdIterator(char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string cardidIteratorNext (in string iterator); */
NS_IMETHODIMP _MYCLASS_::CardidIteratorNext(const char *iterator, char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* boolean cardIdIteratorHasNext (in string iterator); */
NS_IMETHODIMP _MYCLASS_::CardIdIteratorHasNext(const char *iterator, PRBool *_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string cardStoreExportAllCards (in wstring password); */
NS_IMETHODIMP _MYCLASS_::CardStoreExportAllCards(const PRUnichar *password, char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string cardStoreExportCards (in wstring password, in unsigned long count, [array, size_is (count)] in string cardIds); */
NS_IMETHODIMP _MYCLASS_::CardStoreExportCards(const PRUnichar *password, PRUint32 count, const char **cardIds, char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string getMasterSecretForCard (in string cardId); */
NS_IMETHODIMP _MYCLASS_::GetMasterSecretForCard(const char *cardId, char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string getRpIdentifier (in string cardId, in nsIX509Cert relyingPartyCertificate); */
NS_IMETHODIMP _MYCLASS_::GetRpIdentifier(const char *cardId, nsIX509Cert *relyingPartyCertificate, char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string getCardByPPID (in string PPID, in nsIX509Cert relyingPartyCertificate); */
NS_IMETHODIMP _MYCLASS_::GetCardByPPID(const char *PPID, nsIX509Cert *relyingPartyCertificate, char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string getCardStoreName (); */
NS_IMETHODIMP _MYCLASS_::GetCardStoreName(char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* string getCardStoreVersion (); */
NS_IMETHODIMP _MYCLASS_::GetCardStoreVersion(char **_retval)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* readonly attribute wstring errorstring; */
NS_IMETHODIMP _MYCLASS_::GetErrorstring(PRUnichar * *aErrorstring)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* readonly attribute long errornumber; */
NS_IMETHODIMP _MYCLASS_::GetErrornumber(PRInt32 *aErrornumber)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* End of implementation class template. */
#endif


#endif /* __gen_xpidl-idl20091127-9473-aue1ef-0_h__ */
