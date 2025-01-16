package org.ekstep.language.measures;

import org.apache.commons.lang3.StringUtils;
import org.ekstep.language.measures.entity.ComplexityMeasures;
import org.ekstep.language.measures.entity.ParagraphComplexity;
import org.ekstep.language.measures.entity.WordComplexity;
import org.ekstep.language.measures.enums.LanguageParams;
import org.ekstep.language.measures.meta.SyllableMap;
import org.ekstep.language.measures.util.LanguageUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

// TODO: Auto-generated Javadoc

/**
 * The Class ParagraphMeasures.
 *
 * @author karthik
 */
public class ParagraphMeasures {

    /** The word util. */
    static {
        SyllableMap.loadSyllables("te");
        SyllableMap.loadSyllables("hi");
        SyllableMap.loadSyllables("ka");
        SyllableMap.loadSyllables("ta");
        SyllableMap.loadSyllables("gu");
        SyllableMap.loadSyllables("od");
    }


    /**
     * Gets the text complexity.
     *
     * @param language the language
     * @param text     the text
     * @param wordList the word list
     * @return the text complexity
     */
    public static ParagraphComplexity getTextComplexity(String language, String text,
                                                        List<Map<String, Object>> wordList) {
        if (!SyllableMap.isLanguageEnabled(language))
            return null;
        if (StringUtils.isNotBlank(text)) {
            List<String> tokens = LanguageUtil.getTokens(text);
            Map<String, Integer> syllableCountMap = new HashMap<String, Integer>();
            Map<String, Integer> wordFrequency = new HashMap<String, Integer>();
            Map<String, WordComplexity> wordComplexities = new HashMap<String, WordComplexity>();
            Map<String, Double> wcMap = new HashMap<String, Double>();
            Map<String, Map<String, Object>> wordDictonary = null;
            if (wordList != null)
                wordDictonary = wordList.stream()
                        .collect(Collectors.toMap(s -> (String) s.get(LanguageParams.lemma.name()), s -> s));
            if (null != tokens && !tokens.isEmpty()) {
                for (String word : tokens) {
                    WordComplexity wc = WordMeasures.getWordComplexity(language, word);
                    wordComplexities.put(word, wc);
                    Integer count = null == wordFrequency.get(word) ? 0 : wordFrequency.get(word);
                    count += 1;
                    wordFrequency.put(word, count);
                    syllableCountMap.put(word, wc.getCount());
                    if (wordDictonary != null) {
                        Map<String, Object> wordNodeMap = wordDictonary.get(word);
                        Double wordComplexity = null;
                        if (wordNodeMap != null)
                            wordComplexity = (Double) wordNodeMap.get(LanguageParams.word_complexity.name());
                        wcMap.put(word, wordComplexity);
                    }
                }
            }
            ParagraphComplexity pc = new ParagraphComplexity();
            pc.setText(text);
            pc.setWordFrequency(wordFrequency);
            pc.setWordComplexityMap(wcMap);
            pc.setSyllableCountMap(syllableCountMap);
            computeMeans(pc, wordComplexities, wcMap);
            return pc;
        } else {
            return null;
        }
    }

    /**
     * Gets the suitable grade summary info.
     *
     * @param languageId the language id
     * @param value      the value
     * @return the suitable grade summary info
     */
    public static List<Map<String, String>> getSuitableGradeSummaryInfo(String languageId, Double value) {
		/*List<org.ekstep.graph.dac.model.Node> suitableGrade = GradeComplexityCache.getInstance()
				.getSuitableGrades(languageId, value);
		List<Map<String, String>> suitableGradeSummary = new ArrayList<Map<String, String>>();
		if (suitableGrade != null) {
			for (org.ekstep.graph.dac.model.Node sg : suitableGrade) {
				Map<String, String> gradeInfo = new HashMap<>();
				gradeInfo.put("grade", (String) sg.getMetadata().get("gradeLevel"));
				gradeInfo.put("languageLevel", (String) sg.getMetadata().get("languageLevel"));
				suitableGradeSummary.add(gradeInfo);
			}
			return suitableGradeSummary;
		}*/
        return null;
    }

    /**
     * Compute means.
     *
     * @param pc               the pc
     * @param wordComplexities the word complexities
     * @param wcMap            the wc map
     */
    private static void computeMeans(ParagraphComplexity pc, Map<String, WordComplexity> wordComplexities,
                                     Map<String, Double> wcMap) {
        int count = 0;
        double orthoComplexity = 0;
        double phonicComplexity = 0;
        double wordComplexity = 0;
        Map<String, ComplexityMeasures> wordMeasures = new HashMap<String, ComplexityMeasures>();
        for (Entry<String, WordComplexity> entry : wordComplexities.entrySet()) {
            WordComplexity wc = entry.getValue();
            orthoComplexity += wc.getOrthoComplexity();
            phonicComplexity += wc.getPhonicComplexity();
            double wcValue = (null == wcMap.get(entry.getKey()) ? 0 : wcMap.get(entry.getKey()).doubleValue());
            wordComplexity += wcValue;
            count += wc.getCount();
            wordMeasures.put(entry.getKey(), new ComplexityMeasures(wc.getOrthoComplexity(), wc.getPhonicComplexity()));
        }
        pc.setWordCount(wordComplexities.size());
        pc.setSyllableCount(count);
        pc.setWordMeasures(wordMeasures);
        pc.setWordComplexityMap(wcMap);
        pc.setTotalOrthoComplexity(formatDoubleValue(orthoComplexity));
        pc.setTotalPhonicComplexity(formatDoubleValue(phonicComplexity));
        pc.setTotalWordComplexity(formatDoubleValue(wordComplexity));
        pc.setMeanOrthoComplexity(formatDoubleValue(orthoComplexity / count));
        pc.setMeanPhonicComplexity(formatDoubleValue(phonicComplexity / count));
        pc.setMeanWordComplexity(formatDoubleValue(wordComplexity / count));
        double totalComplexity = orthoComplexity + phonicComplexity;
        pc.setMeanComplexity(formatDoubleValue(totalComplexity / pc.getWordCount()));
    }


    /**
     * Update top five.
     *
     * @param result               the result
     * @param words                the words
     * @param wordPosMap           the word pos map
     * @param nonThresholdVocWords the non threshold voc words
     */
    private static void updateTopFive(Map<String, Object> result, List<String> words, Map<String, String> wordPosMap,
                                      List<String> nonThresholdVocWords) {

        Map<String, List<String>> wordsGroupedByPos = wordPosMap.entrySet().stream().collect(
                Collectors.groupingBy(Entry::getValue, Collectors.mapping(Entry::getKey, Collectors.toList())));

        Map<String, Long> wordCountMap = words.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        Map<String, Long> wordCountSortedMap = wordCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Map<String, Object> top5 = new HashMap<>();

        for (Entry<String, List<String>> posEntry : wordsGroupedByPos.entrySet()) {
            //top5.put("noun", getTopFiveOf(wordCountSortedMap, wordsGroupedByPos.get("noun")));
            top5.put(posEntry.getKey(), getTopFiveOf(wordCountSortedMap, posEntry.getValue()));
        }
        top5.put("non-thresholdVocabulary", getTopFiveOf(wordCountSortedMap, nonThresholdVocWords));

        result.put("top5", top5);
    }

    /**
     * Gets the top five of.
     *
     * @param wordCountSortedMap the word count sorted map
     * @param matchWords         the match words
     * @return the top five of
     */
    private static List<String> getTopFiveOf(Map<String, Long> wordCountSortedMap, List<String> matchWords) {
        int count = 0;
        List<String> words = new ArrayList<String>();
        if (matchWords != null && matchWords.size() > 0)
            for (Entry<String, Long> wordEntry : wordCountSortedMap.entrySet()) {
                String word = wordEntry.getKey();
                if (matchWords.contains(word)) {
                    words.add(word);
                    count++;
                    if (count == 5) {
                        break;
                    }
                }
            }

        return words;

    }

    /**
     * Update threshold vocabulary metrics.
     *
     * @param result               the result
     * @param wordList             the word list
     * @param nonThresholdVocWords the non threshold voc words
     */
    private static void updateThresholdVocabularyMetrics(Map<String, Object> result, List<Map<String, Object>> wordList,
                                                         List<String> nonThresholdVocWords) {

        Integer thresholdVocWordCount = 0;
        Integer nonthresholdVocWordCount = 0;

        if (wordList != null) {
            for (Map<String, Object> word : wordList) {
                Object thresholdLevel = word.get(LanguageParams.thresholdLevel.name());
                Object grade = word.get(LanguageParams.grade.name());

                if (grade != null || thresholdLevel != null) {
                    thresholdVocWordCount++;
                } else {
                    nonthresholdVocWordCount++;
                    nonThresholdVocWords.add(word.get(LanguageParams.lemma.name()).toString());
                }
            }

            result.put("thresholdVocabulary", getThresholdVocMap(thresholdVocWordCount, wordList.size()));
            result.put("nonThresholdVocabulary", getThresholdVocMap(nonthresholdVocWordCount, wordList.size()));
        }

    }

    /**
     * Gets the threshold voc map.
     *
     * @param count        the count
     * @param wordListSize the word list size
     * @return the threshold voc map
     */
    private static Map<String, Object> getThresholdVocMap(int count, int wordListSize) {
        Double thresholdPercentage = 0.0;
        thresholdPercentage += count;
        thresholdPercentage = (thresholdPercentage / wordListSize) * 100;
        Map<String, Object> thresholdVocMap = new HashMap<>();
        thresholdVocMap.put("wordCount", count);
        thresholdVocMap.put("%OfWords", formatDoubleValue(thresholdPercentage));
        return thresholdVocMap;
    }


    /**
     * Format double value.
     *
     * @param d the d
     * @return the double
     */
    private static Double formatDoubleValue(Double d) {
        if (null != d) {
            try {
                BigDecimal bd = new BigDecimal(d);
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                return bd.doubleValue();
            } catch (Exception e) {
            }
        }
        return d;
    }
}

@SuppressWarnings("rawtypes")
class ComplexityMeasuresComparator implements Comparator {
    private Map map;

    public ComplexityMeasuresComparator(Map map) {
        this.map = map;
    }

    @Override
    public int compare(Object o1, Object o2) {
        ComplexityMeasures c1 = (ComplexityMeasures) map.get(o1);
        ComplexityMeasures c2 = (ComplexityMeasures) map.get(o2);
        Double complexity1 = c1.getOrthographic_complexity() + c1.getPhonologic_complexity();
        Double complexity2 = c2.getOrthographic_complexity() + c2.getPhonologic_complexity();
        return complexity2.compareTo(complexity1);
    }

}
