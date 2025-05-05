import regex
import configs
import pandas as pd
import unicodedata

language = 'kn' # change the language.
word='ಸಾಮರ್ಥ್ಯ'



config = configs.language_data #Languges based data
scores = config[language]["score"] # Select weightage data for a particular Language

# This function will return syllables for a given word (ಸಾಮರ್ಥ್ಯ ==> ['ಸಾ', 'ಮ', 'ರ್ಥ್ಯ'])
def find_syllables(word):
    letter = config[language]["regex"]["letter"]
    trailing_letter = config[language]["regex"]["trailing_letter"]
    control = config[language]["regex"]["control"]
    regex_exp= rf'{letter}(?:{control}{letter}|{trailing_letter})*'
    syllables= regex.findall(regex_exp, word)
    return syllables

# This function helps in finding whether a syllable is samyutkashara or not and return the included consonants.
def is_samyukta(syllable) :
    virama = config[language]["virama"]
    consonants = []
    for i, char in enumerate(syllable):
        if (i > 0 and syllable[i - 1] == virama) or (i < len(syllable) - 1 and syllable[i + 1] == virama):
            consonants.append(char)
    return consonants

# This funcrion checks whether a syllable has arkavattu and returns the score.
def add_arkavattu_score(samyukta_arr):
    if(samyukta_arr[0]==config[language]["arkavattu"]): return 1.5
    return 0

def get_score(word):
    addedscores=[]
    word=word.replace(" ", "")
    word = unicodedata.normalize('NFC', word)
    # syllables=regex.findall(r'\X', word)
    syllables=find_syllables(word)
    print(syllables) 
    score=0
    
    if len(syllables)==4: # length of word is 4 add weight of 2
        score+=2
        addedscores.append(2) 
    if len(syllables)>4: # length of word is 4 add weight of 4
        score+=4
        addedscores.append(4)
    # similarities=set()
    for syllable in syllables:
        similarities=set()
        resp=is_samyukta(syllable)
        print(resp)
        # Check for arkavattu in language and add the score of it.
        if("arkavattu" in config[language] and config[language]["arkavattu"] in syllable and len(resp)>0):
            arka_score = add_arkavattu_score(resp)
            if(arka_score >0): 
                addedscores.append(1.5)
                score = score + arka_score
        # Add the Samyutakshra Weightage
        if len(resp)>0:
            if len(resp)==2: #f there are two consonants
                
                if resp[0]==resp[1]: #If they are the same, the Weight_base of that consonant is added. along with additional weight of 1
                    score+=scores[resp[0]]['Weight_base']+1
                    addedscores.append(scores[resp[0]]['Weight_base']+1)
                else: # f they are different, the Weight_base of both consonants is added. along with additional weight of 2
                    score+=scores[resp[0]]['Weight_base']+scores[resp[1]]['Weight_base']+2
                    addedscores.append(scores[resp[0]]['Weight_base']+scores[resp[1]]['Weight_base']+2)
            
            if len(resp)>2: # If there are more than two consonants, the Weight_base of 3 consonants is added along with additional 3 points are added.
                score+=scores[resp[0]]['Weight_base']+scores[resp[1]]['Weight_base']+scores[resp[2]]['Weight_base']+3
                addedscores.append(scores[resp[0]]['Weight_base']+scores[resp[1]]['Weight_base']+scores[resp[2]]['Weight_base']+3)
        
        for  char in syllable:
            score+=scores[char]['Weight'] # Add the individual Char weightage.
            addedscores.append(scores[char]['Weight'])
            
            if scores[char]['similar'] >0 : 
                is_similar = char in similarities
                if not is_similar:
                    score+=0.9 # Add the similar score if applicable.
                    addedscores.append(0.9)
                    similarities.add(char)
    score=round(score,2)
    return score,addedscores


def score(content):
    content = regex.sub(r'[^\w\s]', '', content)
    print(content)
    split_content = content.split()
    final_scores =0
    scores_data = []
    for word in split_content:
        output = get_score(word)
        final_scores+=output[0]
        scores_data.append(output[1])

    return final_scores,scores_data

print(score(word))