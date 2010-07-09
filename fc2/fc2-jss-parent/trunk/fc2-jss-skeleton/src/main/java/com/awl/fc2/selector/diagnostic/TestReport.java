/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
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
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
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
package com.awl.fc2.selector.diagnostic;

import java.util.Vector;

public class TestReport {

	boolean allGood;
	String name;
	public TestReport(String name) {
		this.name = name;
		allGood = true;
		m_vecProblem = new Vector<Problem>();
	}
	public boolean isAllGood(){
		return allGood;
	}
	Vector<Problem> m_vecProblem;
	public void addProblem(Problem pb){
		m_vecProblem.add(pb);
		allGood = false;
	}
	public void addProblem(String reason,String consequence,String fix){
		m_vecProblem.add(new Problem(reason, consequence, fix));
		allGood = false;
	}
	
	public String toString(){
		String report ="****************** BEGIN "+name+" *****************\n";
		 
		if(allGood){
			report += "|- No problem detected\n";
		}else{
			for(Problem pb:m_vecProblem){
				report += pb+"\n";
			}
		}
		report +="******************  END "+name+"  *****************\n";
		return report;
		
	}
}

