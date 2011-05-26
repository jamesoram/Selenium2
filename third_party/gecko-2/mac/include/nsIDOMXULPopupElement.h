/*
 * DO NOT EDIT.  THIS FILE IS GENERATED FROM /builds/slave/rel-2.0-xr-osx64-bld/build/dom/interfaces/xul/nsIDOMXULPopupElement.idl
 */

#ifndef __gen_nsIDOMXULPopupElement_h__
#define __gen_nsIDOMXULPopupElement_h__


#ifndef __gen_nsIDOMElement_h__
#include "nsIDOMElement.h"
#endif

#ifndef __gen_nsIDOMXULElement_h__
#include "nsIDOMXULElement.h"
#endif

/* For IDL files that don't want to include root IDL files. */
#ifndef NS_NO_VTABLE
#define NS_NO_VTABLE
#endif

/* starting interface:    nsIDOMXULPopupElement */
#define NS_IDOMXULPOPUPELEMENT_IID_STR "d647cb9a-aa67-4549-9219-b0345fcefe67"

#define NS_IDOMXULPOPUPELEMENT_IID \
  {0xd647cb9a, 0xaa67, 0x4549, \
    { 0x92, 0x19, 0xb0, 0x34, 0x5f, 0xce, 0xfe, 0x67 }}

class NS_NO_VTABLE NS_SCRIPTABLE nsIDOMXULPopupElement : public nsIDOMXULElement {
 public: 

  NS_DECLARE_STATIC_IID_ACCESSOR(NS_IDOMXULPOPUPELEMENT_IID)

  enum { BEFORE_START = 1U };

  enum { BEFORE_END = 2U };

  enum { AFTER_START = 3U };

  enum { AFTER_END = 4U };

  enum { START_BEFORE = 5U };

  enum { START_AFTER = 6U };

  enum { END_BEFORE = 7U };

  enum { END_AFTER = 8U };

  enum { OVERLAP = 9U };

  enum { AT_POINTER = 10U };

  enum { AFTER_POINTER = 11U };

  /* attribute DOMString position; */
  NS_SCRIPTABLE NS_IMETHOD GetPosition(nsAString & aPosition) = 0;
  NS_SCRIPTABLE NS_IMETHOD SetPosition(const nsAString & aPosition) = 0;

  /* void showPopup (in unsigned short alignment, in nsIDOMElement target, in nsIDOMElement anchor); */
  NS_SCRIPTABLE NS_IMETHOD ShowPopup(PRUint16 alignment, nsIDOMElement *target, nsIDOMElement *anchor) = 0;

  /* void hidePopup (); */
  NS_SCRIPTABLE NS_IMETHOD HidePopup(void) = 0;

};

  NS_DEFINE_STATIC_IID_ACCESSOR(nsIDOMXULPopupElement, NS_IDOMXULPOPUPELEMENT_IID)

/* Use this macro when declaring classes that implement this interface. */
#define NS_DECL_NSIDOMXULPOPUPELEMENT \
  NS_SCRIPTABLE NS_IMETHOD GetPosition(nsAString & aPosition); \
  NS_SCRIPTABLE NS_IMETHOD SetPosition(const nsAString & aPosition); \
  NS_SCRIPTABLE NS_IMETHOD ShowPopup(PRUint16 alignment, nsIDOMElement *target, nsIDOMElement *anchor); \
  NS_SCRIPTABLE NS_IMETHOD HidePopup(void); 

/* Use this macro to declare functions that forward the behavior of this interface to another object. */
#define NS_FORWARD_NSIDOMXULPOPUPELEMENT(_to) \
  NS_SCRIPTABLE NS_IMETHOD GetPosition(nsAString & aPosition) { return _to GetPosition(aPosition); } \
  NS_SCRIPTABLE NS_IMETHOD SetPosition(const nsAString & aPosition) { return _to SetPosition(aPosition); } \
  NS_SCRIPTABLE NS_IMETHOD ShowPopup(PRUint16 alignment, nsIDOMElement *target, nsIDOMElement *anchor) { return _to ShowPopup(alignment, target, anchor); } \
  NS_SCRIPTABLE NS_IMETHOD HidePopup(void) { return _to HidePopup(); } 

/* Use this macro to declare functions that forward the behavior of this interface to another object in a safe way. */
#define NS_FORWARD_SAFE_NSIDOMXULPOPUPELEMENT(_to) \
  NS_SCRIPTABLE NS_IMETHOD GetPosition(nsAString & aPosition) { return !_to ? NS_ERROR_NULL_POINTER : _to->GetPosition(aPosition); } \
  NS_SCRIPTABLE NS_IMETHOD SetPosition(const nsAString & aPosition) { return !_to ? NS_ERROR_NULL_POINTER : _to->SetPosition(aPosition); } \
  NS_SCRIPTABLE NS_IMETHOD ShowPopup(PRUint16 alignment, nsIDOMElement *target, nsIDOMElement *anchor) { return !_to ? NS_ERROR_NULL_POINTER : _to->ShowPopup(alignment, target, anchor); } \
  NS_SCRIPTABLE NS_IMETHOD HidePopup(void) { return !_to ? NS_ERROR_NULL_POINTER : _to->HidePopup(); } 

#if 0
/* Use the code below as a template for the implementation class for this interface. */

/* Header file */
class nsDOMXULPopupElement : public nsIDOMXULPopupElement
{
public:
  NS_DECL_ISUPPORTS
  NS_DECL_NSIDOMXULPOPUPELEMENT

  nsDOMXULPopupElement();

private:
  ~nsDOMXULPopupElement();

protected:
  /* additional members */
};

/* Implementation file */
NS_IMPL_ISUPPORTS1(nsDOMXULPopupElement, nsIDOMXULPopupElement)

nsDOMXULPopupElement::nsDOMXULPopupElement()
{
  /* member initializers and constructor code */
}

nsDOMXULPopupElement::~nsDOMXULPopupElement()
{
  /* destructor code */
}

/* attribute DOMString position; */
NS_IMETHODIMP nsDOMXULPopupElement::GetPosition(nsAString & aPosition)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}
NS_IMETHODIMP nsDOMXULPopupElement::SetPosition(const nsAString & aPosition)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void showPopup (in unsigned short alignment, in nsIDOMElement target, in nsIDOMElement anchor); */
NS_IMETHODIMP nsDOMXULPopupElement::ShowPopup(PRUint16 alignment, nsIDOMElement *target, nsIDOMElement *anchor)
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* void hidePopup (); */
NS_IMETHODIMP nsDOMXULPopupElement::HidePopup()
{
    return NS_ERROR_NOT_IMPLEMENTED;
}

/* End of implementation class template. */
#endif


#endif /* __gen_nsIDOMXULPopupElement_h__ */