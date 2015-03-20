package main;

import database.ArticleDBHandler;
import database.TopicDBHandler;
import datamodel.Article;
import datamodel.Topic;

//토픽별로 어떤 기사들이 들어있는지를 출력한다
public class ResultViewer { 
	public static void main(String[] args){
		ArticleDBHandler adbh = new ArticleDBHandler();
		TopicDBHandler tdbh = new TopicDBHandler();
		Topic[] topics = tdbh.retrieveAllTopics();
		for(Topic topic : topics){
			System.out.println("토픽 : " + adbh.getArticle(topic.getMainArticle()).getTitle());
			Article[] articlesInTopic = adbh.getArticlesInTopic(topic.getIndex());
			for(Article article : articlesInTopic){
				System.out.println(article.getTitle() +" : "+ article.getDate());
			}
			System.out.println();
		}
	}
}
