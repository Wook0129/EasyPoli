package main;

import database.ArticleDBHandler;
import database.TopicDBHandler;
import datamodel.Article;

//토픽별로 어떤 기사들이 들어있는지를 출력한다
public class ResultViewer { 
	public static void main(String[] args){
		ArticleDBHandler adbh = new ArticleDBHandler();
		TopicDBHandler tdbh = new TopicDBHandler();
		String[] topicIndex = tdbh.getAllTopicIndex();
		for(String index : topicIndex){
			System.out.println("토픽 : " + adbh.getArticle(tdbh.getMainArticleOfTopic(index)).getTitle()); //Title of Main Article of the topic
			Article[] articlesInTopic = adbh.getArticlesInTopic(index);
			for(Article article : articlesInTopic){
				System.out.println(article.getTitle() +" : "+ article.getDate());
			}
			System.out.println();
		}
		adbh.close();
		tdbh.close();
	}
}
