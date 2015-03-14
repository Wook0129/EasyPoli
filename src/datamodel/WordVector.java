package datamodel;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

public class WordVector {
	private int numVoca;
	private HashMap<String,Double> termFreqVector = new HashMap<String, Double>();

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
	
	public String toString(){
		String result = "";
		JSONObject jObject = new JSONObject();
		for(String key : termFreqVector.keySet()){
			jObject.put(key, termFreqVector.get(key));
		}
		result = jObject.toString();
		return result;
	}
	
	public static WordVector toVector(String jsonString){
		WordVector wv = new WordVector();
		
		JSONParser jParser = new JSONParser();
		JSONObject jObject = new JSONObject();
		try {
			jObject = (JSONObject) jParser.parse(jsonString);
		} catch (ParseException e) {e.printStackTrace();}
		
		for(Object key : jObject.keySet()){
			String term = key.toString();
			double freq = Double.parseDouble(jObject.get(key).toString());
			wv.termFreqVector.put(term, freq);
			wv.numVoca++;
		}
		return wv;
	}
	
	public void print(){
		for(String key : termFreqVector.keySet()){
			System.out.println(key + " : "+termFreqVector.get(key));
		}
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
		sim = interSection / (numVoca1 + numVoca2);
		return sim;
	}
	
}