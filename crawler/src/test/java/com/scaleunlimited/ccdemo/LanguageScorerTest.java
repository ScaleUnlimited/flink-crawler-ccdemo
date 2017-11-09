package com.scaleunlimited.ccdemo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.scaleunlimited.flinkcrawler.parser.ParserResult;
import com.scaleunlimited.flinkcrawler.pojos.ParsedUrl;
import com.scaleunlimited.flinkcrawler.pojos.ValidUrl;

public class LanguageScorerTest {

	@Test
	public void test() throws Exception {
		
		// Create parse object with farsi text
		
		String farsiText = "همه دارای عقل و وجدان میباشند و باید نسبت بیکدیگر با روح برادری رفتار کنند";
		String arabicText = "الكرامة والحقوق. وقد وهبوا عقلاً وضميرًا وعليهم أن يعامل بعضهم بعضًا بروح الإخاء";
		
		String mixedFarsiText = "همه دارای عقل و وجدان میباشند و باید نسبت بیکدیگر باand this is English روح برادری رفتار کنند";
		LanguageScorer langScorer = new LanguageScorer();
		
		ValidUrl url = new ValidUrl("http://foo.com");
		ParsedUrl parsedUrl = new ParsedUrl(url, farsiText, null, null, null, 0);
		ParserResult parserResult = new ParserResult(parsedUrl, null);
		assertEquals(1.0, langScorer.score(parserResult), 0.01);
		
		parsedUrl.setParsedText(arabicText);
		assertEquals(0, langScorer.score(parserResult), 0.01);

		parsedUrl.setParsedText(mixedFarsiText);
		assertEquals(1, langScorer.score(parserResult), 0.01);

	}
}
