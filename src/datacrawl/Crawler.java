package datacrawl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import database.ArticleDBHandler;
import datamodel.Article;
import datamodel.WordVector;

public class Crawler {
	private String startDate;
	private String endDate;
	final double cutOff = 0.1; //두 기사가 동일한 토픽에 대한 것인지 판단하는 유사도의 기준값
	final int articleNum = 30;

	public Crawler(String startDate, String endDate){
		//날짜의 형식은 YYYYMMDD
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public void crawl(){ //Article의 배열을 반환하는 걸로 교체

		//날짜에 대한 루프를 돌리기 위한 코드
		GregorianCalendar gcal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try{
			ArticleDBHandler adbh = new ArticleDBHandler(); //크롤한 결과를 article table에 저장한다

			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);

			for(gcal.setTime(start);gcal.getTime().before(end) || gcal.getTime().equals(end);gcal.add(Calendar.DAY_OF_YEAR, 1)){

				String date = sdf.format(gcal.getTimeInMillis());
				String url = "http://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=100&date="+date;

				Article[] articlesInDate = new Article[articleNum]; //많이 본 기사는 30위까지 조회 됨. 이 중에서 중복 기사를 쳐 낼 것임.

				Document page = Jsoup.connect(url).timeout(30000).get();
				Element pageBody = page.getElementsByClass("content").first();
				Elements linksWithRedun = pageBody.select("a[href]"); //페이지 안에 있는 링크들을 따온다
				Elements links = new Elements();

				//중복되는 링크 제거
				for(Element link: linksWithRedun){
					int redunChecker = 0;
					for(Element link2: links){
						if(link2.attr("href").equals(link.attr("href"))) redunChecker++;
					}
					if(redunChecker == 0) links.add(link);
				}
				//인기 순위에 나타난 기사들을 하나씩 크롤링
				Document articlePage = new Document("");
				int articleCnt = 0;
				for(Element link: links){
					if(link.toString().contains("main/ranking/read.nhn?mid=etc&amp;sid1=111&amp")){
						String subUrl = link.absUrl("href");
						articlePage = Jsoup.connect(subUrl).timeout(10000).get();
						Element articleTitle = articlePage.select("#articleTitle").first();
						Element articleBody = articlePage.select("#articleBodyContents").first();
						String title = articleTitle.text().replace("'", "''");
						String content = articleBody.text().replaceAll("'", "''"); // SQL에서 '는 ''로 입력해야 함
						articlesInDate[articleCnt] = new Article(title, content, date);
						articleCnt++;
					}
				}

				//일자별로 기사를 길이 30의 배열로 받은 뒤에, 중복되는 기사는 쳐내고 남은 것들만 DB에 저장
				for(int cnt = 0; cnt<articlesInDate.length; cnt++){
					if(articlesInDate[cnt] == null) continue;
					else{
						WordVector articleTermVector = articlesInDate[cnt].getTermVector().topNwords();
						for(int cnt2 = (cnt + 1); cnt2 < articlesInDate.length; cnt2++){
							if(articlesInDate[cnt2] != null){
								double sim = WordVector.jacqSim(articleTermVector,articlesInDate[cnt2].getTermVector().topNwords());
								if(sim > cutOff) articlesInDate[cnt2] = null; //조회수 상위에 랭크된 기사와 거의 같다고 볼 수 있는 하위 랭크의 기사는 제거한다
							}
						}
					}
				}

				for(Article article : articlesInDate){
					if(article != null) adbh.insert(article.getTitle(), article.getContent(), article.getDate()); // 기사를 DB에 저장
				}
			}	
			adbh.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}