#
# Copyright (c) 2006, Axel Nennker - nennker.de
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
#     * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
# EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
# DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

BASE_DIR=/opt
BASE_CLASS_DIR=/export/providers
JAVA_HOME=/usr/jdk/entsys-j2se
JSSPATH=/usr/share/lib/mps/secv1/jss4.jar
SDKPATH=${BASE_DIR}/SUNWam/lib/am_sdk.jar

# Variables for key exponent and key output
NICKNAME=Server-Cert
BASE=/opt/SUNWwbsvr
CERTUTIL=$(BASE)/bin/https/admin/bin/certutil
PK12UTIL=$(BASE)/bin/https/admin/bin/pk12util
KEYTOOL=/usr/j2se/bin/keytool
OPENSSL=/usr/sfw/bin/openssl
# Directory where secmod.db resides
ALIAS=/opt/SUNWwbsvr/alias
# Server Prefix
PREFIX="https-n1v1.e1.i3alab.net-n1v1-"


XMLDAPPATH=lib/bcprov-jdk15-133.jar:lib/commons-codec-1.3.jar:lib/commons-httpclient-3.0.jar:lib/lightcrypto.jar:lib/xom-1.1.jar:lib/xmldap.jar
CLASSPATH=.:$(BASE_DIR)/SUNWam/lib/am_services.jar:$(BASE_DIR)/SUNWam/lib/servlet.jar:$(XMLDAPPATH):$(JSSPATH):$(SDKPATH)
JAR_DIR=.


SRCS=\
	LoginInfoCard.java\
	SamplePrincipal.java

# this must not be an absolute path
CLASSES=de/nennker/axel

all: newjar $(NICKNAME).der

$(NICKNAME).der: $(NICKNAME).key
	$(OPENSSL) pkcs8 -topk8 -nocrypt -in $(NICKNAME).key -out $(NICKNAME).der -outform DER

$(NICKNAME).key: $(NICKNAME).p12
	$(OPENSSL) pkcs12 -nocerts -in $(NICKNAME).p12 -out $(NICKNAME).key

$(NICKNAME).p12:
	$(PK12UTIL) -o $(NICKNAME).p12 -n $(NICKNAME) -d $(ALIAS) -P $(PREFIX)

$(CLASSES)/LoginInfoCard.class $(CLASSES)/SamplePrincipal.class: $(SRCS)
	$(JAVA_HOME)/bin/javac -classpath $(CLASSPATH) -d $(BASE_CLASS_DIR) $(SRCS) 

newjar: $(CLASSES)/LoginInfoCard.class $(CLASSES)/SamplePrincipal.class
	cd $(BASE_CLASS_DIR); \
	$(JAVA_HOME)/bin/jar -cf $(JAR_DIR)/LoginInfoCard.jar $(CLASSES)/*.class

deploy:
	cp $(NICKNAME).der $(ALIAS)/$(NICKNAME).der && chmod 400 $(ALIAS)/$(NICKNAME).der
	cp $(JAR_DIR)/LoginInfoCard.jar ${BASE_DIR}/SUNWam/web-src/services/WEB-INF/lib/
	cp $(JAR_DIR)/LoginInfoCard.xml ${BASE_DIR}/SUNWam/web-src/services/config/auth/default/
	cp $(JAR_DIR)/LoginInfoCard.jsp ${BASE_DIR}/SUNWam/web-src/services/config/auth/default/
	cp card.jpg head_logo_mba2.gif head_t.gif ${BASE_DIR}/SUNWam/web-src/services/images/
	#cp $(JSSPATH) ${BASE_DIR}/SUNWwbsvr/bin/https/lib/
	cp lib/xmldap.jar lib/bcprov-jdk15-133.jar lib/commons-codec-1.3.jar lib/commons-httpclient-3.0.jar lib/lightcrypto.jar lib/xom-1.1.jar ${BASE_DIR}/SUNWam/web-src/services/WEB-INF/lib/

clean:
	rm -f $(BASE_CLASS_DIR)/$(CLASSES)/*.class
	rm $(JAR_DIR)/*.jar 
	rm -f $(NICKNAME).p12 $(NICKNAME).der $(NICKNAME).key

dist:
	gtar zcf ../s1am-InfoCardLogin-`date '+%Y%m%d%H%M'`.tar.gz amadmin.xml card.jpg LoginInfoCard.java LoginInfoCard.jsp LoginInfoCard.xml Makefile INSTALL SamplePrincipal.java
