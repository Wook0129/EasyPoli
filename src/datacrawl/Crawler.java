package datacrawl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);
			for(gcal.setTime(start);gcal.getTime().before(end);gcal.add(Calendar.DAY_OF_YEAR, 1)){
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
						Element articleBody = articlePage.select("#articleBodyContents").first();
						String content = articleBody.text().split("기자")[1];
						System.out.println(content);
					}
				}
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}