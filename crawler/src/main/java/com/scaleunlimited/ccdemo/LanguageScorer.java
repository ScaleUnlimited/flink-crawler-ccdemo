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
import com.scaleunlimited.flinkcrawler.parser.ParserResult;

@SuppressWarnings("serial")
public class LanguageScorer extends BasePageScorer {

	private static String _focusLanguage;
	private transient LanguageDetector _languageDetector;
	private transient TextObjectFactory _textObjectFactory;
	
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
		DetectedLanguage best = result.get(0);
		if (best.getLocale().getLanguage().equals(_focusLanguage)) {
			return 1;
		}
		return 0;
	}

}
