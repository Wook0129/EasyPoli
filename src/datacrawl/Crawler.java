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
import database.StructuredArticleDBHandler;
import datamodel.Article;
import datamodel.StructuredArticle;

public class Crawler {
	private String startDate;
	private String endDate;

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
			StructuredArticleDBHandler sadbh = new StructuredArticleDBHandler();
			
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);
			for(gcal.setTime(start);gcal.getTime().before(end) || gcal.getTime().equals(end);gcal.add(Calendar.DAY_OF_YEAR, 1)){
				String date = sdf.format(gcal.getTimeInMillis());

				String url = "http://news.naver.com/main/ranking/popularDay.nhn?rankingType=popular_day&sectionId=100&date="+date;

				Document page = Jsoup.connect(url).timeout(3000).get();
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
				for(Element link: links){
					if(link.toString().contains("main/ranking/read.nhn?mid=etc&amp;sid1=111&amp")){
						String subUrl = link.absUrl("href");
						articlePage = Jsoup.connect(subUrl).timeout(3000).get();
						Element articleTitle = articlePage.select("#articleTitle").first();
						Element articleBody = articlePage.select("#articleBodyContents").first();
						String title = articleTitle.text().replace("'", "''");
						String content = articleBody.text().replaceAll("'", "''"); // SQL에서 '는 ''로 입력해야 함
						adbh.insert(title, content, date); // 기사를 DB에 저장
						// 구조화하고 저장
						StructuredArticle sArticle = new StructuredArticle(new Article(title,content,date));
						sadbh.insert(sArticle.getTermVector(), "0", sArticle.getDate());
					}
				}
			}
			
			adbh.close();
			sadbh.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}