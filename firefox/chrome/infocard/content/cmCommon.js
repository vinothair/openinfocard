function isClaimChecked(elementId, uri) {
	 var checkbox = document.getElementById(elementId);
	 if (!(checkbox === undefined)) {
icDebug("isClaimChecked: found " + elementId);
		 if (!(checkbox.checked === undefined)) {
icDebug("isClaimChecked: is a checkbox ");
		  if (checkbox.checked) {
icDebug("isClaimChecked: is checked ");
		   if ( uri === undefined ) {
		     return "";
		   } else {
			 return uri;
		   }
		  } else {
icDebug("isClaimChecked: is not checked ");
		  }
		 } else {
		  icDebug( "expected type checkbox, but found: " + typeof(checkbox));
		 } 
	 } else {
	  icDebug("checkbox not defined for uri: " + uri );
	 }
	 return null;
}

