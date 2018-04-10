package com.scaleunlimited.ccdemo;

import java.io.IOException;
import java.util.List;

import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import com.scaleunlimited.flinkcrawler.focused.BasePageScorer;
import com.scaleunlimited.flinkcrawler.metrics.CrawlerAccumulator;
import com.scaleunlimited.flinkcrawler.parser.ParserResult;

@SuppressWarnings("serial")
public class LanguageScorer extends BasePageScorer {

	private String _focusLanguage;
	private transient LanguageDetector _languageDetector;
	private transient TextObjectFactory _textObjectFactory;
	private transient CrawlerAccumulator _crawlerAccumulator;
	
	public LanguageScorer(String focusLanguage) {
		_focusLanguage = focusLanguage;
	}
	
	private void load() {
		try {
			//load all languages:
			List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
			//build language detector:
			_languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
					.withProfiles(languageProfiles)
					.build();
			//create a text object factory
			_textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read in language profiles", e);
		}
	}
	
	@Override
	public float score(ParserResult parserResult) {

		if (_languageDetector == null) {
			load();
		}
		//query:
		TextObject textObject = _textObjectFactory.forText(parserResult.getParsedUrl().getParsedText());
//		Optional<LdLocale> lang = _languageDetector.detect(textObject);
		
		List<DetectedLanguage> result = _languageDetector.getProbabilities(textObject);
		if (!result.isEmpty()) {
			DetectedLanguage best = result.get(0);
			String bestLang = best.getLocale().getLanguage();
			_crawlerAccumulator.increment("LanguageDetector", bestLang, 1);
			if (bestLang.equals(_focusLanguage)) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void close() throws Exception {
		
	}

	@Override
	public void open(CrawlerAccumulator crawlerAccumulator) throws Exception {
		_crawlerAccumulator = crawlerAccumulator;
	}

}
