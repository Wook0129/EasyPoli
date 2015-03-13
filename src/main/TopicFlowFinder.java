package main;

import database.ArticleDBHandler;
import datacrawl.Crawler;

public class TopicFlowFinder {
	public static void main(String[] args){
//		Crawler c = new Crawler("20150101","20150102");
//		c.crawl();
		ArticleDBHandler adbh = new ArticleDBHandler();
//		adbh.insert("두번째기사", "두번째내용", "20150103");
//		adbh.select("20150103");
//		adbh.delete("20150101");
//		adbh.update("title", "두번쨰기사", "변경");
		adbh.close();
	}
}
