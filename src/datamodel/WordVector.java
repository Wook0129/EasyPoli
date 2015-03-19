package datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class WordVector {
	private int numVoca;
	private HashMap<String,Double> termFreqVector = new HashMap<String, Double>(10); //초기용량 10

	public WordVector(){
		numVoca = 0;
	}
	public WordVector(String content){ // 생성자에서, content를 받으면 어휘벡터로 만드는 과정
		TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
		List<KoreanTokenizer.KoreanToken> parsed;
		parsed = processor.tokenize(content);
		Object[] parsedResult = parsed.toArray();
		for(Object result : parsedResult){
			putWord(result.toString());
		}
	}

	public void putWord(String word){
		//길이가 1이상인 명사만 사용. (Noun + 한글자 = 5글자)
		if(word.contains("Noun") && !word.contains("*") && word.length() > 5){
			if(!termFreqVector.containsKey(word)){
				termFreqVector.put(word, 1.0);
				numVoca++;
			}
			else{termFreqVector.put(word, termFreqVector.get(word) + 1.0);}
		}
	}

	public int getNumOfVoca(){
		return numVoca;
	}

	public String[] getTermVector(){
		String[] termSet = new String[numVoca];

		for(int cnt = 0; cnt< termFreqVector.keySet().size(); cnt++){
			String key = termFreqVector.keySet().toArray()[cnt].toString();
			termSet[cnt] = key;
		}
		return termSet;
	}

	public double[] getFreqVector(){
		double[] freqVector = new double[numVoca];

		for(int cnt = 0; cnt< numVoca; cnt++){
			double value = termFreqVector.get(termFreqVector.keySet().toArray()[cnt]);
			freqVector[cnt] = value;
		}
		return freqVector;
	}

	public void print(){
		for(String key : termFreqVector.keySet()){
			System.out.println(key + " : "+termFreqVector.get(key));
		}
	}
	public WordVector topNwords(){
		WordVector topN = new WordVector();
		ArrayList as = new ArrayList(termFreqVector.entrySet());

		Collections.sort( as , new Comparator() {
			public int compare( Object o1 , Object o2 )
			{
				Map.Entry e1 = (Map.Entry)o1 ;
				Map.Entry e2 = (Map.Entry)o2 ;
				double first = (double)e1.getValue();
				double second = (double)e2.getValue();
				Integer c1 = (int)first;
				Integer c2 = (int)second;
				return c1.compareTo( c2 );
			}
		});
		Object[] sortedArray = as.toArray();
		//문서 내에서 출현 빈도가 높은 단어의 20 순위까지 본다
		final int termRank = Math.max(0, sortedArray.length - 10);
		for(int cnt = sortedArray.length - 1; cnt >= termRank; cnt--)
		{
				String word = sortedArray[cnt].toString().split("=")[0];
				double freq = Double.parseDouble(sortedArray[cnt].toString().split("=")[1]);
				topN.termFreqVector.put(word, freq);
				topN.numVoca++;
		}
		return topN;
	}
	//일단 자카드 계수 쓸거임. 그런데.. 정확하게 일치하지않는 단어라도 동의어가 있을 수 있음. 그런애들을 반영할 방법은 없을까? 사전 등 이용?
	public static double jacqSim(WordVector vector1, WordVector vector2){
		double sim = 0;
		double numVoca1 = vector1.getNumOfVoca();
		double numVoca2 = vector2.getNumOfVoca();
		double interSection = 0;
		String[] termVector1 = vector1.getTermVector();
		String[] termVector2 = vector2.getTermVector();
		for(String term1 : termVector1){
			for(String term2 : termVector2){
				if(term1.equals(term2)) interSection++;
			}
		}
		sim = interSection / (numVoca1 + numVoca2 - interSection);
		return sim;
	}
	
	public static double modifiedJacqSim(WordVector vector1, WordVector vector2){
		double sim = 0;
		double numVoca1 = vector1.getNumOfVoca();
		double numVoca2 = vector2.getNumOfVoca();
		String[] termVector1 = vector1.getTermVector();
		String[] termVector2 = vector2.getTermVector();
		double[] freqVector1 = vector1.getFreqVector();
		double[] freqVector2 = vector2.getFreqVector();
		for(int cnt=0; cnt<termVector1.length; cnt++){
			for(int cnt2=0; cnt2<termVector2.length; cnt2++){
				if(termVector1[cnt].equals(termVector2[cnt2])) {
					sim += (freqVector1[cnt]/numVoca1 + freqVector2[cnt2]/numVoca2);
					break;
				}
			}
		}
		return sim;
	}
	
}