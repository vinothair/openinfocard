package com.awl.fc2.plugin.store.local;


import java.util.List;
import java.util.Vector;

import org.xmldap.infocard.InfoCard;
import org.xmldap.infocard.policy.SupportedClaim;

import com.awl.fc2.selector.query.CompatibleInfoCards;

class MatrixAttvsCard{
	Vector<InfoCard> originalSet;
	CompatibleInfoCards aSet = new CompatibleInfoCards();
	boolean matrix[][];
	boolean importCard[];
	int cptCardForAttribute[];
	int nbCards;
	int nbAtt;
	public MatrixAttvsCard(Vector<String> att,Vector<InfoCard> cards) {
		originalSet = cards;
		fillWithData(att, cards);			
	}
	public void selectAllCard(){
		for(int j=0;j<nbCards;j++){
			importCard[j] = true;
		}
	}
	public void initMatrix(){
		matrix = new boolean[nbAtt][nbCards];
		importCard = new boolean[nbCards];
		cptCardForAttribute = new int[nbAtt];
		selectAllCard();
		for(int i = 0;i<nbAtt;i++){
			cptCardForAttribute[i] = 0;
			for(int j=0;j<nbCards;j++){
				matrix[i][j] = false;
			}
		}
	}
	public void fillWithData(Vector<String> att,Vector<InfoCard> cards){
		nbAtt = att.size();
		nbCards = cards.size();
		initMatrix();
		
		for( int l_iatt = 0;l_iatt <nbAtt;l_iatt++){
			String curAtt = att.get(l_iatt);
			for(int l_iCards =0;l_iCards < nbCards; l_iCards++){
				
				List<SupportedClaim> lst = cards.get(l_iCards).getClaimList().getSupportedClaims();
				for(int i=0;i<lst.size();i++){				
					if(curAtt.contains(lst.get(i).getURI())){
						matrix[l_iatt][l_iCards] =true;
						cptCardForAttribute[l_iatt]++;
						break;
					}
				}														
			}								
		}			
	}
	public void computeCPT(){
		for(int i = 0;i<nbAtt;i++){
			cptCardForAttribute[i] = 0;
			for(int j=0;j<nbCards;j++){
				if(matrix[i][j] && importCard[j]){
					cptCardForAttribute[i]++;
				}
			}
		}
	}
	public boolean isSatisifying(){
		
		for(int i = 0;i<nbAtt;i++){
			if(cptCardForAttribute[i] == 0) return false;			
		}
		return true;
	}
	
	public CompatibleInfoCards findASet(){
		selectAllCard();
		computeCPT();
		if(isSatisifying()){
			System.out.println(getMatrix());
			// It exists at least a solution
			// Find the cards we cant remove
			int tabForcedCards[] = new int[nbCards];
			
			for(int i=0;i<nbCards;i++) {tabForcedCards[i]=-1;}
			for(int i=0;i<nbAtt;i++){
				if(cptCardForAttribute[i]==1){
					for(int j=0;j<nbCards;j++){
						if(matrix[i][j]==true){
							tabForcedCards[j] = 1;							
						}
					}
				}
			}
			//
			for(int c=0;c<nbCards;c++){
				System.out.println(getMatrix());
				if(tabForcedCards[c]!=1){
					// Try to remove the c^th cards
					importCard[c] = false;
					computeCPT();
					if(isSatisifying()){
						//we should allways be here
						for(int i=0;i<nbAtt;i++){
							if(cptCardForAttribute[i]==1){
								for(int j=0;j<nbCards;j++){
									if(matrix[i][j]==true && importCard[j] == true){
										tabForcedCards[j] = 1;							
									}
								}
							}
						}						
					}else{
						System.out.println("PAS NORMAL");
						importCard[c] = true;
					}									
				}
			}
			
			for(int i =0;i< nbCards;i++){
				if(tabForcedCards[i] == 1){
					aSet.addInfoCard(originalSet.get(i));
				}
			}
			return aSet;
		}
		return null;
	}
	public String getMatrix(){
		String res= "-----------\n";
		for(int i = 0;i<nbAtt;i++){			
			for(int j=0;j<nbCards;j++){
				//if(matrix[i][j] && importCard[j]){
					res += ((matrix[i][j]&& importCard[j])?"x":"o")+",\t";
				//}
				
			}
			res +="\n";
		}
		
		res +="------------";
		return res;
	}
	
	
}