package ner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;

import datamodel.WordVector;

public class NERTagger {

	private static double cutOff=0.56;
	private static double prLength=1;
	private static double prLastName=1;
	private static double prDic=1.0;
	private static String[] dic;
	
	public NERTagger(){
		
		//Initialize With Making Dictionary
		
		FileReader dicFileReader;
		BufferedReader dicReader;
		try {
			dicFileReader = new FileReader("C://Users/태욱/workspace/Easypoli/data/체언_상세.txt");
			dicReader = new BufferedReader(dicFileReader);

			//국어사전
			Vector<String> v = new Vector<String>();
			while(true){
				final String word = dicReader.readLine();
				if(word == null) break;
				else{
					if(word.length() > 1) v.add(word);
				}
			}
			Object[] o = v.toArray();
			dic = new String[o.length];
			for(int cnt = 0; cnt<dic.length; cnt++){
				dic[cnt] = (String)o[cnt];
			}
			dicReader.close();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println(">>>>>Complete making Dictionary");
	}


	public WordVector getNames(String content){

		WordVector result = new WordVector();

		TwitterKoreanProcessorJava processor = new TwitterKoreanProcessorJava.Builder().build();
		List<KoreanTokenizer.KoreanToken> parsed;

		parsed = processor.tokenize(content);

		Object[] parsedResult = parsed.toArray();

		// 명사 아닌 것들 제외
		String[] nouns = new String[parsedResult.length];

		for(int cnt=0; cnt<parsedResult.length; cnt++){

			if(parsedResult[cnt].toString().contains("Noun")){
				nouns[cnt] = parsedResult[cnt].toString();
				String noun = nouns[cnt].replaceAll("Noun", "").replaceAll("\\*","");

				double probLength = 0;
				double probLastName = 0;
				double probNotDicWord = 0;
				double probName = 0;

				//규칙 기반 접근 방법

				//세글자 단어 뒤에 직책이 따라온다면 그 단어는 이름
				if((noun.length() == 3) && ((cnt+1)<parsedResult.length)){

					if(parsedResult[cnt+1].toString().contains("의원") || parsedResult[cnt+1].toString().contains("대표")
							||parsedResult[cnt+1].toString().contains("대통령")||parsedResult[cnt+1].toString().contains("의장")
							||parsedResult[cnt+1].toString().contains("총리")||parsedResult[cnt+1].toString().contains("수석")
							||parsedResult[cnt+1].toString().contains("특보")||parsedResult[cnt+1].toString().contains("후보")){
						probName = 1;
					}
					//기자는 빼준다
					else if((cnt+2)<parsedResult.length && parsedResult[cnt+2].toString().contains("기자")){
						probName = 0;
					}
				}

				//같은 글자가 연속으로 오는 이름은 거의 없다
				else if(noun.length()>=3 &&
						(noun.toCharArray()[0] == noun.toCharArray()[1] || noun.toCharArray()[1] == noun.toCharArray()[2])){
					probName = 0;
				}

				//한국어에서는 이름 다음 바로 동사가 오는 경우는 없다
				else if((cnt+2)<parsedResult.length &&
						parsedResult[cnt+1].toString().contains("Verb")){
					probName = 0;
				}

				//이름 다음 곧바로 인용하지 않는다(조사나 다른 것이 먼저 옴)
				else if((cnt+2)<parsedResult.length &&
						(parsedResult[cnt+1].toString().contains("'")||parsedResult[cnt+1].toString().contains("\""))){
					probName = 0;
				}

				// 김태욱 (25)와 같은 형태로 나오면 이름으로 처리
				else if((noun.length() == 3) && ((cnt+4)<= parsedResult.length) &&
						parsedResult[cnt+1] != null && parsedResult[cnt+2] != null && parsedResult[cnt+3] != null &&
						parsedResult[cnt+1].toString().contains("(") && parsedResult[cnt+2].toString().contains("Number")
						&& parsedResult[cnt+3].toString().contains(")")){
					probName = 1;
				}

				//통계적 접근방법
				else{

					//글자 길이 체크
					if(noun.length()==3){
						probLength = prLength;
					}
					else if(noun.length()==2||noun.length()==4){
						probLength = 1 - prLength;
					}
					else{
						probLength = 0;
					}

					//!--우리나라 성씨(주요)--!
					if((noun.startsWith("김")||noun.startsWith("이")||noun.startsWith("박")
							||noun.startsWith("최")||noun.startsWith("정")||noun.startsWith("강")
							||noun.startsWith("조")||noun.startsWith("윤")||noun.startsWith("장")
							||noun.startsWith("임")||noun.startsWith("오")||noun.startsWith("한")
							||noun.startsWith("신")||noun.startsWith("서")||noun.startsWith("권")
							||noun.startsWith("황")||noun.startsWith("안")||noun.startsWith("송")
							||noun.startsWith("류")||noun.startsWith("홍")||noun.startsWith("전")
							||noun.startsWith("고")||noun.startsWith("문")||noun.startsWith("손")
							||noun.startsWith("양")||noun.startsWith("배")||noun.startsWith("백")
							||noun.startsWith("조")||noun.startsWith("허")||noun.startsWith("남")
							||noun.startsWith("심")||noun.startsWith("유")||noun.startsWith("노")
							||noun.startsWith("하")||noun.startsWith("전")||noun.startsWith("정")
							||noun.startsWith("곽")||noun.startsWith("성")||noun.startsWith("차")
							||noun.startsWith("유")||noun.startsWith("구")||noun.startsWith("우")
							||noun.startsWith("주")||noun.startsWith("임")||noun.startsWith("나")
							||noun.startsWith("신")||noun.startsWith("민")||noun.startsWith("진")
							||noun.startsWith("지")||noun.startsWith("엄")||noun.startsWith("원")
							||noun.startsWith("채"))){

						probLastName = prLastName;
					}
					else{
						probLastName = 1 - prLastName;
					}

					//단어가 사전에 나오는 단어를 포함할 경우 이름이 아닐 확률이 높다
					probNotDicWord = prDic;

					for(int diccnt=0; diccnt<dic.length; diccnt++){
						if(noun.contains(dic[diccnt])) {
							probNotDicWord = 1 - prDic;
						}
					}
					//확률 계산
					probName = Math.pow((probLength * probLastName * probNotDicWord), 1.0/3);
				}
				if(probName > cutOff){
					//FIXME : putWord 메서드에서 Noun이 있는 것만 처리함. 그래서 다시 붙여줌. 고칠 필요가 있어보인다
					result.putWord(noun+"Noun");
				}
			}
		}
		return result;
	}
}
