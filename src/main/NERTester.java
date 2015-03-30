package main;

import database.ArticleDBHandler;
import datamodel.Article;
import datamodel.WordVector;
import ner.NERTagger;

public class NERTester {
	public static void main(String[] args){
		NERTagger tagger = new NERTagger();
		ArticleDBHandler adbh = new ArticleDBHandler();
		Article[] articles = adbh.getAllArticles();
		for(Article a : articles){	
			WordVector personVector = tagger.getNames(a.getTitle() + " " + a.getContent());
			System.out.println(a.getTitle() + " : " + personVector.terms());
		}
	}
}
